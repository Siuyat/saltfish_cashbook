package com.example.ruanx.cashbook;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ruanx.cashbook.Model.BR_item;
import com.example.ruanx.cashbook.SQLite.MyDatabaseManager;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by G40 on 2017/12/29.
 */

public class AddBRActivity extends AppCompatActivity {
    final private String BACKGROUN_IMAGE_PATH="backgroudImagePath";
    private SharedPreferences pref;
    private Button Btn_add;
    private Button Btn_cancle;
    private EditText money;
    private RadioGroup borrowRG;
    private EditText name;
    private EditText commentsbr;
    private Button date;
    private TextView dateTv;
    static final int addBritem=1002;
    private LinearLayout linearLayout;
    private MyDatabaseManager dbManager;
    private String isBorrow;
    private String isReturn;
    private double money_br;
    private String comments_br;
    private int month = 0;
    private int year = 0;
    private int day = 0;
    private DecimalFormat df = new DecimalFormat("######0.00");
    private Calendar calendar;
    static final int DATE_DIALOG_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_br);
        initView();
        initWindow();
        updateBackGround();
        dbManager = new MyDatabaseManager(this);
        initDate();

        //自动弹出软键盘
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager =
                        (InputMethodManager) money.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(money, 0);
            }}, 800);// 0.8秒后自动弹出

        Btn_cancle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //调用 onCreateDialog(int)回调函数来请求一个Dialog
                showDialog(DATE_DIALOG_ID);
            }
        });

        Btn_add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Boolean flag=true;
                String isborrow="",hisname="",isreturn="N",comments="";
                double money1= 0.0;
                //要选择是借入还是借出
                if(borrowRG.getCheckedRadioButtonId() == R.id.borrow){
                    isborrow="入";
                } else if (borrowRG.getCheckedRadioButtonId() == R.id.rent){
                    isborrow="出";
                } else {
                    flag = false;
                }
                //得到对方姓名
                if(!name.getText().toString().equals("")){
                    hisname=name.getText().toString();
                }
                else {
                    flag=false;
                }
                //得到钱数
                if(!money.getText().toString().equals("")){
                    money1 = Double.valueOf(money.getText().toString());
                }
                else {
                    flag=false;
                }
                //日期不能为空
                if(year == 0 || month == 0 || day ==0){
                    flag = false;
                }
                //备注可以为空？
                if(!commentsbr.getText().toString().equals("")){
                    comments = commentsbr.getText().toString();
                }

                if(flag) {
                    BR_item br_item = new BR_item(isborrow, hisname, isreturn, money1, comments, day, month, year);
                    dbManager.addBRItemToDB(br_item);
                    finish();
                }else {
                    Toast.makeText(AddBRActivity.this,"请完善信息！",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int y, int m, int d) {
            StringBuilder dateStr = new StringBuilder();
            dateStr.append(y).append("-")
                    .append(m+1).append("-")
                    .append(d);
            year = y; month = m+1; day = d;
            dateTv.setText(dateStr.toString());
        }
    };

    /**
     * 当Activity调用showDialog函数时会触发该函数的调用
     */
    protected Dialog onCreateDialog(int id){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        switch(id){
            case DATE_DIALOG_ID:
                DatePickerDialog dpd = new DatePickerDialog(this,dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                dpd.setCancelable(true);
                dpd.setTitle("选择日期");
                dpd.show();
                break;
            default:
                break;
        }
        return null;
    }

    //一开始得到当天日期
    private void initDate(){
        calendar = Calendar.getInstance();  //获得calendar实例
        month = (calendar.get(Calendar.MONTH)+1);
        day = calendar.get(Calendar.DATE);
        year = calendar.get(Calendar.YEAR);
        dateTv.setText(year+"-"+month+"-"+day);
    }



    private void initView(){
        linearLayout=findViewById(R.id.addact_br_linearlayout);
        Btn_add=findViewById(R.id.addbr_ok);
        Btn_cancle=findViewById(R.id.cancel_br);
        money=findViewById(R.id.money_br);
        name=findViewById(R.id.hisName);
        commentsbr=findViewById(R.id.comments_br);
        date=(Button)findViewById(R.id.date_br);
        borrowRG=findViewById(R.id.isborrowRG);
        dateTv = (TextView)findViewById(R.id.date_br_tv);
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

