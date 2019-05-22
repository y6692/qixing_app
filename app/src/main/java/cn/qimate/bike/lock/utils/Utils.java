package cn.qimate.bike.lock.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.format.Formatter;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.qimate.bike.base.BaseApplication;

/**
 * 作者：王凯强 on 2017/1/06 11:07
 * <p>
 * 邮箱：317097478@qq.com
 */
public class Utils {

    public static <T> boolean isEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }

    /**
     * 把数据源HashMap转换成json
     *
     * @param map
     */
    public static String hashMapToJson(HashMap map) {
        String string = "{";
        for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry e = (Map.Entry) it.next();
            string += "\"" + e.getKey() + "\":";
            string += "\"" + e.getValue() + "\",";
        }
        string = string.substring(0, string.lastIndexOf(","));
        string += "}";
        return string;
    }

    /**
     * Gets the version name of the application. For e.g. 1.9.3
     * **
     */
    public static String getApplicationVersionNumber() {

        String versionName = null;
        Context ctx = BaseApplication.getInstance();
        try {
            versionName = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }

    /**
     * 通过媒体Uri获取路劲
     *
     * @param context
     * @param mediaContentUri
     * @return
     */
    public static String getPathForMediaUri(Context context, Uri mediaContentUri) {

        Cursor cur = null;
        String path = null;

        try {
            String[] projection = {MediaStore.MediaColumns.DATA};
            cur = context.getContentResolver().query(mediaContentUri, projection, null, null, null);

            if (cur != null && cur.getCount() != 0) {
                cur.moveToFirst();
                path = cur.getString(cur.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            }

            // Log.v( TAG, "#getRealPathFromURI Path: " + path );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cur != null && !cur.isClosed())
                cur.close();
        }

        return path;
    }

    /**
     * 获取当前SDCard可以空间
     *
     * @param context 得到文件系统的信息：存储块大小，总的存储块数量，可用存储块数量
     *                获取sd卡空间
     *                存储设备会被分为若干个区块
     *                每个区块的大小 * 区块总数 = 存储设备的总大小
     *                每个区块的大小 * 可用区块的数量 = 存储设备可用大小
     * @return
     */
    public static String getSDCardRemainderSize(Context context) {

        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize;
        long totalBlocks;
        long availableBlocks;
        // 由于API18（Android4.3）以后getBlockSize过时并且改为了getBlockSizeLong
        // 因此这里需要根据版本号来使用那一套API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
            availableBlocks = stat.getAvailableBlocks();
        }
        // 利用formatSize函数把字节转换为用户等看懂的大小数值单位
        String totalText = formatSize(context, blockSize * totalBlocks);
        String availableText = formatSize(context, blockSize * availableBlocks);
        //"SDCard总大小:\n" + totalText);
        //"SDCard可用空间大小:\n" + availableText);
        return availableText;
    }

    //封装Formatter.formatFileSize方法，具体可以参考安卓的API
    private static String formatSize(Context context, long size) {
        return Formatter.formatFileSize(context, size);
    }

    public static double str2Double(String str) {
        try {
            return Double.valueOf(str);
        } catch (Exception e) {
        }
        return 0;
    }
}
