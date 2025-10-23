import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.api.AuthApi;
import stellarburgers.api.UserApi;
import stellarburgers.models.User;

import static org.hamcrest.Matchers.*;

public class AuthTest {
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        user = new User("test-auth-" + System.currentTimeMillis() + "@yandex.ru",
                "password", "TestUser");

        Response response = UserApi.createUser(user);
        accessToken = response.path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            UserApi.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Вход под существующим пользователем")
    public void testLoginWithValidCredentials() {
        Response response = AuthApi.login(user);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Вход с неверным email")
    public void testLoginWithInvalidEmail() {
        User invalidUser = new User("invalid@yandex.ru", user.getPassword(), user.getName());

        Response response = AuthApi.login(invalidUser);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Вход с неверным паролем")
    public void testLoginWithInvalidPassword() {
        User invalidUser = new User(user.getEmail(), "wrongpassword", user.getName());

        Response response = AuthApi.login(invalidUser);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
