package stellarburgers.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import stellarburgers.models.User;
import static io.restassured.RestAssured.given;

public class AuthApi {
    private static final String BASE_URL = "https://stellarburgers.education-services.ru/api";

    @Step("Логин пользователя")
    public static Response login(User user) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .body(user)
                .when()
                .post("/auth/login");
    }
}
