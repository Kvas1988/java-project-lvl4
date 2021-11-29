package hexlet.code;

import java.net.MalformedURLException;
import java.net.URL;

public class Utils {
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

    public static String getContentFromBody(String body, String begin, String end) {
        String[] s = body.split(begin);

        if (s.length == 1) {
            return null;
        }

        return s[1].split(end)[0];
    }
}
