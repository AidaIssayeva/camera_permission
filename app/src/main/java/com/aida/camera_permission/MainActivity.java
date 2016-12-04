package com.aida.camera_permission;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";
    private Button mButton;
    private int TAKE_PHOTO= 0;
    private int count=0;
    private String dir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/camera_permission/";
    private MarshMallowPermisssion marshMallowPermisssion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton=(Button)findViewById(R.id.camera);
        marshMallowPermisssion=new MarshMallowPermisssion(this);

        File newdir = new File(dir);
        newdir.mkdirs();
       // isStoragePermissionGranted();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPhotoFromCamera();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.d("CameraDemo", "Pic saved");
        }
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
//            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
//            mButton.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//
//
//                    count++;
//                    String file = dir+count+".jpg";
//                    File newfile = new File(file);
//                    try {
//                        newfile.createNewFile();
//                    }
//                    catch (IOException e)
//                    {
//                    }
//
//                    Uri outputFileUri = Uri.fromFile(newfile);
//
//                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//
//                    startActivityForResult(cameraIntent, TAKE_PHOTO);
//                }
//            });
//        }
//    }
    public void getPhotoFromCamera() {

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
