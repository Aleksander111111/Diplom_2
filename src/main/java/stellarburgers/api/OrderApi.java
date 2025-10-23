package stellarburgers.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import stellarburgers.models.Order;
import static io.restassured.RestAssured.given;

public class OrderApi {
    private static final String BASE_URL = "https://stellarburgers.education-services.ru/api";

    @Step("Получение ингредиентов")
    public static Response getIngredients() {
        return given()
                .baseUri(BASE_URL)
                .when()
                .get("/ingredients");
    }

    @Step("Создание заказа")
    public static Response createOrder(Order order, String accessToken) {
        if (accessToken != null) {
            return given()
                    .header("Content-type", "application/json")
                    .header("Authorization", accessToken)
                    .baseUri(BASE_URL)
                    .body(order)
                    .when()
                    .post("/orders");
        } else {
            return given()
                    .header("Content-type", "application/json")
                    .baseUri(BASE_URL)
                    .body(order)
                    .when()
                    .post("/orders");
        }
    }

    @Step("Получение заказов пользователя")
    public static Response getUserOrders(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .baseUri(BASE_URL)
                .when()
                .get("/orders");
    }
}
