package cn.http.config;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by lan on 2016/7/18.
 */
public class CookieManager implements CookieJar {
    private final PersistentCookieStore persistentCookieStore;

    public CookieManager() {
        persistentCookieStore = new PersistentCookieStore();
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        persistentCookieStore.add(url, cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = persistentCookieStore.get(url);

        return cookies != null ? cookies : new ArrayList<Cookie>();
    }

    public void clear() {
        persistentCookieStore.removeAll();
    }

}
