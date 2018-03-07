package com.example.ruanx.cashbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ruanx.cashbook.Model.Item;
import com.example.ruanx.cashbook.SQLite.MyDatabaseManager;

import java.text.DecimalFormat;

public class ItemInfoActivity extends AppCompatActivity {
    final private String BACKGROUN_IMAGE_PATH="backgroudImagePath";
    final private String SharedPreferences_BG="background";
    private Button back;
    private Button edit;
    private TextView labelTV;
    private TextView moneyTV;
    private TextView dateTV;
    private TextView commentsTV;
    private Item item;
    private ConstraintLayout constraintLayoutInfo;
    private int itemid;
    private SQLiteDatabase db;
    private MyDatabaseManager dbManager;
    private DecimalFormat df = new DecimalFormat("######0.00");
    private String label="一般支出";
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);
        initWindow();
        initView();
        updateBackGround();
        //初始化数据库
        dbManager = new MyDatabaseManager(this);
        db = dbManager.getMyDB();

        final Intent intent = getIntent();
        itemid =  intent.getIntExtra("item_id",-1);
        Log.d("item_id", "onCreate: "+itemid);

        //出错了
        if(itemid == -1){
            finish();
        }

        initShow();

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent();
                i.putExtra("item_label",label);
                setResult(1,i);
                finish();
            }
        });

        edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(ItemInfoActivity.this,AddItemActivity.class);
                i.putExtra("item_id",itemid);
                startActivityForResult(i,2);
            }
        });
    }



    private void initView(){
        back = (Button)findViewById(R.id.info_back_btn);
        edit = (Button)findViewById(R.id.info_edit_btn);
        labelTV = (TextView)findViewById(R.id.info_label_tv);
        moneyTV = (TextView)findViewById(R.id.info_money_tv);
        dateTV = (TextView)findViewById(R.id.info_date_tv);
        commentsTV = (TextView)findViewById(R.id.info_comments_tv);
        constraintLayoutInfo = (ConstraintLayout)findViewById(R.id.infoact_constraintlayout);
    }


    private void updateBackGround(){
        pref = getSharedPreferences("background",MODE_PRIVATE);
        String bgpath = pref.getString(BACKGROUN_IMAGE_PATH,"1");
        switch (bgpath) {
            case "1":
                constraintLayoutInfo.setBackgroundResource(R.mipmap.darksky_ver);
                Log.d("main background", "onCreate: " + 1);
                break;
            case "2":
                constraintLayoutInfo.setBackgroundResource(R.mipmap.forest_b_ver);
                Log.d("main background", "onCreate: " + 2);
                break;
            case "3":
                constraintLayoutInfo.setBackgroundResource(R.mipmap.dusk_ver);
                Log.d("main background", "onCreate: " + 3);
                break;
            case "4":
                constraintLayoutInfo.setBackgroundResource(R.mipmap.map_ver);
                Log.d("main background", "onCreate: " + 4);
                break;
            default:
                Bitmap bitmap = BitmapFactory.decodeFile(bgpath);
                if(bitmap!=null) {
                    constraintLayoutInfo.setBackground(new BitmapDrawable(getResources(), bitmap));
                    //把bitmap转为drawable,layout为xml文件里的主layout
                }
                else{
                    constraintLayoutInfo.setBackgroundResource(R.mipmap.darksky_ver);
                    pref.edit().putString(BACKGROUN_IMAGE_PATH,"1");
                    Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void initShow(){
        Item item = dbManager.getItemByID(itemid);
        labelTV.setText("在"+item.getLabel()+"上"+item.getType()+"了");
        dateTV.setText(""+item.getYear()+"-"+item.getMonth()+"-"+item.getDay());
        moneyTV.setText(df.format(item.getMoney()));
        commentsTV.setText(item.getSubtype()+"  "+item.getComments());
        label = item.getLabel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 ) {
            initShow();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent i = new Intent();
            i.putExtra("item_label",label);
            setResult(1,i);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
}
