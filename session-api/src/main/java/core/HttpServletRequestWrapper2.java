package core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by lrkin on 2016/12/2.
 */
public class HttpServletRequestWrapper2 extends javax.servlet.http.HttpServletRequestWrapper {
    private final static Logger log = LoggerFactory.getLogger(HttpServletRequestWrapper2.class);
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final SessionManager sessionManager;
    private HttpSessionWrapper session;
    private String sessionCookieName;
    private String cookieDomain;
    private String cookieContextPath;
    private int maxInactiveInterval;
    private int cookieMaxAge;

    public HttpServletRequestWrapper2(HttpServletRequest request, HttpServletResponse response, SessionManager sessionManager) {
        super(request);
        this.request = request;
        this.response = response;
        this.sessionManager = sessionManager;
    }

    @Override
    public HttpSession getSession(boolean create) {
        return super.getSession(create);
    }

    /**
     * get session instance , create new one if not exist
     *
     * @return
     */
    @Override
    public HttpSession getSession() {
        return doGetSession(true);
    }

    private HttpSession doGetSession(boolean b) {
    }

    /**
     * get session id name in cookie
     *
     * @return
     */
    public String getSessionCookieName() {
        return sessionCookieName;
    }

    /**
     * set session id in cookie
     *
     * @param sessionCookieName
     */
    public void setSessionCookieName(String sessionCookieName) {
        this.sessionCookieName = sessionCookieName;
    }

    /**
     * get cookie's domain
     *
     * @return
     */
    public String getCookieDomain() {
        return this.cookieDomain;
    }

    /**
     * set cookie cookie's domain
     */
    public void setCookieDomain(String cookieDomain) {
        this.cookieDomain = cookieDomain;
    }

    /**
     * get cookie's store path
     *
     * @return cookie's store path
     */
    public String getCookieContextPath() {
        return cookieContextPath;
    }

    /**
     * set cookie's store path
     */
    public void setCookieContextPath(String cookieContextPath) {
        this.cookieContextPath = cookieContextPath;
    }

    /**
     * set session inactive life (seconds)
     */
    public void setMaxInactiveInterval(int maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    /**
     * set cookie max age
     *
     * @param cookieMaxAge cookie max age
     */
    public void setCookieMaxAge(int cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    /**
     * get current session
     *
     * @return the current session instance
     */
    public HttpSessionWrapper currentSession() {
        return session;
    }

}
