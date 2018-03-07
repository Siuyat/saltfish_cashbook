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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ruanx.cashbook.Adapter.CommonAdapter;
import com.example.ruanx.cashbook.Adapter.ViewHolder;
import com.example.ruanx.cashbook.Model.Account_item;
import com.example.ruanx.cashbook.SQLite.MyDatabaseManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Administrator on 2017/12/27.
 */

public class AccountActivity extends AppCompatActivity {
    private TextView account_year;
    private Button before;
    private Button next;
    private TextView back;
    private TextView sr1,jy1,zc1;
    private SQLiteDatabase db;
    private MyDatabaseManager dbManager;
    private DecimalFormat df = new DecimalFormat("######0.00");
    private List<Account_item>data;
    private RecyclerView list;
    private CommonAdapter commonAdapter;
    private Calendar calendar;
    private String current_month;
    private String current_year;
    private LinearLayout linearLayout;
    private boolean ifclickyear = false;


    private LineChartView lineChart;

    String[] date = {"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"};//X轴标注
    private int xsize = 0;  //当前月（x轴大小）
    float[] shouru; //收入数组
    float[] zhichu; //支出数组
    float[] jieyu;  //结余数组

    private List<PointValue> mPointValues_shouru = new ArrayList<PointValue>();
    private List<PointValue> mPointValues_zhichu = new ArrayList<PointValue>();
    private List<PointValue> mPointValues_jieyu = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);
        dbManager = new MyDatabaseManager(this);
        db = dbManager.getMyDB();
        initView();
        initWindow();
        initDate();

        updateBackGround();
        getYearAccount();
        btn_click();

        recyclerview();
        getmonthaccount();

        getAxisXLables();//获取x轴的标注
        getAxisPoints();//获取坐标点
        initLineChart();//初始化

    }

    public void initView(){
        list=(RecyclerView)findViewById(R.id.rec);
        account_year=(TextView)findViewById(R.id.account_year);
        before=(Button)findViewById(R.id.before);
        next=(Button)findViewById(R.id.next);
        sr1=(TextView)findViewById(R.id.sr1);
        zc1=(TextView)findViewById(R.id.zc1);
        jy1=(TextView)findViewById(R.id.jy1);
        data=new ArrayList<>();
        back = (TextView)findViewById(R.id.back_from_account);
        linearLayout=(LinearLayout)findViewById(R.id.actact_linearlayout);
        lineChart = (LineChartView)findViewById(R.id.linechart);
    }

    final private String BACKGROUN_IMAGE_PATH="backgroudImagePath";
    private SharedPreferences pref;

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


    public void getYearAccount(){
        String year;
        //year=account_year.getText().toString();
        if(!ifclickyear){
            year = current_year;
            Log.d("current_year", "getYearAccount: "+year);
            account_year.setText(year);
        }else{
            year=account_year.getText().toString();
        }
        double yearTotal = dbManager.getYearTotal(Integer.parseInt(year));
        double yearshouru = dbManager.getYearShouru(Integer.parseInt(year));
        double balance =  yearshouru - yearTotal;
        zc1.setText(""+df.format(yearTotal));
        sr1.setText(""+df.format(yearshouru));
        jy1.setText(""+df.format(balance));
    }

    public void btn_click(){
        before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String year;
                year=account_year.getText().toString();
                int y = Integer.parseInt(year)-1;
                String y1 = Integer.toString(y);
                account_year.setText(""+y1);
                getYearAccount();
                clean_item();
                getmonthaccount();
                ifclickyear = true;

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String year;
                int y;
                year=account_year.getText().toString();
                if(year.equals(current_year)) y=Integer.parseInt(year);
                else y = Integer.parseInt(year)+1;
                String y1 = Integer.toString(y);
                account_year.setText(""+y1);
                getYearAccount();
                clean_item();
                getmonthaccount();
                ifclickyear = true;

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void recyclerview(){
        list.setLayoutManager(new LinearLayoutManager(this));
        commonAdapter=new CommonAdapter<Account_item>(this, R.layout.account_item,data) {
            @Override
            protected void convert(ViewHolder holder, Account_item item) {
                TextView month=holder.getView(R.id.yf1);
                TextView zc3=holder.getView(R.id.zc3);
                TextView sr3=holder.getView(R.id.sr3);
                TextView jy3=holder.getView(R.id.jy3);
                month.setText(item.getmonth()+"月");
                zc3.setText(item.getzc());
                sr3.setText(item.getsr());
                jy3.setText(item.getjy());
            }
        };
        list.setAdapter(commonAdapter);
    }

    public void initDate(){
        calendar = Calendar.getInstance();  //获得calendar实例
        current_month = (calendar.get(Calendar.MONTH)+1)+"";
        current_year = ""+calendar.get(Calendar.YEAR);
    }

    public void getmonthaccount(){
        String year;
        year=account_year.getText().toString();
        int j;
        if(year.equals(current_year)) j = Integer.parseInt(current_month);
        else j=12;
        double monthTotal;
        double monthShouru;
        double monthbal;
        String mon,sr,zc,jy;

        shouru = new float[j];
        zhichu = new float[j];
        jieyu = new float[j];
        xsize = j-1;

        Log.d("xsize  ", "getmonthaccount: "+xsize);

        for (int i=1;i<j+1;i++){
            monthTotal = dbManager.getMonthTotal(Integer.parseInt(year),i);
            monthShouru = dbManager.getMonthShouru(Integer.parseInt(year),i);
            monthbal = monthShouru - monthTotal;

            shouru[i-1] = new Double(monthShouru).floatValue();
            zhichu[i-1] = new Double(monthTotal).floatValue();
            jieyu[i-1] = new Double(monthbal).floatValue();

            Log.d("shouru  ", "["+(i-1)+"]="+shouru[i-1]);
            Log.d("zhichu  ", "["+(i-1)+"]="+zhichu[i-1]);
            Log.d("jieyu  ", "["+(i-1)+"]="+jieyu[i-1]);

            mon=Integer.toString(i);
            sr=String.valueOf(monthShouru);
            zc=String.valueOf(monthTotal);
            jy=String.valueOf(monthbal);
            Account_item a_item = new Account_item(mon,sr,zc,jy);
            data.add(a_item);
            commonAdapter.notifyDataSetChanged();
        }

        getAxisXLables();//获取x轴的标注
        getAxisPoints();//获取坐标点
        initLineChart();//初始化

    }

    public void clean_item(){
        int j = commonAdapter.getItemCount();
        for (int i=0;i<j;i++){
            commonAdapter.removeItem(0);
        }
    }

    /**
     * 初始化LineChart的一些设置
     */
    private void initLineChart(){
        Line line = new Line(mPointValues_shouru).setColor(Color.parseColor("#63B8FF"));  //折线的颜色
        Line line1 = new Line(mPointValues_zhichu).setColor(Color.parseColor("#FF4081"));  //折线的颜色
        Line line2 = new Line(mPointValues_jieyu).setColor(Color.parseColor("#FFA500"));  //折线的颜色

        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);//曲线是否平滑
	    line.setStrokeWidth(1);//线条的粗细，默认是3
        line.setFilled(true);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
		line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示

        line1.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.SQUARE）
        line1.setCubic(false);//曲线是否平滑
        line1.setStrokeWidth(1);//线条的粗细，默认是3
        line1.setFilled(true);//是否填充曲线的面积
        line1.setHasLabels(true);//曲线的数据坐标是否加上备注
		line1.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line1.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示
        line1.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示

        line2.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.SQUARE）
        line2.setCubic(false);//曲线是否平滑
        line2.setStrokeWidth(1);//线条的粗细，默认是3
        line2.setFilled(true);//是否填充曲线的面积
        line2.setHasLabels(true);//曲线的数据坐标是否加上备注
		line2.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line2.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示
        line2.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示

        lines.add(line);
        lines.add(line1);
        lines.add(line2);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X轴下面坐标轴字体是斜的显示还是直的，true是斜的显示
//	    axisX.setTextColor(Color.WHITE);  //设置字体颜色
        axisX.setTextColor(Color.parseColor("#222222"));//灰色

//	    axisX.setName("未来几天的天气");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        //axisX.setMaxLabelChars(12); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
//	    data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线


        Axis axisY = new Axis();  //Y轴
        axisY.setName("");//y轴标注
        axisY.setTextSize(11);//设置字体大小
        axisY.setTextColor(Color.parseColor("#222222"));//灰色
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边
        //设置行为属性，支持缩放、滑动以及平移
        //ineChart.setInteractive(true);
        //lineChart.setZoomType(ZoomType.HORIZONTAL);  //缩放类型，水平
        //lineChart.setMaxZoom((float) 3);//缩放比例
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);
        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = 0;
        //v.right= xsize+1;
        v.right = 12;
        lineChart.setCurrentViewport(v);
    }

    /**
     * X 轴的显示
     */
    private void getAxisXLables(){
        mAxisXValues.clear();
        for (int i = 0; i < date.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(date[i]));
        }
    }
    /**
     * 图表的每个点的显示
     */
    private void getAxisPoints(){
        mPointValues_jieyu.clear();
        mPointValues_shouru.clear();
        mPointValues_zhichu.clear();
        for (int i = 0; i < 12; i++) {
            if(i<xsize+1) {
                mPointValues_shouru.add(new PointValue(i, shouru[i]));
                mPointValues_zhichu.add(new PointValue(i, zhichu[i]));
                mPointValues_jieyu.add(new PointValue(i, jieyu[i]));
            } else {
                mPointValues_shouru.add(new PointValue(i,0));
                mPointValues_zhichu.add(new PointValue(i, 0));
                mPointValues_jieyu.add(new PointValue(i, 0));
            }
        }

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
