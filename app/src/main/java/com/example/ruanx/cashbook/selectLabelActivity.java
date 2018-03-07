package com.example.ruanx.cashbook;

import android.content.Context;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ruanx.cashbook.Adapter.CommonAdapter;
import com.example.ruanx.cashbook.Adapter.ViewHolder;
import com.example.ruanx.cashbook.Model.BR_item;
import com.example.ruanx.cashbook.Model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G40 on 2018/1/1.
 */

public class selectLabelActivity extends AppCompatActivity {
    private Button back;
    private LinearLayout linearLayout;
    final private String BACKGROUN_IMAGE_PATH="backgroudImagePath";
    private SharedPreferences pref;
    private ListView listViewType;
    private selectAdapter adapter;
    private subtypeAdapter adapter2;
    private ListView listViewSubType;
    private TextView sType;
    String a="";
    private String label="购物消费";
    private String mytype="支出";
    private String subtype="日常用品";
    private String[] type = {"购物消费","餐饮酒水","交通出行","休闲娱乐","居家生活","健康医疗","文化教育","收入","其它支出"};
    private String[] gouwu_subtype = {"日常用品","服装鞋帽","化妆护肤","电器数码","美容美发","虚拟物品","送礼人情","其它"};
    private String[] canyin_subtype = {"早餐","午餐","晚餐","加餐","饮料酒水","零食","水果","请客吃饭","其它"};
    private String[] jiaotong_subtype = {"公共交通","打车","加油","停车费","日常养护","其它"};
    private String[] yule_subtype = {"电影","度假","运动健身","棋牌桌游","KTV","手游","其它"};
    private String[] shenghuo_subtype = {"房租房贷","家政清洁","物业水电煤","话费宽带","宠物","其它"};
    private String[] jiankang_subtype = {"诊疗","药品","住院","按摩理疗","其它"};
    private String[] wenhua_subtype = {"学杂费","培训考试","书报杂志","其它"};
    private String[] shouru_subtype = {"工资","奖金","补贴","中奖","礼金人情","理财盈亏","其它"};
    private String[] qita_subtype = {"慈善捐助","理财支出","罚款赔偿","丢失","其它"};


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        initView();
        updateBackGround();
        initRecyclerView();
        initWindow();
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

    private void initSubType(){
        switch (label){
            case "购物消费":
                adapter2=new subtypeAdapter(this, R.layout.item_subtype,gouwu_subtype);
                listViewSubType.setAdapter(adapter2);
                break;
            case "餐饮酒水":
                adapter2=new subtypeAdapter(this, R.layout.item_subtype,canyin_subtype);
                listViewSubType.setAdapter(adapter2);
                break;
            case "交通出行":
                adapter2=new subtypeAdapter(this, R.layout.item_subtype,jiaotong_subtype);
                listViewSubType.setAdapter(adapter2);
                break;
            case "休闲娱乐":
                adapter2=new subtypeAdapter(this, R.layout.item_subtype,yule_subtype);
                listViewSubType.setAdapter(adapter2);
                break;
            case "居家生活":
                adapter2=new subtypeAdapter(this, R.layout.item_subtype,shenghuo_subtype);
                listViewSubType.setAdapter(adapter2);
                break;
            case "健康医疗":
                adapter2=new subtypeAdapter(this, R.layout.item_subtype,jiankang_subtype);
                listViewSubType.setAdapter(adapter2);
                break;
            case "文化教育":
                adapter2=new subtypeAdapter(this, R.layout.item_subtype,wenhua_subtype);
                listViewSubType.setAdapter(adapter2);
                break;
            case "收入":
                mytype="收入";
                adapter2=new subtypeAdapter(this, R.layout.item_subtype,shouru_subtype);
                listViewSubType.setAdapter(adapter2);
                break;
            case "其它支出":
                adapter2=new subtypeAdapter(this, R.layout.item_subtype,qita_subtype);
                listViewSubType.setAdapter(adapter2);
                break;
            default:
                break;
        }
    }


    private void initRecyclerView(){
        adapter=new selectAdapter(this, R.layout.item_select,type);
        listViewType.setAdapter(adapter);
        listViewType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                label=type[i];
                sType.setText(label);
                mytype="支出";
                initSubType();
            }
        });

        adapter2=new subtypeAdapter(this, R.layout.item_subtype,gouwu_subtype);
        listViewSubType.setAdapter(adapter2);
        listViewSubType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                subtype=adapterView.getAdapter().getItem(i).toString();
                Intent intent = new Intent();
                intent.putExtra("label_return",label);
                intent.putExtra("type_return",mytype);
                intent.putExtra("subtype_return",subtype);
                setResult(4,intent);
                finish();
            }
        });
    }

    private void initView(){
        linearLayout=findViewById(R.id.selact_linearlayout);
        back=findViewById(R.id.back_from_select);
        listViewType=findViewById(R.id.listview_select);
        listViewSubType=findViewById(R.id.listview_selectsub);
        sType=findViewById(R.id.textv_select_type);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("label_return","购物消费");
                intent.putExtra("type_return","支出");
                intent.putExtra("subtype_return","日常用品");
                setResult(4,intent);
                finish();
            }
        });
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



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //TODO something
            Intent intent = new Intent();
            intent.putExtra("label_return","购物消费");
            intent.putExtra("type_return","支出");
            intent.putExtra("subtype_return","日常用品");
            setResult(4,intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public class selectAdapter extends ArrayAdapter<String> {
        private int resourceID;
        private String[] mItemList={};
        public selectAdapter(Context context, int resourceId, String[] objects) {
            super(context, resourceId, objects);
            mItemList=objects;
            resourceID=resourceId;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout imageListView;
            // 获取数据
            String type = getItem(position);
            if(convertView == null) {
                imageListView = new LinearLayout(getContext());

                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater.inflate(resourceID, imageListView, true);//把image_item.xml布局解析到LinearLayout里面
            }
            else {
                imageListView = (LinearLayout)convertView;
            }
            // 获取控件,填充数据
            TextView textView = imageListView.findViewById(R.id.textv_selecttype);
            textView.setText(type);
            return imageListView;
        }
    }
    public class subtypeAdapter extends ArrayAdapter<String> {
        private int resourceID;
        private String[] mItemList={};
        public subtypeAdapter(Context context, int resourceId, String[] objects) {
            super(context, resourceId, objects);
            mItemList=objects;
            resourceID=resourceId;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout imageListView;
            // 获取数据
            String subtype = getItem(position);
            if(convertView == null) {
                imageListView = new LinearLayout(getContext());

                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater.inflate(resourceID, imageListView, true);//把image_item.xml布局解析到LinearLayout里面
            }
            else {
                imageListView = (LinearLayout)convertView;
            }
            // 获取控件,填充数据
            TextView textView = imageListView.findViewById(R.id.text_subtype);
            textView.setText(subtype);
            return imageListView;
        }
    }
}
