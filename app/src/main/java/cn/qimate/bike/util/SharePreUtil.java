package cn.qimate.bike.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import cn.qimate.bike.base.BaseApplication;

/**
 * Created by tomsun on 2016/10/10.
 */
public class SharePreUtil {
    private static Map<String, SharePreUtil> preferencesMap = new HashMap<>();
    public static Context context = BaseApplication.context;
    private SharedPreferences preferences = null;

    public static SharePreUtil getPreferences(String fileName) {
        if (preferencesMap.containsKey(fileName)){
            SharePreUtil sh = preferencesMap.get(fileName);
            return sh;
        }
        SharePreUtil sh = new SharePreUtil();
        sh.preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        preferencesMap.put(fileName, sh);
        return sh;
    }

    /**
     * 向SP存入指定key对应的数据
     * 其中value可以是String、boolean、float、int、long等各种基本类型的值
     * @param key
     * @param value
     */
    public void putString(String key, String value) {
        preferences.edit().putString(key, value).commit();
    }

    public void putBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).commit();
    }

    public void putFloat(String key, float value) {
        preferences.edit().putFloat(key, value).commit();
    }

    public void putInt(String key, int value) {
        preferences.edit().putInt(key, value).commit();
    }

    public void putLong(String key, long value) {
        preferences.edit().putLong(key, value).commit();
    }

    /**
     * 清空SP里所以数据
     */
    public void clear() {
        preferences.edit().clear().commit();
    }

    /**
     * 删除SP里指定key对应的数据项
     * @param key
     */
    public void remove(String key) {
        preferences.edit().remove(key).commit();
    }

    /**
     * 获取SP数据里指定key对应的value。如果key不存在，则返回默认值defValue。
     * @param key
     * @return
     */
    public String getString(String key) {
        return preferences.getString(key, null);
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public float getFloat(String key) {
        return preferences.getFloat(key, 0);
    }

    public int getInt(String key) {
        return preferences.getInt(key, 0);
    }

    public long getLong(String key) {
        return preferences.getLong(key, 0);
    }

    /**
     * 判断SP是否包含特定key的数据
     * @param key
     * @return
     */
    public boolean contains(String key){
        return preferences.contains(key);
    }
}
