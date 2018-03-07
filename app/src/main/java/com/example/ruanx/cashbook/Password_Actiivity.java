package com.example.ruanx.cashbook;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ruanx.cashbook.Model.Item;

/**
 * Created by Administrator on 2018/1/1.
 */

public class Password_Actiivity extends AppCompatActivity {
    private Button OK;
    private Button Back;
    private Button CLEAR;
    private EditText editText1;
    private EditText editText2;
    private Intent intent;
    private int Index;
    private LinearLayout linearLayout;
    private SharedPreferences pref;
    final private String BACKGROUN_IMAGE_PATH="backgroudImagePath";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password);
        init();
        readAccount();
        login();
        updateBackGround();
        initWindow();
    }

    public void init()
    {
        OK = (Button)findViewById(R.id.buttonOK);
        editText1 = (EditText)findViewById(R.id.editText1);
        editText2 = (EditText)findViewById(R.id.editText2);
        Back=(Button)findViewById(R.id.p_back);
        CLEAR = (Button)findViewById(R.id.buttonCLE);
        intent = this.getIntent();
        Index = intent.getIntExtra("judge1", 0);
        linearLayout = (LinearLayout)findViewById(R.id.password_ll);
    }

    public void readAccount() {
        //创建SharedPreferences对象
        SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
        //获得保存在SharedPredPreferences中的密码
        String flag = sp.getString("flag", "");
        String password1 = sp.getString("password1", "");
        String password2 = sp.getString("password2", "");
        //在输入框中显示密码
        if(!"".equals(password2) && flag.equals("yes") && password1.equals(password2)&& Index!=5){
            editText1.setText(password1);
            editText2.setText(password2);
            editText1.setVisibility(View.INVISIBLE);
            editText2.setHint("Password");
            editText2.setText("");
            CLEAR.setVisibility(View.INVISIBLE);
        }
    }

    public void login(){
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获得用户输入的密码
                String password1 = editText1.getText().toString();
                String password2 = editText2.getText().toString();
                if ("".equals(password1) | "".equals(password2) ){
                    Toast.makeText(Password_Actiivity.this, "Password cannot be empty.", Toast.LENGTH_LONG).show();
                    return ;
                }
                else if (!password1.equals(password2)) {
                    Toast.makeText(Password_Actiivity.this, "Password Mismatch", Toast.LENGTH_LONG).show();
                    return ;
                }
                else{
                    String flag = "yes";
                    //创建sharedPreference对象，info表示文件名，MODE_PRIVATE表示访问权限为私有的
                    SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
                    //获得sp的编辑器
                    SharedPreferences.Editor ed = sp.edit();
                    //以键值对的显示将密码保存到sp中
                    ed.putString("flag",flag);
                    ed.putString("password1", password1);
                    ed.putString("password2", password2);
                    //提交密码
                    ed.commit();
                    Intent i = new Intent(Password_Actiivity.this,MainActivity.class);
                    i.putExtra("judge", 5);
                    startActivity(i);
                    finish();
                }
            }
        });

        CLEAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder delete_alertDialog = new AlertDialog.Builder(Password_Actiivity.this);
                delete_alertDialog.setTitle("取消密码保护")
                        .setMessage("确认取消密码保护?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String flag = "no";
                                SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
                                SharedPreferences.Editor ed = sp.edit();
                                ed.putString("flag",flag);
                                ed.commit();
                                Toast.makeText(Password_Actiivity.this, "取消密码保护成功", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {}
                        });
                delete_alertDialog.create().show();
            }
        });
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Index==5) finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent2 = this.getIntent();
        int Index = intent2.getIntExtra("judge1", 0);
        if (Index!=5){
            System.exit(0);
        }
        else super.onBackPressed();
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
