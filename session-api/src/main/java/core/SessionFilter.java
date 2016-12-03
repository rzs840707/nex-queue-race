package core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by lrkin on 2016/12/2.
 */
public class SessionFilter implements Filter {
    private final static Logger log = LoggerFactory.getLogger(SessionFilter.class);
    protected final static String SESSION_COOKIE_NAME = "sessionCookieName";
    protected final static String DEFAULT_SESSION_COOKIE_NAME = "sfid";
    protected final static String MAX_INACTIVE_INTERVAL = "maxInactiveInterval";

    /**
     * session cookie name
     *
     * @param filterConfig
     * @throws ServletException
     */
    protected String sessionCookieName;

    /**
     * default 30 mins
     */
    protected final static int DEFAULT_MAX_INACTIVE_INTERVAL = 30 * 60;

    /**
     * max inactive interval
     */
    protected int maxInactiveInterval;

    /**
     * cookie domain
     */
    protected final static String COOKIE_DOMAIN = "cookieDomain";

    /**
     * cookie name
     */
    protected String cookieDomain;

    /**
     * cookie context path
     */
    protected final static String COOKIE_CONTEXT_PATH = "cookieContextPath";

    /**
     * default cookie context path
     */
    protected final static String DEFAULT_COOKIE_CONTEXT_PATH = "/";

    /**
     * cookie's context path
     */
    protected String cookieContextPath;

    /**
     * cookie max age
     */
    protected final static String COOKIE_MAX_AGE = "cookieMaxAge";

    /**
     * default cookie max age
     */
    protected final static int DEFAULT_COOKIE_MAX_AGE = -1;

    /**
     * cookie's life
     */
    protected int cookieMaxAge;

    /**
     * session manager
     */
    protected SessionManager sessionManager;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            sessionManager = createSessionManager();
            initAttrs(filterConfig);
        } catch (Exception e) {
            log.error("failed to init session filter", e);
            throw new ServletException();
        }
    }

    /**
     * subclass create session manager
     *
     * @return
     * @throws IOException
     */
    protected abstract SessionManager createSessionManager() throws IOException;

    private void initAttrs(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    }

    @Override
    public void destroy() {

    }
}
