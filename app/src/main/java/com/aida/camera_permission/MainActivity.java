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
