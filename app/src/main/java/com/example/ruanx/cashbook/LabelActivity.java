package com.example.ruanx.cashbook;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.ruanx.cashbook.Adapter.LabelAdapter;

public class LabelActivity extends AppCompatActivity {

    private Button zhichuBtn;
    private Button shouruBtn;
    private Button back;
    private ListView listView;
    private ListView listView_2;
    private String[] zhichu = {"一般支出","购物","餐饮","交通","娱乐","生活","文化","健康"};
    private String[] shouru = {"工作","奖金","补贴","理财","礼金","其它"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);
        initView();
        initWindow();

        zhichuBtn.setTextSize(35);
        shouruBtn.setTextSize(25);

        LabelAdapter adapter = new LabelAdapter(LabelActivity.this, R.layout.label_item,zhichu);
        listView.setAdapter(adapter);

        LabelAdapter adapter_2 = new LabelAdapter(LabelActivity.this, R.layout.label_item,shouru);
        listView_2.setAdapter(adapter_2);

        zhichuBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                listView.setVisibility(View.VISIBLE);
                listView_2.setVisibility(View.GONE);
                zhichuBtn.setTextSize(35);
                shouruBtn.setTextSize(25);
            }
        });

        shouruBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                listView.setVisibility(View.GONE);
                listView_2.setVisibility(View.VISIBLE);
                zhichuBtn.setTextSize(25);
                shouruBtn.setTextSize(35);
            }
        });

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String str = zhichu[i];
                Intent intent = new Intent();
                intent.putExtra("label_return",str);
                intent.putExtra("type_return","支出");
                setResult(4,intent);
                finish();
            }
        });


        listView_2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String str = shouru[i];
                Intent intent = new Intent();
                intent.putExtra("label_return",str);
                intent.putExtra("type_return","收入");
                setResult(4,intent);
                finish();
            }
        });

    }

    private void initView(){
        zhichuBtn = (Button)findViewById(R.id.zhichu_btn);
        shouruBtn = (Button)findViewById(R.id.shouru_btn);
        listView = (ListView)findViewById(R.id.label_ll);
        listView_2 = (ListView)findViewById(R.id.label_ll2);
        back = (Button)findViewById(R.id.back_from_label);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //TODO something
            Intent intent = new Intent();
            intent.putExtra("label_return","一般支出");
            intent.putExtra("type_return","支出");
            setResult(4,intent);
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
