package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.query.QUrl;
import io.ebean.DB;
import io.ebean.Transaction;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static hexlet.code.Utils.getDomain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class AppTest {

    private static Javalin app;
    private static String baseUrl;
    private static Transaction transaction;
    private static Url existingUrl;
    private static MockWebServer server;

    @BeforeAll
    public static void beforeAll() throws IOException {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;

        server = new MockWebServer();
        String mockBody = Files.readString(new File("src/test/resources/bodytest.html").toPath());
        MockResponse response = new MockResponse().setBody(mockBody);

        server.enqueue(response);
        server.start();

        existingUrl = new Url("https://www.ag.ru");
        existingUrl.save();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        server.shutdown();
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
    void checkUrlTest() {
        String mockUrl = server.url("/").toString();

        // create page for mock site
        Unirest.post(baseUrl + "/urls")
                .field("url", mockUrl)
                .asEmpty();

        // get our Url instance (and his id)
        mockUrl = mockUrl.substring(0, mockUrl.length() - 1);
        Url actualUrl = new QUrl()
                .name.equalTo(mockUrl)
                .findOne();

        // make a post method - check url
        HttpResponse<String> responsePost = Unirest
                .post(baseUrl + "/urls/" + actualUrl.getId() + "/checks")
                .asString();

        assertEquals(302, responsePost.getStatus());
        assertEquals("/urls/" + actualUrl.getId(),
                responsePost.getHeaders().getFirst("Location"));

        // get a urls/id page
        HttpResponse<String> response = Unirest
                .get(baseUrl + "/urls/" + actualUrl.getId())
                .asString();

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("GitHub: Where the world builds software · GitHub")); // Title
        assertTrue(response.getBody().contains("Where the world builds software ")); // h1


    }

    @Test
    void getDomainTest() {
        String actual = getDomain("https://some-domain.org/example/path");
        assertEquals("https://some-domain.org", actual);

        String actual1 = getDomain("https://some-domain.org:8080/example/path");
        assertEquals("https://some-domain.org:8080", actual1);
    }
}
