package com.example.kimyoungbin.test;

import android.content.Intent;
import android.graphics.Rect;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.NMapView.OnMapStateChangeListener;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.maps.NMapLocationManager;


public class MainActivity extends NMapActivity implements NMapView.OnMapStateChangeListener {

    public static final String API_KEY = "Nai301kypXqOizIZTp5J";

    NMapView mMapView = null;

    NMapController mMapController = null;

    LinearLayout MapContainer;

    NMapViewerResourceProvider mMapViewerResourceProvider = null;
    NMapOverlayManager mOverlayManager;
    NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = null;
    private NMapOverlayManager.OnCalloutOverlayListener onCalloutOverlayListener;

    private NMapMyLocationOverlay mMyLocationOverlay;
    private NMapLocationManager mMapLocationManager;
    private NMapCompassManager mMapCompassManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapContainer = (LinearLayout)findViewById(R.id.MapContainer);

        mMapView = new NMapView(this);

        mMapView.setApiKey(API_KEY);

        //setContentView(mMapView);

        mMapController = mMapView.getMapController();

        MapContainer.addView(mMapView);

        mMapView.setClickable(true);

        mMapView.setBuiltInZoomControls(true,null);

        mMapView.setOnMapStateChangeListener(this);

        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);

        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);

        testOVerlayMaker();

        mMapLocationManager = new NMapLocationManager(this);

        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);

        mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);

        startMyLocation();

    }

    @Override
    public void onMapInitHandler(NMapView nMapView, NMapError errorInfo) {
        if(errorInfo == null){
            mMapController.setMapCenter(new NGeoPoint(126.978371, 37.5666091), 11);
        }else{
            android.util.Log.e("NMAP","onMapInitHandler: erro="+errorInfo.toString());
        }
    }

    @Override
    public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {

    }

    @Override
    public void onMapCenterChangeFine(NMapView nMapView) {

    }

    @Override
    public void onZoomLevelChange(NMapView nMapView, int level) {

    }

    @Override
    public void onAnimationStateChange(NMapView arg0, int animType, int animState) {

    }

    public void onCalloutClick(NMapPOIdataOverlay poIdataOverlay, NMapPOIitem item){
        Toast.makeText(MainActivity.this, "onCalloutClick:"+item.getTitle(), Toast.LENGTH_LONG).show();

    }

    public void onFocusChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item){
        if(item != null){
            Log.i("NMAP","onFocusChanged: "+item.toString());
        }else{
            Log.i("NMAP", "onFocusChanged: ");
        }
    }

    public NMapCalloutOverlay onCreateCalloutOVerlay(NMapOverlay arg0, NMapOverlayItem arg1, Rect arg2){
        return new NMapCalloutBasicOverlay(arg0, arg1, arg2);
    }

    public void onbackButtonClicked(View v){
        Toast.makeText(getApplicationContext(), "돌아가기 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
        finish();
    }

    private void startMyLocation(){
        if(mMapLocationManager.isMyLocationEnabled()){
            if(!mMapView.isAutoRotateEnabled()){
                mMyLocationOverlay.setCompassHeadingVisible(true);
                mMapCompassManager.enableCompass();
                mMapView.setAutoRotateEnabled(true, false);
            }
            mMapView.invalidate();
        }else{
            boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(true);
            if(!isMyLocationEnabled){
                Toast.makeText(MainActivity.this, "Please enable a My Location source in system settings", Toast.LENGTH_LONG).show();
                Intent goToSettings = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(goToSettings);
                return;
            }
        }
    }

    private void stopMyLocation(){
        mMapLocationManager.disableMyLocation();
        if(mMapView.isAutoRotateEnabled()){
            mMyLocationOverlay.setCompassHeadingVisible(false);
            mMapCompassManager.disableCompass();
            mMapView.setAutoRotateEnabled(false, false);
        }

    }

    private final NMapActivity.OnDataProviderListener onDataProviderListener = new NMapActivity.OnDataProviderListener(){

        @Override
        public void onReverseGeocoderResponse(NMapPlacemark nMapPlacemark, NMapError errInfo) {
            if(errInfo!=null){
                Log.e("myLog", "Failed to findPlacemarkAtLocation: error="+errInfo.toString());
                Toast.makeText(MainActivity.this, errInfo.toString(), Toast.LENGTH_LONG).show();
                return;
            }else{
                Toast.makeText(MainActivity.this, nMapPlacemark.toString(), Toast.LENGTH_LONG).show();
            }
        }
    };

    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {
        @Override
        public boolean onLocationChanged(NMapLocationManager nMapLocationManager, NGeoPoint myLocation) {
            if(mMapController != null){
                mMapController.animateTo(myLocation);
            }
            return true;
        }

        @Override
        public void onLocationUpdateTimeout(NMapLocationManager nMapLocationManager) {

        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {
            stopMyLocation();
        }
    };

    private void testOVerlayMaker(){
        int markerId = NMapPOIflagType.PIN;

        NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
        poiData.beginPOIdata(2);
        poiData.addPOIitem(128.3925046, 36.1454420, "marker1",markerId, 0);
        poiData.addPOIitem(128.3915046, 36.1354420, "marker2",markerId, 0);
        poiData.endPOIdata();

        NMapPOIdataOverlay poIdataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
        poIdataOverlay.showAllPOIdata(0);

        poIdataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);
    }
}
