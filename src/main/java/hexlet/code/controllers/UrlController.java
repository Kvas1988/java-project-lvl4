package hexlet.code.controllers;

import hexlet.code.model.UrlCheck;
import io.javalin.http.Handler;

import hexlet.code.model.Url;
import hexlet.code.model.query.QUrl;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static hexlet.code.Utils.getDomain;

public final class UrlController {

    static final Logger LOGGER = LoggerFactory.getLogger(UrlController.class);

    public static Handler createUrl = ctx -> {
        String url = ctx.formParam("url");
        LOGGER.info("got url from form");

        url = getDomain(url);
        if (url == null) {
            LOGGER.warn("invalid address given");
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.status(400).redirect("/urls");
            return;
        }

        Url checkUrl = new QUrl()
                .name.equalTo(url)
                .findOne();

        if (checkUrl != null) {
            LOGGER.info("Address already exists");
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.redirect("/urls");
            return;
        }

        Url newUrl = new Url(url);
        newUrl.save();
        LOGGER.info("new url saved to DB");
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.redirect("/urls");
    };

    public static Handler urlList = ctx -> {
        List<Url> urls = new QUrl()
                .orderBy()
                    .id.asc()
                .findList();
        LOGGER.info("got url list from db");

        ctx.attribute("urls", urls);
        ctx.render("urls/index.html");
    };

    public static Handler showUrl = ctx -> {
        int id = ctx.pathParamAsClass("id", int.class).getOrDefault(null);

        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        if (url == null) {
            ctx.status(404).result("404. Not Found");
            return;
        }

        ctx.attribute("url", url);
        ctx.render("urls/show.html");
    };


    public static Handler checkUrl = ctx -> {
        long id = ctx.pathParamAsClass("id", long.class).getOrDefault(null);
        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        if (url == null) {
            ctx.sessionAttribute("flash", "Invalid url");
            ctx.redirect("/urls");
            return;
        }

        HttpResponse<String> response = null;
        try {
            response = Unirest
                    .get(url.getName())
                    .asString();
        } catch (Exception e) {
            ctx.sessionAttribute("flash", e.getMessage());
            ctx.redirect("/urls/" + id);
            return;
        }

        // Parse body
        Document doc = Jsoup.parse(response.getBody());
        String title = doc.title();
        String h1 = doc.selectFirst("h1") != null
                ? doc.selectFirst("h1").text()
                : "";
        String description = doc.selectFirst("meta[name=description]") != null
                ? doc.selectFirst("meta[name=description]").attr("content")
                : "";


        UrlCheck urlCheck = new UrlCheck(
                response.getStatus(),
                title,
                h1,
                description,
                url
        );

        urlCheck.save();
        ctx.sessionAttribute("flash", "Страница успешно проверена");
        ctx.redirect("/urls/" + id);
    };

}
