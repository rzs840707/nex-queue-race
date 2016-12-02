package core;

import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.*;

/**
 * Created by lrkin on 2016/12/2.
 */
public class HttpSessionWrapper implements HttpSession {

    /**
     * session id
     */
    private final String id;

    /**
     * session created time
     */
    private final long createdAt;

    /**
     * session last access time
     */
    private volatile long lastAccessedAt;

    /**
     * session max active
     */
    private int maxInactiveInterval;

    private final ServletContext servletContext;

    /**
     * the session manager
     */
    private final SessionManager sessionManager;

    /**
     * the new attributes of the current request
     */
    private final Map<String, Object> newAttributes = Maps.newHashMap();

    /**
     * the deleted attributes of the current request
     */
    private final Set<String> deleteAttribute = Sets.newHashSet();

    /**
     * session attributes store
     */
    private final Map<String, Object> sessionStore;

    /**
     * true if session invoke invalidate()
     */
    private volatile boolean invalid;

    /**
     * true if session attrs updated
     */
    private volatile boolean dirty;

    public HttpSessionWrapper(String id, SessionManager sessionManager, ServletContext context) {
        this.id = id;
        this.sessionManager = sessionManager;
        this.sessionStore = sessionManager.loadById(id);
        this.servletContext = context;
        this.createdAt = System.currentTimeMillis();
        this.lastAccessedAt = createdAt;
    }

    @Override
    public long getCreationTime() {
        return createdAt;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getLastAccessedTime() {
        return lastAccessedAt;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public void setMaxInactiveInterval(int i) {
        this.maxInactiveInterval = i;
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        checkValid();
        if (newAttributes.containsKey(name)) {
            return newAttributes.get(name);
        } else if (deleteAttribute.contains(name)) {
            return null;
        }
        return sessionStore.get(name);
    }

    @Override
    public Object getValue(String s) {
        return getAttribute(s);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        checkValid();
        HashSet<String> names = Sets.newHashSet(sessionStore.keySet());
        names.addAll(newAttributes.keySet());
        names.removeAll(deleteAttribute);
        return Collections.enumeration(names);
    }

    @Override
    public String[] getValueNames() {
        checkValid();
        Set<String> names = Sets.newHashSet(sessionStore.keySet());
        names.addAll(newAttributes.keySet());
        names.removeAll(deleteAttribute);
        return names.toArray(new String[0]);
    }

    @Override
    public void setAttribute(String name, Object value) {
        checkValid();
        if (value != null) {
            newAttributes.put(name, value);
            deleteAttribute.remove(name);
        } else {
            deleteAttribute.add(name);
            newAttributes.remove(name);
        }
        dirty = true;
    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        checkValid();
        deleteAttribute.add(name);
        newAttributes.remove(name);
        dirty = true;
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
        dirty = true;
    }

    @Override
    public void invalidate() {
        invalid = true;
        dirty = true;
        sessionManager.deleteById(this.getId());
    }

    @Override
    public boolean isNew() {
        return Boolean.TRUE;
    }
}