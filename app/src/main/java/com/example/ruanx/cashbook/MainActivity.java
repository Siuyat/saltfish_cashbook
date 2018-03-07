package com.example.ruanx.cashbook;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.Output;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ruanx.cashbook.Model.Item;
import com.example.ruanx.cashbook.Adapter.ItemAdapter;
import com.example.ruanx.cashbook.SQLite.MyDatabaseManager;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final int permissionRequestCode=2018;
    final private String BACKGROUN_IMAGE_PATH="backgroudImagePath";
    final private int AddItem  =1;
    final private int ChangeBackground =3;
    private MyDatabaseManager dbManager;
    private Button ssBtn;
    private Button addBtn;
    private Button beijingBtn;
    private Button jkBtn;
    private Button exBtn;
    private Button pieChartBtn;
    private Button exchangeBtn;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<Item> mItemList = new ArrayList<>();
    private Calendar calendar;
    private TextView dateTV;
    private TextView yueshouruTV;
    private TextView yueeduTV;
    private TextView yueeduNumTV;
    private TextView yueshouruNumTV;
    private TextView jinrihuafeiTV;
    private String month;   //月
    private String year;    //年
    private String day;     //日
    private String nthdayofyear;    //一年的第几天
    private String weekday;     //周几
    private DecimalFormat df = new DecimalFormat("######0.00");  //保留两位小数

    private SharedPreferences pref;
    private DrawerLayout mDrawerLayout;
    private Button showmeun;
    private Button password;




    private FrameLayout frameLayout;
    private ConstraintLayout middlecons;
    private LinearLayout mainlin;
    private Button ls;
    //获取手机本身存储根目录
    private String absolutePath="";//绝对
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        absolutePath=Environment.getExternalStoragePublicDirectory("").getAbsolutePath();//绝对

        //初始化数据库
        dbManager = new MyDatabaseManager(this);
        initView();
        initWindow();//状态栏透明
        initDate();

        initShow(); //显示各计数额
        dataUpdate();//从数据库读ArrayList
        updateBackGround();//更新主题
        initshowmenu();//显示侧滑菜单
        initdraw();//对侧滑菜单进行初始化
        login();
        readAccount();
        

        //添加条目
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //      Toast.makeText(MainActivity.this, "Fab click", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, AddItemActivity.class);
                i.putExtra("item_id", -1);//传值-1，则为新项目
                startActivityForResult(i,AddItem);
            }
        });

        //换背景
        beijingBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(MainActivity.this,BackGroundActivity.class);
                startActivityForResult(i,ChangeBackground);
            }
        });

        //汇率助手
        exchangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ExchangeActivity.class);
                startActivity(i);
            }
        });

        //搜索记账条目
        ssBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(i);
            }
        });

        //饼图
        pieChartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,Pie_Activity.class);
                startActivity(i);
            }
        });


        //导出账单
        exBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,OutputActivity.class);
                startActivity(i);
                /*
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        ||ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE }, permissionRequestCode);
                } else {
                    dbManager.ex(absolutePath);
                }
                */
            }
        });

        //查看我的借款
        jkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,ViewBrActivity.class);
                startActivity(i);
            }
        });

         /* RecyclerView单击事件，跳转界面 */
        adapter.setRecyclerViewOnItemClickListener(new ItemAdapter.RecyclerViewOnItemClickListener(){
            @Override
            public void onItemClickListener(View view, int position) {
                Item item = mItemList.get(position);
                int itemid = item.getItemId();
                Log.d("item id", "onItemClickListener: "+itemid);
                Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
                intent.putExtra("item_id",itemid);
                startActivityForResult(intent,AddItem);
            }
        });

        ls.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(MainActivity.this,AccountActivity.class);
                startActivity(i);
            }
        });

        /* RecyclerView长按事件，删除item */
        adapter.setOnItemLongClickListener(new ItemAdapter.RecyclerViewOnItemLongClickListener(){
            @Override
            public boolean onItemLongClickListener(View view, final int position){
                AlertDialog.Builder delete_alertDialog = new AlertDialog.Builder(MainActivity.this);
                delete_alertDialog.setTitle("移除条目")
                        .setMessage("确认删除?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Item item = mItemList.get(position);
                                dbManager.deleteItemFromDB(item);   //从数据库中删除人物
                                adapter.removeItem(position);
                                initShow(); //更新界面有关金额部分
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {}
                        });
                delete_alertDialog.create().show();
                return true;
            }
        });

    }

    private void initView(){
        addBtn = (Button)findViewById(R.id.but_additem);
        dateTV = (TextView)findViewById(R.id.date_tv);
        yueshouruTV = (TextView)findViewById(R.id.yueshouru_tv);
        yueeduTV = (TextView)findViewById(R.id.yueedu_tv);
        jinrihuafeiTV = (TextView)findViewById(R.id.jinricount_tv);
        yueshouruNumTV = (TextView)findViewById(R.id.yueshouru_num_tv);
        yueeduNumTV = (TextView)findViewById(R.id.yueedu_num_tv);
        beijingBtn = (Button)findViewById(R.id.but_bg);
        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawerlayout);
        frameLayout=findViewById(R.id.drawerlayout_frame);
        password=(Button)findViewById(R.id.but_mm);
        middlecons = (ConstraintLayout)findViewById(R.id.main_middlecons);
        mainlin=(LinearLayout)findViewById(R.id.main_lin);
        ls=(Button)findViewById(R.id.but_ls);
        exchangeBtn=(Button)findViewById(R.id.but_cur);
        ssBtn=findViewById(R.id.but_ss);
        jkBtn=findViewById(R.id.but_jk);
        exBtn=findViewById(R.id.but_ex);
        pieChartBtn=findViewById(R.id.but_bt);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter =  new ItemAdapter(mItemList);
        recyclerView.setAdapter(adapter); //默认
    }

    // 更新list
    private void dataUpdate() {
        mItemList=dbManager.initItemFromDB(mItemList);
      //  adapter =  new ItemAdapter(mItemList);
      //  recyclerView.setAdapter(adapter);
    }

    private void updateBackGround(){
        pref = getSharedPreferences("background",MODE_PRIVATE);
        String bgpath = pref.getString(BACKGROUN_IMAGE_PATH,"1");
        switch (bgpath) {
            case "1":
                frameLayout.setBackgroundResource(R.mipmap.darksky_ver);
                Log.d("main background", "onCreate: " + 1);
                break;
            case "2":
                frameLayout.setBackgroundResource(R.mipmap.forest_b_ver);
                Log.d("main background", "onCreate: " + 2);
                break;
            case "3":
                frameLayout.setBackgroundResource(R.mipmap.dusk_ver);
                Log.d("main background", "onCreate: " + 3);
                break;
            case "4":
                frameLayout.setBackgroundResource(R.mipmap.map_ver);
                Log.d("main background", "onCreate: " + 4);
                break;
            default:
                Bitmap bitmap = BitmapFactory.decodeFile(bgpath);
                if(bitmap!=null) {
                        frameLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
                        //把bitmap转为drawable,layout为xml文件里的主layout
                }
                else{
                    frameLayout.setBackgroundResource(R.mipmap.darksky_ver);
                    pref.edit().putString(BACKGROUN_IMAGE_PATH,"1");
                    Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    //从活动返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddItem ) {
            dataUpdate();
            adapter.notifyDataSetChanged();
            initShow();
        } else if(requestCode == ChangeBackground){
            //换背景
            updateBackGround();
        }
    }

    //刷新有关日期的部分
    private void initDate(){
        calendar = Calendar.getInstance();  //获得calendar实例
        weekday = printDayOfWeek();
        month = (calendar.get(Calendar.MONTH)+1)+"";
        day = ""+calendar.get(Calendar.DATE);
        year = ""+calendar.get(Calendar.YEAR);
        nthdayofyear=""+calendar.get(Calendar.DAY_OF_YEAR);
        //dateTV.setText(year+"-"+month+"-"+day+"，今年的第"+nthdayofyear+"天，"+weekday);//2017-12-23 第300天 周几
        dateTV.setText(year+"-"+month+"-"+day);
        yueshouruTV.setText(month+"月收入");
        yueeduTV.setText(month+"月支出");
    }

    //刷新有关主界面金额的部分
    private void initShow(){
        double dayTotal = dbManager.getDayTotal(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day));
        double monthTotal = dbManager.getMonthTotal(Integer.parseInt(year),Integer.parseInt(month));
        double monthShouru = dbManager.getMonthShouru(Integer.parseInt(year),Integer.parseInt(month));
        Log.d("monthtotal", "initShow: "+df.format(monthTotal));
        jinrihuafeiTV.setText(""+df.format(dayTotal));
        yueeduNumTV.setText(""+df.format(monthTotal));
        yueshouruNumTV.setText(""+df.format(monthShouru));
    }

    //得到日期，周几
    private String printDayOfWeek() {
        String day = "";
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                day = "周日";
                break;
            case Calendar.MONDAY:
                day = "周一";
                break;
            case Calendar.TUESDAY:
                day ="周二";
                break;
            case Calendar.WEDNESDAY:
                day = "周三";
                break;
            case Calendar.THURSDAY:
                break;
            case Calendar.FRIDAY:
                day = "周五";
                break;
            case Calendar.SATURDAY:
                day ="周六";
                break;
            default:
                break;
        }
        return day;
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

    private void initshowmenu(){
        showmeun = (Button)findViewById(R.id.but_caidan);
        showmeun.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
    }

    private void initdraw()
    {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                drawerView.setClickable(true);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
            }
            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mDrawerLayout!=null){
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
                mDrawerLayout.closeDrawers();
            }else super.onBackPressed();
        }
    }

    public void login(){
        password.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,Password_Actiivity.class);
                i.putExtra("judge1", 5);
                startActivity(i);
            }
        });
    }

    public void readAccount() {
        Intent intent = this.getIntent();
        int Index = intent.getIntExtra("judge", 0);
        //创建SharedPreferences对象
        SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
        //获得保存在SharedPredPreferences中的密码
        String flag = sp.getString("flag", "");
        if(flag.equals("yes")&& Index!=5 ){
            Intent i = new Intent(MainActivity.this,Password_Actiivity.class);
            startActivity(i);
        }
    }
}
