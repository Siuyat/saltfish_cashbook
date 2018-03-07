package com.example.ruanx.cashbook;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.example.ruanx.cashbook.Model.Item;
import com.example.ruanx.cashbook.SQLite.MyDatabaseManager;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AddItemActivity extends AppCompatActivity {
    private LinearLayout linearLayout;
    private EditText commentsET;
    private TextView labelTV;
    private TextView shouzhiTV;
    private TextView subtypeTV;
    private Button labelBtn;
    private TextView dateTV;
    private Button dateBtn;
    private EditText numberET;
    private Button okBtn;
    private Button cancelBtn;
    static final int DATE_DIALOG_ID = 0;
    private int day = 0;
    private int year = 0;
    private int month = 0;
    private MyDatabaseManager dbManager;
    private int itemId;
    private DecimalFormat df = new DecimalFormat("######0.00");  //保留两位小数
    private Calendar calendar;
    String[] str={"","",""};

    final private String BACKGROUN_IMAGE_PATH="backgroudImagePath";
    private int bgid;   // 1 2 3 4 对应4套主题，数字存储在SF中。
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        initView();
        initWindow();
        initDate(); //初始化为当前日期，需要改动再按btn
        updateBackGround();

        //自动弹出软键盘
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
             public void run() {
                 InputMethodManager inputManager =
                 (InputMethodManager) numberET.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                 inputManager.showSoftInput(numberET, 0);
             }}, 800);// 0.8秒后自动弹出

        dbManager = new MyDatabaseManager(this);
        final Intent intent = getIntent();
        itemId = intent.getIntExtra("item_id",-1); //若为-1，是新增条目，否则是数据库中的id
        str = dbManager.getMostUseLabel();
        Log.d("label ", str[0]+" "+str[1]+" "+str[2]);

        //itemId != 1,编辑项目的情况，初始化各显示
        if(itemId!=-1){
            Item item = dbManager.getItemByID(itemId);
            commentsET.setText(item.getComments());
            numberET.setText(df.format(item.getMoney()));
            year = item.getYear();
            day = item.getDay();
            month = item.getMonth();
            dateTV.setText(year+"-"+month+"-"+day);
            shouzhiTV.setText(item.getType());
            labelTV.setText(item.getLabel());
            subtypeTV.setText(item.getSubtype());
        } else {
            str = dbManager.getMostUseLabel();
            //Log.d("label ", str[0]+" "+str[1]+" "+str[2]);
            shouzhiTV.setText(str[0]);
            labelTV.setText(str[1]);
            subtypeTV.setText(str[2]);
        }

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //调用 onCreateDialog(int)回调函数来请求一个Dialog
                showDialog(DATE_DIALOG_ID);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean flag = true;    //判断信息是否完全
                String label = "";
                String comments = "";
                String type = "";
                String subType="";
                double number = 0.00;
                comments = commentsET.getText().toString();
                type = shouzhiTV.getText().toString();
                label = labelTV.getText().toString();
                subType=subtypeTV.getText().toString();
                if(year==0 || month==0 || day==0){
                    flag = false;
                }
                String num = numberET.getText().toString();
                if(null == num || "".equals(num)){
                    flag = false;
                }
                if(type == null || "".equals(num)){
                    flag = false;
                }
                if(label == null || "".equals(num)){
                    flag = false;
                }
                if(subType == null || "".equals(num)){
                    flag = false;
                }
                if(flag==false){
                    Toast.makeText(AddItemActivity.this,"请完善信息！",Toast.LENGTH_SHORT).show();
                } else {
                    number = Double.valueOf(num);
                    Item item = new Item(type, label,subType, comments, day, month, year, number);
                    if(itemId==-1) {    //新增
                        dbManager.addItemToDB(item);
                        Toast.makeText(AddItemActivity.this, "添加成功！", Toast.LENGTH_SHORT).show();
                    } else {    //更新
                        dbManager.updateItem(itemId,item);
                        Toast.makeText(AddItemActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }

            }
        });

        labelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(AddItemActivity.this,selectLabelActivity.class);
                startActivityForResult(i,4);
            }
        });

    }

    //从活动返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4 ) {
                labelTV.setText(data.getStringExtra("label_return"));
                shouzhiTV.setText(data.getStringExtra("type_return"));
                subtypeTV.setText(data.getStringExtra("subtype_return"));
        }
    }

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


    private void initView(){
        shouzhiTV = (TextView)findViewById(R.id.shouzhi_tv);
        labelTV = (TextView)findViewById(R.id.label_tv);
        labelBtn = (Button)findViewById(R.id.chooser_label);
        commentsET = (EditText)findViewById(R.id.comments_et);
        dateTV = (TextView)findViewById(R.id.date_tv);
        dateBtn = (Button)findViewById(R.id.date_btn);
        numberET = (EditText)findViewById(R.id.number_et);
        okBtn = (Button)findViewById(R.id.additem_ok);
        cancelBtn = (Button)findViewById(R.id.cancel_add);
        linearLayout=findViewById(R.id.addact_linearlayout);
        subtypeTV=findViewById(R.id.subtype_tv);
        updateBG();
        updateBGfromalbum();
    }

    private void updateBG(){
        editor = getSharedPreferences("background",MODE_PRIVATE).edit();
        pref = getSharedPreferences("background",MODE_PRIVATE);
        bgid = pref.getInt("bg",0);
        if(bgid==0){
            editor.putInt("bg",1);
            editor.apply();
            bgid = 1;
        }
        switch (bgid) {
            case 1:
                linearLayout.setBackgroundResource(R.mipmap.darksky_ver);
                Log.d("main background", "onCreate: " + 1);
                break;
            case 2:
                linearLayout.setBackgroundResource(R.mipmap.forest_b_ver);
                Log.d("main background", "onCreate: " + 2);
                break;
            case 3:
                linearLayout.setBackgroundResource(R.mipmap.dusk_ver);
                Log.d("main background", "onCreate: " + 3);
                break;
            case 4:
                linearLayout.setBackgroundResource(R.mipmap.map_ver);
                Log.d("main background", "onCreate: " + 4);
                break;
            default:
                linearLayout.setBackgroundResource(R.mipmap.darksky_ver);
                break;
        }

    }
    private void updateBGfromalbum(){
        String bgpath=pref.getString(BACKGROUN_IMAGE_PATH,"");
        if(!bgpath.equals("")){
            setBackgroud(bgpath);
        }
    }
    private void setBackgroud(String imagePath){
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        if(bitmap!=null) {
            linearLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
            //把bitmap转为drawable,layout为xml文件里的主layout
        }
        else {
            updateBG();
        }
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int y, int m, int d) {
            StringBuilder dateStr = new StringBuilder();
            dateStr.append(y).append("-")
                    .append(m+1).append("-")
                    .append(d);
            year = y; month = m+1; day = d;
            dateTV.setText(dateStr.toString());
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
        month = (calendar.get(Calendar.MONTH)+1);   //获得月份
        day = calendar.get(Calendar.DATE);  //获得日期
        year = calendar.get(Calendar.YEAR); //获得年份
        dateTV.setText(year+"-"+month+"-"+day);
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
