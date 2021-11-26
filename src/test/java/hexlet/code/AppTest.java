package hexlet.code;

import hexlet.code.controllers.UrlController;
import hexlet.code.model.Url;
import hexlet.code.model.query.QUrl;
import io.ebean.DB;
import io.ebean.Transaction;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class AppTest {

    private static Javalin app;
    private static String baseUrl;
    private static Transaction transaction;
    private static Url existingUrl;

    @BeforeAll
    public static void beforeAll() {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;

        existingUrl = new Url("https://www.ag.ru");
        existingUrl.save();
    }

    @AfterAll
    public static void afterAll() {
        app.stop();
    }

    @BeforeEach
    public void beforeEach() {
        transaction = DB.beginTransaction();


    }

    @AfterEach
    public void afterEach() {
        transaction.rollback();
    }

    @Test
    void rootTest() {
        HttpResponse<String> response = Unirest.get(baseUrl).asString();
        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("Page Analyzer"));
    }

    @Test
    void getUrlsTest() {
        HttpResponse<String> response = Unirest
                .get(baseUrl + "/urls")
                .asString();

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains(existingUrl.getName()));
    }

    @Test
    void postUrlsTest() {
        String newAdress = "https://baeldung.com";
        HttpResponse<String> responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("url", newAdress)
                .asString();

        assertEquals(302, responsePost.getStatus());
        assertEquals("/urls", responsePost.getHeaders().getFirst("Location"));

        HttpResponse<String> response = Unirest
                .get(baseUrl + "/urls")
                .asString();
        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains(newAdress));
        assertTrue(response.getBody().contains("Страница успешно добавлена"));

        Url actual = new QUrl()
                .name.equalTo(newAdress)
                .findOne();

        assertNotNull(actual);
        assertEquals(newAdress, actual.getName());
    }

    @Test
    void getUrlsIdTest() {
        HttpResponse<String> response = Unirest
                .get(baseUrl + "/urls/" + existingUrl.getId())
                .asString();

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains(existingUrl.getName()));
    }

    @Test
    void getDomainTest() {
        String actual = UrlController.getDomain("https://some-domain.org/example/path");
        assertEquals("https://some-domain.org", actual);

        String actual1 = UrlController.getDomain("https://some-domain.org:8080/example/path");
        assertEquals("https://some-domain.org:8080", actual1);
    }
}
