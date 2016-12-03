package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    /**
     * find cookie from request
     *
     * @param request
     * @param name
     * @return
     */
    public static Cookie findCookie(HttpServletRequest request, String name) {
        if (request != null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(name)) {
                        return cookie;
                    }
                }
            }
        }
        return null;
    }

    /**
     * find cookie value
     *
     * @param request
     * @param name
     * @return
     */
    public static String findCookieValue(HttpServletRequest request, String name) {
        Cookie cookie = findCookie(request, name);
        return cookie != null ? cookie.getValue() : null;
    }

    public static void addCookie(HttpServletRequest request, HttpServletResponse response,
                                 String name, String value, String domain, int maxAge, boolean httpOnly) {
        String contextPath = request.getContextPath();
        if (contextPath == null || contextPath.isEmpty()) {
            contextPath = "/";
        }
        addCookie(request, response, name, value, domain, maxAge, httpOnly, contextPath);
    }

    private static void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, String domain, int maxAge, boolean httpOnly, String contextPath) {
        if (request != null && response != null) {
            Cookie cookie = new Cookie(name, value);
            cookie.setMaxAge(maxAge);
            cookie.setSecure(request.isSecure());
            if (contextPath == null || contextPath.isEmpty()) {
                cookie.setPath("/");
            } else {
                cookie.setPath(contextPath);
            }
            if (domain != null && !domain.isEmpty()) {
                cookie.setDomain(domain);
            }
            if (httpOnly) {
                cookie.setHttpOnly(true);
            }

            response.addCookie(cookie);
            log.debug("Cookie update the sessionID.[name={},value={},maxAge={},httpOnly={},path={},domain={}]",
                    cookie.getName(), cookie.getValue(), cookie.getMaxAge(), httpOnly, cookie.getPath(),
                    cookie.getDomain());
        }
    }


    /**
     * failure a cookie
     */
    public static void failureCookie(HttpServletRequest request, HttpServletResponse response,
                                     String name, String domain, String contextPath) {
        if (request != null && response != null) {
            addCookie(request, response, name, null, domain, 0, true, contextPath);
        }
    }

    /**
     * failure a cookie
     */
    public static void failureCookie(HttpServletRequest request,
                                     HttpServletResponse response, String name, String domain) {
        String contextPath = request.getContextPath();
        if (contextPath == null || contextPath.isEmpty()) {
            contextPath = "/";
        }
        failureCookie(request, response, name, domain, contextPath);
    }

    /**
     * failure a cookie
     */
    public static void failureCookie(HttpServletRequest request,
                                     HttpServletResponse response, String name) {
        failureCookie(request, response, name, null);
    }

    /**
     * get request full url, include params
     *
     * @param request current request
     * @return request full url, include params
     */
    public static String getFullRequestUrl(HttpServletRequest request) {
        StringBuilder buff = new StringBuilder(
                request.getRequestURL().toString());
        String queryString = request.getQueryString();
        if (queryString != null) {
            buff.append("?").append(queryString);
        }

        return buff.toString();
    }


    /**
     * redirect to url
     *
     * @param response        HttpServletResponse object
     * @param url             recirect url
     * @param movePermanently true 301 for permanent redirect, false 302(temporary redirect)
     * @throws java.io.IOException
     */
    public static void redirect(HttpServletResponse response, String url,
                                boolean movePermanently) throws IOException {
        if (!movePermanently) {
            response.sendRedirect(url);
        } else {
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.setHeader("Location", url);
        }
    }

}
