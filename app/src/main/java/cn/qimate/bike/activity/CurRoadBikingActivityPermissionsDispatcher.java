// This file was generated by PermissionsDispatcher. Do not modify!
package cn.qimate.bike.activity;

import android.support.v4.app.ActivityCompat;


import java.lang.ref.WeakReference;

import permissions.dispatcher.GrantableRequest;
import permissions.dispatcher.PermissionUtils;

public final class CurRoadBikingActivityPermissionsDispatcher {
  private static final int REQUEST_CONNECTDEVICE = 0;

  private static final String[] PERMISSION_CONNECTDEVICE = new String[] {"android.permission.ACCESS_COARSE_LOCATION","android.permission.BLUETOOTH"};

  private static GrantableRequest PENDING_CONNECTDEVICE;

  private CurRoadBikingActivityPermissionsDispatcher() {
  }

  static void connectDeviceWithPermissionCheck(CurRoadBikingActivity target, String imei) {
    if (PermissionUtils.hasSelfPermissions(target, PERMISSION_CONNECTDEVICE)) {
      target.connectDevice(imei);
    } else {
      PENDING_CONNECTDEVICE = new MainActivityConnectDevicePermissionRequest(target, imei);
      ActivityCompat.requestPermissions(target, PERMISSION_CONNECTDEVICE, REQUEST_CONNECTDEVICE);
    }
  }

  static void onRequestPermissionsResult(CurRoadBikingActivity target, int requestCode, int[] grantResults) {
    switch (requestCode) {
      case REQUEST_CONNECTDEVICE:
      if (PermissionUtils.verifyPermissions(grantResults)) {
        if (PENDING_CONNECTDEVICE != null) {
          PENDING_CONNECTDEVICE.grant();
        }
      }
      PENDING_CONNECTDEVICE = null;
      break;
      default:
      break;
    }
  }

  private static final class MainActivityConnectDevicePermissionRequest implements GrantableRequest {
    private final WeakReference<CurRoadBikingActivity> weakTarget;

    private final String imei;

    private MainActivityConnectDevicePermissionRequest(CurRoadBikingActivity target, String imei) {
      this.weakTarget = new WeakReference<CurRoadBikingActivity>(target);
      this.imei = imei;
    }

    @Override
    public void proceed() {
      CurRoadBikingActivity target = weakTarget.get();
      if (target == null) return;
      ActivityCompat.requestPermissions(target, PERMISSION_CONNECTDEVICE, REQUEST_CONNECTDEVICE);
    }

    @Override
    public void cancel() {
    }

    @Override
    public void grant() {
      CurRoadBikingActivity target = weakTarget.get();
      if (target == null) return;
      target.connectDevice(imei);
    }
  }
}
