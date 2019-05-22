
package cn.qimate.bike.core.common;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * 网络连接Utils
 * @author wutao
 *
 */

public class NetworkUtils {

    /** 网络不可用 */

    public static final int NONETWORK = 0;

    /** 是wifi连接 */

    public static final int WIFI = 1;

    /** 不是wifi连接 */

    public static final int NOWIFI = 2;


    public static int getNetWorkType(Context context) {

        if (!isNetWorkAvalible(context)) {
 
           return NetworkUtils.NONETWORK;

        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
		cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting())

            return NetworkUtils.WIFI;

        else

            return NetworkUtils.NOWIFI;

    }


    public static boolean isNetWorkAvalible(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {

            return false;

        }

        NetworkInfo ni = cm.getActiveNetworkInfo();

        return ni != null && ni.isAvailable();

    }

}
