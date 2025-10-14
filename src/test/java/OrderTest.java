import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.api.OrderApi;
import stellarburgers.api.UserApi;
import stellarburgers.models.Order;
import stellarburgers.models.User;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.*;

public class OrderTest {
    private User user;
    private String accessToken;
    private List<String> validIngredients;

    @Before
    public void setUp() {
        user = new User("test-order-" + System.currentTimeMillis() + "@yandex.ru",
                "password", "TestUser");

        Response response = UserApi.createUser(user);
        accessToken = response.path("accessToken");

        Response ingredientsResponse = OrderApi.getIngredients();
        validIngredients = ingredientsResponse.path("data._id");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            UserApi.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void testCreateOrderWithAuth() {
        Order order = new Order(Arrays.asList(validIngredients.get(0), validIngredients.get(1)));

        Response response = OrderApi.createOrder(order, accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue())
                .body("name", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void testCreateOrderWithoutAuth() {
        Order order = new Order(Arrays.asList(validIngredients.get(0), validIngredients.get(1)));

        Response response = OrderApi.createOrder(order, null);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue())
                .body("name", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами")
    public void testCreateOrderWithIngredients() {
        Order order = new Order(Arrays.asList(validIngredients.get(0), validIngredients.get(1)));

        Response response = OrderApi.createOrder(order, accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void testCreateOrderWithoutIngredients() {
        Order order = new Order(Arrays.asList());

        Response response = OrderApi.createOrder(order, accessToken);

        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void testCreateOrderWithInvalidIngredientHash() {
        Order order = new Order(Arrays.asList("invalid_hash_1", "invalid_hash_2"));

        Response response = OrderApi.createOrder(order, accessToken);

        response.then()
                .statusCode(500);
    }

    @Test
    @DisplayName("Получение заказов пользователя")
    public void testGetUserOrders() {
        Order order = new Order(Arrays.asList(validIngredients.get(0), validIngredients.get(1)));
        OrderApi.createOrder(order, accessToken);

        Response response = OrderApi.getUserOrders(accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }
}
