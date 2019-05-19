package cn.qimate.bike.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


/**
 * Created by lan on 2016/7/7.
 */
public class UIHelper {
    private static Toast toast;
    private static ProgressDialog progressDialog;
    private static AlertDialog alertDialog;

    private static boolean checkActivityNoValid(Context context) {
        if(context instanceof AppCompatActivity) {
            boolean noValid = ((AppCompatActivity) context).isDestroyed() || ((AppCompatActivity) context).isFinishing();

            return noValid;
        }
        return false;
    }

    public static void showToast(Context context, String str) {
        showToast(context, str, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, int stringId) {
        showToast(context, context.getString(stringId), Toast.LENGTH_SHORT);
    }

    private static void showToast(Context context, CharSequence str, int duration) {
        if(toast != null) {
            toast.cancel();
            toast = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        toast = Toast.makeText(context, str, duration);
        toast.show();
    }

    public static void cancelToast() {
        if(toast != null) {
           // toast.cancel();
            toast = null;
        }
    }

    public static void showAlertDialog(Context context, int msgId) {
        if(alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msgId)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showAlertDialog(Context context, String msg) {
        if(alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showAlertDialog(Context context, int msgId,
                                       DialogInterface.OnClickListener confirmListener) {
        if(alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msgId)
                .setCancelable(false)
                .setPositiveButton("确定", confirmListener)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showAlertDialog(Context context, int msgId,
                                       DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener neutralListener) {
        if(alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msgId)
                .setCancelable(false)
                .setPositiveButton("确定", positiveListener)
                .setNeutralButton("不再提醒", neutralListener)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showAlertDialog(Context context, int titleId, int msgId,
                                       DialogInterface.OnClickListener confirmListener) {
        if(alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleId)
                .setMessage(msgId)
                .setCancelable(false)
                .setPositiveButton("确定", confirmListener)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showAlertDialog(Context context, String title, String msg,
                                       DialogInterface.OnClickListener confirmListener) {
        if(alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("确定", confirmListener)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void cancelAlertDialog() {
        if(alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    public static boolean showProgress(Context context, int strId) {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return false;
        }

        String msg = context.getString(strId);
        progressDialog = ProgressDialog.show(context, "", msg, false, false);
        progressDialog.show();

        return true;
    }

    public static void showProgress(Context context, int strId, boolean cancelable) {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        String msg = context.getString(strId);
        progressDialog = ProgressDialog.show(context, "", msg, cancelable, cancelable);
        progressDialog.show();
    }

    public static void showProgress(Context context, String text) {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        progressDialog = ProgressDialog.show(context, "", text, true, true);
        progressDialog.show();
    }

    public static void showProgressValue(Context context, int strId) {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        String msg = context.getString(strId);
        progressDialog = ProgressDialog.show(context, "", msg, true, true);
        progressDialog.show();
    }

    public static void setProgress(int progress) {
        if(progressDialog != null) {
            progressDialog.setMessage(progress + "%");
        }
    }

    public static void dismiss() {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
