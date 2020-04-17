package cn.qimate.bike.activity;

import android.graphics.Color;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.util.ToastUtil;

public class BackUp {
}

//                    if(unauthorized_code==6){
//                        centerMarker.setAlpha(0);
//                    }else{
//                        centerMarker.setAlpha(255);
//                    }

//                    if (result.getFlag().equals("Success")) {
//                        JSONArray array = new JSONArray(result.getData());
//
//                        Log.e("initNearby===Bike", "==="+array.length());
//
//                        for (Marker marker : bikeMarkerList){
//                            if (marker != null){
//                                marker.remove();
//                            }
//                        }
//                        if (!bikeMarkerList.isEmpty() || 0 != bikeMarkerList.size()){
//                            bikeMarkerList.clear();
//                        }
//                        if (0 == array.length()){
//                            ToastUtils.show("附近没有单车");
//                        }else {
//                            for (int i = 0; i < array.length(); i++){
//                                NearbyBean bean = JSON.parseObject(array.getJSONObject(i).toString(), NearbyBean.class);
//                                // 加入自定义标签
//
////                                Log.e("initNearby===Bike", bean.getLatitude()+"==="+bean.getLongitude());
//
//                                MarkerOptions bikeMarkerOption = new MarkerOptions().position(new LatLng(Double.parseDouble(bean.getLatitude()),Double.parseDouble(bean.getLongitude()))).icon(bikeDescripter);
//                                Marker bikeMarker = aMap.addMarker(bikeMarkerOption);
//                                bikeMarkerList.add(bikeMarker);
//                            }
//
//                        }
//                    } else {
//                        ToastUtils.show(result.getMsg());
//                    }

