package com.aida.camera_permission;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button mButton, mLocation;
    private int TAKE_PHOTO = 0;
    private TextView mLocationResult;
    private int GET_LOCATION = 1;
    private int count = 0;
    private String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/camera_permission/";
    private MarshMallowPermisssion marshMallowPermisssion;
    private Location mLocationMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (Button) findViewById(R.id.camera);
        mLocation = (Button) findViewById(R.id.location);
        mLocationResult = (TextView) findViewById(R.id.locationResult);
        marshMallowPermisssion = new MarshMallowPermisssion(this);

        File newdir = new File(dir);
        newdir.mkdirs();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPhotoFromCamera();
            }
        });

        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.d("CameraDemo", "Pic saved");
        }
        if (requestCode == GET_LOCATION && resultCode == RESULT_OK) {
            Log.d("LocationDemo", "Location Received");


        }
    }

    private void getLocation() {
        if (!marshMallowPermisssion.checkPermissionForLocation()) {
            marshMallowPermisssion.requestPermissionForLocation();
        } else {
            if (!marshMallowPermisssion.checkPermissionForInternet()) {
                marshMallowPermisssion.requestPermissionForInternet();
            }else {
                LocationManager locationManager = (LocationManager)
                        getSystemService(Context.LOCATION_SERVICE);

//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
                if (mLocationMan == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 500, 10, new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    mLocationResult.setText("latitude:" + mLocationMan.getLatitude() + "; \nlongitude:" + mLocationMan.getLongitude());
                                    Log.v("location", "latitiude:" + mLocationMan.getLatitude() + "; \nlongitude:" + mLocationMan.getLongitude());
                                }

                                @Override
                                public void onStatusChanged(String s, int i, Bundle bundle) {

                                }

                                @Override
                                public void onProviderEnabled(String s) {

                                }

                                @Override
                                public void onProviderDisabled(String s) {

                                }
                            });
                    if (locationManager != null) {
                        mLocationMan = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (mLocationMan != null) {
                            mLocationResult.setText("latitude:" + mLocationMan.getLatitude() + "; \nlongitude:" + mLocationMan.getLongitude());
                            Log.v("location", "latitiude:" + mLocationMan.getLatitude() + "; \nlongitude:" + mLocationMan.getLongitude());
                        }

                    }
                }
            }

    }
}


    private void getPhotoFromCamera() {

        if (!marshMallowPermisssion.checkPermissionForCamera()) {
            marshMallowPermisssion.requestPermissionForCamera();
        } else {
            if (!marshMallowPermisssion.checkPermissionForExternalStorage()) {
                marshMallowPermisssion.requestPermissionForExternalStorage();
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File newdir = new File(dir);
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
                try {
                    File mediaFile = File.createTempFile(
                            "IMG_" + timeStamp,  /* prefix */
                            ".jpg",         /* suffix */
                            newdir      /* directory */
                    );
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mediaFile));
                    startActivityForResult(takePictureIntent, TAKE_PHOTO);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
