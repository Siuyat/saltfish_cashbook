package com.example.ruanx.cashbook;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class BackGroundActivity extends AppCompatActivity {
    final private String BACKGROUN_IMAGE_PATH="backgroudImagePath";
    final private String BACKGROUN_BACKGROUND="background";
    private Button okBtn;
    private Button backBtn;
    private String bgpath;
    private Button bgA;
    private Button bgB;
    private Button bgC;
    private Button bgD;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;
    private ConstraintLayout constraintLayout;
    //拍照 相册
    private static final int CHOOSE_PHOTO = 1001;
    private Button album;
    //拍照 相册结束


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_ground);
        initView();
        initWindow();
        updateBG();
        selectBG();
    }

    private void updateBG(){
        pref = getSharedPreferences(BACKGROUN_BACKGROUND,MODE_PRIVATE);
        bgpath=pref.getString(BACKGROUN_IMAGE_PATH,"1");
        editor = getSharedPreferences(BACKGROUN_BACKGROUND,MODE_PRIVATE).edit();
        switch (bgpath) {
            case "1":
                constraintLayout.setBackgroundResource(R.mipmap.darksky_ver);
                Log.d("main background", "onCreate: " + 1);
                break;
            case "2":
                constraintLayout.setBackgroundResource(R.mipmap.forest_b_ver);
                Log.d("main background", "onCreate: " + 2);
                break;
            case "3":
                constraintLayout.setBackgroundResource(R.mipmap.dusk_ver);
                Log.d("main background", "onCreate: " + 3);
                break;
            case "4":
                constraintLayout.setBackgroundResource(R.mipmap.map_ver);
                Log.d("main background", "onCreate: " + 4);
                break;
            default:
                setBackgroud(bgpath);
                break;
        }

    }


    private void initView(){
        album=findViewById(R.id.bg_album);
        okBtn = (Button)findViewById(R.id.bg_ok);
        backBtn = (Button)findViewById(R.id.bg_back);
        bgA = (Button)findViewById(R.id.bg_1);
        bgB = (Button)findViewById(R.id.bg_2);
        bgC = (Button)findViewById(R.id.bg_3);
        bgD = (Button)findViewById(R.id.bg_4);
        constraintLayout = (ConstraintLayout)findViewById(R.id.bgact_constraintlayout);
        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }

    /* 状态栏透明 */
    private void initWindow(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }


    /*从相册中选择背景*/
    private void selectBG(){

        bgA.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                constraintLayout.setBackgroundResource(R.mipmap.darksky_ver);
                bgpath = "1";
                editor.putString(BACKGROUN_IMAGE_PATH,"");
            }
        });

        bgB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                constraintLayout.setBackgroundResource(R.mipmap.forest_b_ver);
                bgpath = "2";
                editor.putString(BACKGROUN_IMAGE_PATH,"");
            }
        });

        bgC.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                constraintLayout.setBackgroundResource(R.mipmap.dusk_ver);
                bgpath = "3";
                editor.putString(BACKGROUN_IMAGE_PATH,"");
            }
        });

        bgD.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                constraintLayout.setBackgroundResource(R.mipmap.map_ver);
                bgpath = "4";

            }
        });

        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(BackGroundActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(BackGroundActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    openAlbum();
                }
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                editor.putString(BACKGROUN_IMAGE_PATH,bgpath);
                editor.apply();
                finish();
            }
        });
    }





    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        if(imagePath!=null){
            displayImage(imagePath); // 根据图片路径显示图片
        }
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        if(imagePath!=null){
            displayImage(imagePath); // 根据图片路径显示图片
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        if(bitmap!=null) {
            bgpath=imagePath;
            constraintLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
            //把bitmap转为drawable,layout为xml文件里的主layout
        }
        else {
           bgpath="1";
           constraintLayout.setBackgroundResource(R.mipmap.darksky_ver);
        }
    }

   private void setBackgroud(String imagePath){
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        if(bitmap!=null) {
           constraintLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
            //把bitmap转为drawable,layout为xml文件里的主layout
        }
        else {//说明文件没有找到
            bgpath="1";
            editor.putString(BACKGROUN_IMAGE_PATH,"1");
            constraintLayout.setBackgroundResource(R.mipmap.darksky_ver);
       }
   }

}