//    private void schoolRange2(){
//        if(isHidden) return;
//
//        Log.e("main===schoolRange0", isHidden+"==="+jsonArray);
//
//        if(jsonArray != null){
//
//            Log.e("biking===schoolRange%", loadingDialog+"==="+loadingDialog.isShowing());
//
//            onStartCommon("正在加载");
//
////            m_myHandler.post(new Runnable() {
////                @Override
////                public void run() {
////                    if (loadingDialog2 != null && !loadingDialog2.isShowing()) {
////                        loadingDialog2.setTitle("正在加载");
////                        loadingDialog2.show();
////                    }
////                }
////            });
//
//
//            m_myHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if(isHidden) return;
//
//                    Log.e("biking===schoolRange%0", loadingDialog+"==="+loadingDialog.isShowing());
//
//                    try{
//                        pOptions.clear();
//                        if (!isContainsList.isEmpty() || 0 != isContainsList.size()){
//                            isContainsList.clear();
//                        }
//
//                        for (int i = 0; i < jsonArray.length(); i++) {
//
////                            if (loadingDialog != null && !loadingDialog.isShowing()) {
////                                loadingDialog.setTitle("正在加载");
////                                loadingDialog.show();
////                            }
//
//                            final List<LatLng> list = new ArrayList<>();
//                            final List<LatLng> list2 = new ArrayList<>();
//                            int flag=0;
//
//                            for (int j = 0; j < jsonArray.getJSONArray(i).length(); j ++){
//                                JSONObject jsonObject = jsonArray.getJSONArray(i).getJSONObject(j);
//
//                                LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")), Double.parseDouble(jsonObject.getString("longitude")));
//
//                                if(jsonObject.getInt("is_yhq")==0){
//                                    flag=0;
//                                    list.add(latLng);
//                                }else{
//                                    flag=1;
//                                    list2.add(latLng);
//                                }
//                            }
//
//                            Log.e("biking===schoolRange#", "==="+i);
//
//                            Polygon polygon = null;
//                            final PolygonOptions pOption = new PolygonOptions();
//                            if(flag==0){
//                                pOption.addAll(list);
//
//                                pOptions.add(aMap.addPolygon(pOption.strokeWidth(2)
//                                        .strokeColor(Color.argb(255, 228, 59, 74))
//                                        .fillColor(Color.argb(75, 230, 0, 18))));
//
//                            }else{
//                                pOption.addAll(list2);
//                                pOptions.add(aMap.addPolygon(pOption.strokeWidth(2)
//                                        .strokeColor(Color.argb(255, 255, 80, 23))
//                                        .fillColor(Color.argb(75, 255, 80, 23))));
//
//                                getCenterPoint(list2);
//                            }
//
//                            if(!isHidden){
////                                pOptions.add(polygon);
//
////                                isContainsList.add(polygon.contains(myLocation));
//                            }
//
//                        }
//                    }catch (Exception e){
//                        e.printStackTrace();
//                        Log.e("biking===schoolRange_e", "==="+e);
//                    }
//
//                    Log.e("biking===schoolRange%1", loadingDialog+"==="+loadingDialog.isShowing());
//
//                    if (loadingDialog != null && loadingDialog.isShowing()){
//                        loadingDialog.dismiss();
//                    }
//
//                    Log.e("biking===schoolRange%2", loadingDialog+"==="+loadingDialog.isShowing());
//                }
//            });
//
//
//        }else{
//            RequestParams params = new RequestParams();
//            params.put("type", 1);
//
////            if(1==1) return;
//
//            HttpHelper.get(context, Urls.schoolRange, params, new TextHttpResponseHandler() {
//                @Override
//                public void onStart() {
//                    onStartCommon("正在加载");
//                }
//                @Override
//                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                    onFailureCommon(throwable.toString());
//                }
//
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, final String responseString) {
//                    m_myHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            try {
//                                ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                                if (result.getFlag().equals("Success")) {
//                                    jsonArray = new JSONArray(result.getData());
//
//                                    if(isHidden) return;
//
//                                    if (!isContainsList.isEmpty() || 0 != isContainsList.size()){
//                                        isContainsList.clear();
//                                    }
//                                    for (int i = 0; i < jsonArray.length(); i++) {
//                                        List<LatLng> list = new ArrayList<>();
//                                        List<LatLng> list2 = new ArrayList<>();
//                                        int flag=0;
//
////                            Log.e("main===schoolRange1", i+"==="+jsonArray.length()+"==="+jsonArray.getJSONArray(i).length());
//
//                                        for (int j = 0; j < jsonArray.getJSONArray(i).length(); j ++){
//
//                                            JSONObject jsonObject = jsonArray.getJSONArray(i).getJSONObject(j);
//
////                                Log.e("main===schoolRange1", "221==="+jsonObject.getInt("is_yhq"));
//
//                                            LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")), Double.parseDouble(jsonObject.getString("longitude")));
//
//
////                                Log.e("main===schoolRange1", "222==="+Integer.parseInt(jsonObject.getString("is_yhq")));
//
//                                            if(jsonObject.getInt("is_yhq")==0){
//                                                flag=0;
//                                                list.add(latLng);
//
////                                    Log.e("main===schoolRange1", "222==="+jsonObject.getInt("is_yhq"));
//                                            }else{
//                                                flag=1;
//                                                list2.add(latLng);
//
////                                    MarkerOptions centerMarkerOption = new MarkerOptions().position(latLng).icon(freeDescripter);
////                                    aMap.addMarker(centerMarkerOption);
//
////                                    Log.e("main===schoolRange1", "223==="+jsonObject.getInt("is_yhq"));
//                                            }
//                                        }
//
////                            Log.e("main===schoolRange1", "333==="+list2+"==="+flag);
//
//                                        Polygon polygon = null;
//                                        PolygonOptions pOption = new PolygonOptions();
//                                        if(flag==0){
//                                            pOption.addAll(list);
//                                            polygon = aMap.addPolygon(pOption.strokeWidth(2)
//                                                    .strokeColor(Color.argb(255, 228, 59, 74))
//                                                    .fillColor(Color.argb(75, 230, 0, 18)));
////                                polygon = aMap.addPolygon(pOption.strokeWidth(2)
////                                        .strokeColor(Color.argb(160, 0, 0, 255))
////                                        .fillColor(Color.argb(160, 0, 0, 255)));
//                                        }else{
//                                            pOption.addAll(list2);
//                                            polygon = aMap.addPolygon(pOption.strokeWidth(2)
//                                                    .strokeColor(Color.argb(255, 255, 80, 23))
//                                                    .fillColor(Color.argb(75, 255, 80, 23)));
////                                polygon = aMap.addPolygon(pOption.strokeWidth(2)
////                                        .strokeColor(Color.argb(160, 255, 0, 0))
////                                        .fillColor(Color.argb(160, 255, 0, 0)));
//
//                                            getCenterPoint(list2);
//                                        }
//
//
//
//                                        if(!isHidden){
//                                            pOptions.add(polygon);
//
//                                            isContainsList.add(polygon.contains(myLocation));
//                                        }else{
////                                Log.e("pOptions===Bike", isContainsList.size()+"==="+pOptions.size());
//                                        }
//
//                                    }
//                                }else {
//                                    ToastUtil.showMessageApp(context,result.getMsg());
//                                }
//                            }catch (Exception e){
//                            }
//                            if (loadingDialog != null && loadingDialog.isShowing()){
//                                loadingDialog.dismiss();
//                            }
//                        }
//                    });
//
//                }
//            });
//        }
//
//
//    }
