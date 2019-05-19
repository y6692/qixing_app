package cn.http.config;

import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by lan on 2016/7/18.
 */
public class PersistentCookieStore {
    private final ArrayMap<String, ConcurrentHashMap<String, Cookie>> cookieStore;

    public PersistentCookieStore() {
        cookieStore = new ArrayMap<>();
    }

    protected String getCookieToken(Cookie cookie) {
        return cookie.name() + "@" + cookie.domain();
    }

    public void add(HttpUrl url, Cookie cookie) {
        String name = getCookieToken(cookie);

        //将cookies缓存到内存中 如果缓存过期 就重置此cookie
        if (!cookie.persistent()) {
            if (!cookieStore.containsKey(url.host())) {
                cookieStore.put(url.host(), new ConcurrentHashMap<String, Cookie>());
            }
            cookieStore.get(url.host()).put(name, cookie);
        } else {
            if (cookieStore.containsKey(url.host())) {
                cookieStore.get(url.host()).remove(name);
            }
        }
    }

    public void add(HttpUrl url, List<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            add(url, cookie);
        }
    }

    public List<Cookie> get(HttpUrl url) {
        ArrayList<Cookie> ret = new ArrayList<>();
        if (cookieStore.containsKey(url.host()))
            ret.addAll(cookieStore.get(url.host()).values());
        return ret;
    }

    public boolean contain(HttpUrl url) {
        return cookieStore.containsKey(url.host());
    }

    public void removeAll() {
        Iterator iterator = cookieStore.keySet().iterator();
        while (iterator.hasNext()) {
            iterator.remove();
        }
    }

    public List<Cookie> getCookies() {
        ArrayList<Cookie> ret = new ArrayList<>();
        for (String key : cookieStore.keySet())
            ret.addAll(cookieStore.get(key).values());

        return ret;
    }

}
