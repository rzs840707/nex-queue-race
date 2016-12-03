package core;

import api.SessionManager;
import me.hao0.common.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.WebUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by lrkin on 2016/12/2.
 */
public abstract class SessionFilter implements Filter {
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

    private void initAttrs(FilterConfig config) {
        String param = config.getInitParameter(SESSION_COOKIE_NAME);
        sessionCookieName = Strings.isNullOrEmpty(param) ? DEFAULT_COOKIE_CONTEXT_PATH : param;
        param = config.getInitParameter(MAX_INACTIVE_INTERVAL);
        maxInactiveInterval = Strings.isNullOrEmpty(param) ? DEFAULT_MAX_INACTIVE_INTERVAL : Integer.parseInt(param);
        cookieDomain = config.getInitParameter(COOKIE_DOMAIN);
        param = config.getInitParameter(COOKIE_CONTEXT_PATH);
        cookieContextPath = Strings.isNullOrEmpty(param) ? DEFAULT_COOKIE_CONTEXT_PATH : param;
        param = config.getInitParameter(COOKIE_MAX_AGE);
        cookieMaxAge = Strings.isNullOrEmpty(param) ? DEFAULT_COOKIE_MAX_AGE : Integer.parseInt(param);
        log.info("SessionFilter (sessionCookieName={},maxInactiveInterval={},cookieDomain={})",
                sessionCookieName, maxInactiveInterval, cookieDomain);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequestWrapper2) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequestWrapper2 request2 = new HttpServletRequestWrapper2(httpRequest, httpResponse, sessionManager);
        request2.setSessionCookieName(sessionCookieName);
        request2.setMaxInactiveInterval(maxInactiveInterval);
        request2.setCookieDomain(cookieDomain);
        request2.setCookieContextPath(cookieContextPath);
        request2.setCookieMaxAge(cookieMaxAge);

        //do other filter
        chain.doFilter(request2, response);

        HttpSessionWrapper session = request2.currentSession();
        if (session != null) {
            if (!session.isValid()) {
                //if invalidate , delete session
                log.debug("session is invalid, will be deleted");
                WebUtil.failureCookie(httpRequest, httpResponse, sessionCookieName, cookieDomain, cookieContextPath);
            } else {
                if (session.isDirty()) {
                    //should flush to store
                    log.debug("try to flush session to session store");
                    Map<String, Object> snapshot = session.snapshot();
                    if (sessionManager.persist(session.getId(), snapshot, maxInactiveInterval)) {
                        log.debug("succeed to flush session{} to store, key is : {}", snapshot, session.getId());
                    } else {
                        log.error("failed to persist session to redis");
                        WebUtil.failureCookie(httpRequest, httpResponse, sessionCookieName, cookieDomain, cookieContextPath);
                    }
                } else {
                    //refresh expire time
                    sessionManager.expire(session.getId(), maxInactiveInterval);
                }
            }
        }
    }

    @Override
    public void destroy() {
        sessionManager.destroy();
        log.debug("filter is destroy!");
    }
}
