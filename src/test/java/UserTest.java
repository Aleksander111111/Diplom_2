import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.api.UserApi;
import stellarburgers.models.User;
import static org.hamcrest.Matchers.*;

public class UserTest {
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        user = new User("test-user-" + System.currentTimeMillis() + "@yandex.ru",
                "password", "TestUser");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            UserApi.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    public void testCreateUniqueUser() {
        Response response = UserApi.createUser(user);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());

        accessToken = response.path("accessToken");
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void testCreateExistingUser() {
        Response createResponse = UserApi.createUser(user);
        accessToken = createResponse.path("accessToken");

        Response response = UserApi.createUser(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    public void testCreateUserWithoutEmail() {
        User userWithoutEmail = new User("", "password", "TestUser");

        Response response = UserApi.createUser(userWithoutEmail);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    public void testCreateUserWithoutPassword() {
        User userWithoutPassword = new User("test@yandex.ru", "", "TestUser");

        Response response = UserApi.createUser(userWithoutPassword);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    public void testCreateUserWithoutName() {
        User userWithoutName = new User("test@yandex.ru", "password", "");

        Response response = UserApi.createUser(userWithoutName);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
