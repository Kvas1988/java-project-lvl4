package hexlet.code.controllers;

import io.javalin.http.Handler;

import hexlet.code.model.Url;
import hexlet.code.model.query.QUrl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public final class UrlController {

    public static Handler createUrl = ctx -> {
        String url = ctx.formParam("url");

        url = getDomain(url);
        if (url == null) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.redirect("/urls");
            return;
        }

        Url checkUrl = new QUrl()
                .name.equalTo(url)
                .findOne();

        if (checkUrl != null) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.redirect("/urls");
            return;
        }

        Url newUrl = new Url(url);
        newUrl.save();
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.redirect("/urls");
    };

    public static Handler urlList = ctx -> {
        List<Url> urls = new QUrl()
                .orderBy()
                    .id.asc()
                .findList();

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

    public static String getDomain(String url) {
        URL u = null;
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            return null;
        }

        String domain = u.getProtocol() + "://" + u.getHost();
        int port = u.getPort();
        if (port != -1) {
            domain += ":" + port;
        }
        return domain;
    }
}
