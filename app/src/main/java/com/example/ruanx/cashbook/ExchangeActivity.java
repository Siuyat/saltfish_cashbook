package com.example.ruanx.cashbook;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ruanx.cashbook.Adapter.CommonAdapter;
import com.example.ruanx.cashbook.Adapter.ViewHolder;
import com.example.ruanx.cashbook.Model.ExchangeRate_item;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by G40 on 2018/1/2.
 */

public class ExchangeActivity extends AppCompatActivity {
    private TextView latestTime;
    private Button Btn_back;
    private EditText input;
    private Button Btn_check;
    //private TextView dub;
    private RecyclerView recycler_ex;
    private List<ExchangeRate_item> rList=new ArrayList<>();
    private List<ExchangeRate_item> list=new ArrayList<>();
    private CommonAdapter<ExchangeRate_item> adapter;
    private String updateTime="";
    private String reason="";
    private String errorcode="";
    //private String result="";
    final static  private String AppKey="4946d6011c79889b71af17c014ca068a";
    final static private String exchangeURL="http://op.juhe.cn/onebox/exchange/query?key="+AppKey;//请求接口地址
    private double num = 100;
    private DecimalFormat df = new DecimalFormat("######0.00");  //保留两位小数


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        initView();
        initWindow();
        recyclerview();
        sendRequestWithOkHttp();

        Btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String in = input.getText().toString();
                if(in.equals("")){
                    Toast.makeText(ExchangeActivity.this,"请输入金额！",Toast.LENGTH_SHORT).show();
                }else{
                    num = Double.valueOf(in);   //得到数值
                    latestTime.setText("正在查询，这可能要花几分钟时间，请稍候");
                    sendRequestWithOkHttp();
                }
            }
        });
    }



    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(exchangeURL)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithJSONObject(responseData);
                    showResponse();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    private void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONObject jsonObject=new JSONObject(jsonData);
            errorcode=String.valueOf(jsonObject.getInt("error_code"));
            if(errorcode.equals("0")){
                JSONObject cnt=jsonObject.getJSONObject("result");
                updateTime=cnt.getString("update");
               // String l=cnt.getString("list");
                rList=new ArrayList<>();
                rList=splitListToList(cnt.getString("list"));
               // StringBuilder res= resl(cnt.getString("list"));
            }
            else {
                reason=errorcode+jsonObject.getString("reason");
                latestTime.setText(reason);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private List<ExchangeRate_item> splitListToList(String list){
        List<ExchangeRate_item> l=new ArrayList<>();
        //l.add(new ExchangeRate_item("人民币","100.00"));
        String[] res ={},subres={};
        StringBuilder sb=new StringBuilder();
        String subString1=list.substring(1,list.length()-2);
        res=subString1.split("],");

        for(int i=0;i<res.length;i++){
            subres=res[i].substring(1).split(",");
            String name=subres[0];
            String rate=subres[2];
            rate = rate.substring(1,rate.length()-1);
            double dd = num * (100/Double.parseDouble(rate));
            String rate2 = df.format(dd);
            l.add(new ExchangeRate_item(name.substring(1,name.length()-1),rate2));
           // l.add(new ExchangeRate_item(name.substring(1,name.length()-1),rate.substring(1,rate.length()-1)));
          //  l.add(new ExchangeRate_item(name.substring(1,name.length()-1),rate.substring(1,rate.length()-1)));
        }

        return l;
    }


//    private StringBuilder resl(String list){
//        String[] res ={},subres={};
//        StringBuilder sb=new StringBuilder();
//        String subString1=list.substring(1,list.length()-2);
//        res=subString1.split("],");
//
//        for(int i=0;i<res.length;i++){
//            subres=res[i].substring(0).split(",");
//
//        }
//
//        return sb;
//    }


    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在这里进行UI操作，将结果显示到界面上
                latestTime.setText("最后更新时间："+updateTime+" 数据仅供参考\n"+reason+"");
                //latestTime.setText("最后更新时间："+updateTime);
                //adapter.notifyDataSetChanged();
                recyclerview();
            }
        });
    }





    private void recyclerview(){
        recycler_ex.setLayoutManager(new LinearLayoutManager(this));
        adapter=new CommonAdapter<ExchangeRate_item>(this, R.layout.item_exchange,rList) {
            @Override
            protected void convert(ViewHolder holder, ExchangeRate_item item) {
                TextView name=holder.getView(R.id.currency_name);
                TextView rate=holder.getView(R.id.exchange_rate);
                name.setText(item.getCurrencyName());
                rate.setText(item.getXianhuimairujia());
            }
        };
        recycler_ex.setAdapter(adapter);
    }


    private void initView(){
        latestTime=findViewById(R.id.latest_time);
        recycler_ex=findViewById(R.id.recycler_exchange);
        Btn_back=findViewById(R.id.ex_to_main);
        //dub=findViewById(R.id.dubgu);
        Btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        input=(EditText)findViewById(R.id.exchange_input_et);
        Btn_check=(Button)findViewById(R.id.exchange_btn);
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
