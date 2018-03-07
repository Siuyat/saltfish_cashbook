package com.example.ruanx.cashbook;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ruanx.cashbook.Model.BT_Item;
import com.example.ruanx.cashbook.SQLite.MyDatabaseManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

public class OutputActivity extends AppCompatActivity {

    private Button backBtn;
    private Button okBtn;
    private TextView dateTv;
    private MyDatabaseManager dbManager;
    private SharedPreferences pref;
    final private String BACKGROUN_IMAGE_PATH="backgroudImagePath";
    final int permissionRequestCode=2018;
    private ConstraintLayout linearLayout;
    //获取手机本身存储根目录
    private String absolutePath="";//绝对
    private int begin = 0;
    private int end = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);
        initView();
        updateBackGround();
        initWindow();
        dbManager = new MyDatabaseManager(this);
        absolutePath= Environment.getExternalStoragePublicDirectory("").getAbsolutePath();//绝对
        Log.d("绝对路径 ", "onCreate: "+absolutePath);

        dateTv.setOnClickListener(new View.OnClickListener() {
            Calendar c = Calendar.getInstance();
            @Override
            public void onClick(View v) {
                new DoubleDatePickerDialog(OutputActivity.this, 0, new DoubleDatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                          int startDayOfMonth, DatePicker endDatePicker, int endYear, int endMonthOfYear,
                                          int endDayOfMonth) {
                        int s_year,s_month,s_day;
                        int e_year,e_month,e_day;
                        double all,per;
                        String textString = String.format("%d-%d-%d - %d-%d-%d", startYear,startMonthOfYear + 1, startDayOfMonth,endYear, endMonthOfYear + 1, endDayOfMonth);
                        s_year=startYear;s_month=startMonthOfYear;s_day=startDayOfMonth;
                        e_year=endYear;e_month=endMonthOfYear;e_day=endDayOfMonth;
                        begin=s_year*10000+(s_month+1)*100+s_day-1;
                        end=e_year*10000+(e_month+1)*100+e_day+1;
                        //all  = dbManager.getTimeTotal(s_weight,e_weight);
                        dateTv.setText(textString);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), true).show();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (ContextCompat.checkSelfPermission(OutputActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        ||ContextCompat.checkSelfPermission(OutputActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(OutputActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE }, permissionRequestCode);
                } else {
                    //dbManager.ex(absolutePath);
                    if(begin==0 || end == 0){
                        Toast.makeText(OutputActivity.this,"请选择日期！",Toast.LENGTH_SHORT).show();
                    }else{
                        dbManager.exPartTime(absolutePath,begin,end);
                        Toast.makeText(OutputActivity.this,"导出成功！",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case permissionRequestCode:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dbManager.exPartTime(absolutePath,begin,end);
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private void initView(){
        okBtn = (Button)findViewById(R.id.output_ok_btn);
        backBtn = (Button)findViewById(R.id.back_from_output);
        dateTv = (TextView)findViewById(R.id.output_double_date_tv);
        linearLayout = (ConstraintLayout) findViewById(R.id.output_ll);
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

    private void updateBackGround(){
        pref = getSharedPreferences("background",MODE_PRIVATE);
        String bgpath = pref.getString(BACKGROUN_IMAGE_PATH,"1");
        switch (bgpath) {
            case "1":
                linearLayout.setBackgroundResource(R.mipmap.darksky_ver);
                Log.d("main background", "onCreate: " + 1);
                break;
            case "2":
                linearLayout.setBackgroundResource(R.mipmap.forest_b_ver);
                Log.d("main background", "onCreate: " + 2);
                break;
            case "3":
                linearLayout.setBackgroundResource(R.mipmap.dusk_ver);
                Log.d("main background", "onCreate: " + 3);
                break;
            case "4":
                linearLayout.setBackgroundResource(R.mipmap.map_ver);
                Log.d("main background", "onCreate: " + 4);
                break;
            default:
                Bitmap bitmap = BitmapFactory.decodeFile(bgpath);
                if(bitmap!=null) {
                    linearLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
                    //把bitmap转为drawable,layout为xml文件里的主layout
                }
                else{
                    linearLayout.setBackgroundResource(R.mipmap.darksky_ver);
                    pref.edit().putString(BACKGROUN_IMAGE_PATH,"1");
                    Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
