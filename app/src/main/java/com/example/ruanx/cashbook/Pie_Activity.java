package com.example.ruanx.cashbook;


import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ruanx.cashbook.Model.BT_Item;
import com.example.ruanx.cashbook.SQLite.MyDatabaseManager;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2017/12/28.
 */

public class Pie_Activity extends AppCompatActivity {
    private Button btn;
    private PieChart mChart;
    private Description des;
    private PieData pieData;
    private TextView dateTv;
    private MyDatabaseManager dbManager;
    private DecimalFormat df = new DecimalFormat("######0.00");
    private LinearLayout linearLayout;
    private SQLiteDatabase db;
    private List<BT_Item>data;
    private String zzc;
    final private String BACKGROUN_IMAGE_PATH="backgroudImagePath";
    private SharedPreferences pref;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pie_chart);
        initView();
        btn_click();
        initWindow();
        init_piecahrt();
        updateBackGround();
    }

    public void initView(){
        btn = (Button) findViewById(R.id.dateBtn);
        mChart= (PieChart)findViewById(R.id.pie_chart);
        zzc="";
        dbManager = new MyDatabaseManager(this);
        db = dbManager.getMyDB();
        data=new ArrayList<>();
        dateTv = (TextView)findViewById(R.id.double_date_tv);
        back = (Button)findViewById(R.id.back_from_pie);
        linearLayout = (LinearLayout)findViewById(R.id.pie_lin);
    }

    public void btn_click(){
        btn.setOnClickListener(new View.OnClickListener() {
            Calendar c = Calendar.getInstance();
            @Override
            public void onClick(View v) {
                new DoubleDatePickerDialog(Pie_Activity.this, 0, new DoubleDatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                          int startDayOfMonth, DatePicker endDatePicker, int endYear, int endMonthOfYear,
                                          int endDayOfMonth) {
                        int s_year,s_month,s_day;
                        int e_year,e_month,e_day;
                        double all,per;
                        double ybzc,gw,cy,jt,yl,sh,wh,jk;
                        int s_weight,e_weight;
                        data=new ArrayList<>();
                        String textString = String.format("%d-%d-%d - %d-%d-%d", startYear,startMonthOfYear + 1, startDayOfMonth,endYear, endMonthOfYear + 1, endDayOfMonth);
                        s_year=startYear;s_month=startMonthOfYear;s_day=startDayOfMonth;
                        e_year=endYear;e_month=endMonthOfYear;e_day=endDayOfMonth;
                        s_weight=s_year*10000+(s_month+1)*100+s_day-1;
                        e_weight=e_year*10000+(e_month+1)*100+e_day+1;
                        all  = dbManager.getTimeTotal(s_weight,e_weight);

                        gw = dbManager.getLabelTotal(s_weight,e_weight,"购物消费");
                        per=gw/all;
                        BT_Item bt_item1 = new BT_Item(gw,per,"购物消费");
                        if (gw!=0.0)data.add(bt_item1);

                        cy = dbManager.getLabelTotal(s_weight,e_weight,"餐饮酒水");
                        per=cy/all;
                        BT_Item bt_item2 = new BT_Item(cy,per,"餐饮酒水");
                        if (cy!=0.0)data.add(bt_item2);

                        jt = dbManager.getLabelTotal(s_weight,e_weight,"交通出行");
                        per=jt/all;
                        BT_Item bt_item3 = new BT_Item(jt,per,"交通出行");
                        if (jt!=0.0)data.add(bt_item3);

                        yl = dbManager.getLabelTotal(s_weight,e_weight,"休闲娱乐");
                        per=yl/all;
                        BT_Item bt_item4 = new BT_Item(yl,per,"休闲娱乐");
                        if (yl!=0.0)data.add(bt_item4);

                        sh = dbManager.getLabelTotal(s_weight,e_weight,"居家生活");
                        per=sh/all;
                        BT_Item bt_item5 = new BT_Item(sh,per,"居家生活");
                        if (sh!=0.0)data.add(bt_item5);

                        wh = dbManager.getLabelTotal(s_weight,e_weight,"文化教育");
                        per=wh/all;
                        BT_Item bt_item6 = new BT_Item(wh,per,"文化教育");
                        if (wh!=0.0)data.add(bt_item6);

                        jk = dbManager.getLabelTotal(s_weight,e_weight,"健康医疗");
                        per=jk/all;
                        BT_Item bt_item7 = new BT_Item(jk,per,"健康医疗");
                        if (jk!=0.0)data.add(bt_item7);

                        ybzc = dbManager.getLabelTotal(s_weight,e_weight,"其它支出");
                        per=ybzc/all;
                        BT_Item bt_item = new BT_Item(ybzc,per,"其它支出");
                        if (ybzc!=0.0)data.add(bt_item);

                        zzc = String .valueOf(all);
                        init_piecahrt();
                        //btn.setText(textString);
                        dateTv.setText(textString);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), true).show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void init_piecahrt(){
        pieData= getPieData();
        initReportChart(mChart,pieData);
    }

    public void initReportChart(PieChart mChart,PieData pieData) {
        mChart.setUsePercentValues(true);
        mChart.setDescription(des);  //设置描述信息
        mChart.setExtraOffsets(5,5,5,5);  //设置间距
        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setCenterText("总支出\n"+zzc+"元");  //设置饼状图中间文字
        mChart.setCenterTextSize(20f);  //中间文字大小
        mChart.setCenterTextColor(Color.rgb(100,100,100));
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);
        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);
        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);
        mChart.setTouchEnabled(false);  //设置是否响应点击触摸
        mChart.setDrawCenterText(true);  //设置是否绘制中心区域文字
        mChart.setDrawEntryLabels(false);  //设置是否绘制标签
        mChart.setDrawMarkers(true);
        mChart.setRotationAngle(0); //设置旋转角度
        mChart.setRotationEnabled(true); //设置是否旋转
        mChart.setHighlightPerTapEnabled(false);  //设置是否高亮显示触摸的区域
        mChart.setData(pieData);  //设置数据
        mChart.setDrawMarkerViews(true);  //设置是否绘制标记
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);  //设置动画效果
        mChart.setEntryLabelColor(Color.WHITE);
        Legend mLegend = mChart.getLegend();  //设置比例图
       // mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);  //左下边显示
        //mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        mLegend.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
        mLegend.setFormSize(18f);//比例块大小
        mLegend.setTextSize(12f);
        mLegend.setXEntrySpace(10f);//设置距离饼图的距离，防止与饼图重合
        mLegend.setYEntrySpace(8f);
        //设置比例块换行...
        mLegend.setWordWrapEnabled(true);
        mLegend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        mLegend.setForm(Legend.LegendForm.SQUARE);//设置比例块形状，默认为方块
    }

    public PieData getPieData(){
        ArrayList entries =new ArrayList<>();
        ArrayList colors =new ArrayList<>();
        PieDataSet dataSet =new PieDataSet(entries,"");
        if (data.size()==0){
            entries.add(new PieEntry(100f,"暂无数据"));  //没有数据的状态给设置默认值
            colors.add(Color.GRAY);//默认为灰色
        }
        else {
           for (int i=0;i<data.size();i++){
               BT_Item item = data.get(i);
               float f = (float)item.getper();
               String t = df.format(item.getmoney())+"元";
               String s;
               if (item.getper()*100 < 10.0) s = item.getlabel()+"     "+t+"       "+df.format(item.getper()*100)+"%";
               else s = item.getlabel()+"     "+t+"     "+df.format(item.getper()*100)+"%";
               entries.add(new PieEntry(f,s));
               if (item.getlabel().equals("其它支出")) colors.add(Color.rgb(200,200,200));  //grey
               else if (item.getlabel().equals("餐饮酒水")) colors.add(Color.rgb(242,224,200)); //oitake
               else if (item.getlabel().equals("购物消费")) colors.add(Color.rgb(245,150,170)); //momo
               else if (item.getlabel().equals("休闲娱乐")) colors.add(Color.rgb(241,124,103)); //sangosyu
               else if (item.getlabel().equals("交通出行")) colors.add(Color.rgb(171,211,224)); //skyblue
               else if (item.getlabel().equals("居家生活")) colors.add(Color.rgb(198,181,215));  // water purple
               else if (item.getlabel().equals("文化教育")) colors.add(Color.rgb(108,134,171)); //sabiasagi
               else if (item.getlabel().equals("健康医疗")) colors.add(Color.rgb(115,137,158));  //hanada
           }
        }
        dataSet.setSliceSpace(0f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(colors);
        PieData data =new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.TRANSPARENT);
        mChart.highlightValues(null);
        mChart.setDrawEntryLabels(true);
        mChart.setDrawMarkerViews(true);
        return data;
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
