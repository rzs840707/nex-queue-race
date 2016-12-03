package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by lrkin on 2016/12/3.
 */
public class WebUtil {
    private final static Logger log = LoggerFactory.getLogger(WebUtil.class);

    /**
     * Headers about client's IP
     */
    private static final String[] HEADERS_ABOUT_CLIENT_IP = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    private WebUtil() {
    }

    /**
     * get client ip
     *
     * @param request
     * @return
     */
    public static String getClientIpAddr(HttpServletRequest request) {
        for (String header : HEADERS_ABOUT_CLIENT_IP) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }


    public static void failureCookie(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String sessionCookieName, String cookieDomain, String cookieContextPath) {
    }
}
