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
}
