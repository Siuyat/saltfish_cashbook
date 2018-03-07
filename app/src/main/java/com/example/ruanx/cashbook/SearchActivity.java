package com.example.ruanx.cashbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ruanx.cashbook.Model.Item;
import com.example.ruanx.cashbook.SQLite.MyDatabaseHelper;
import com.example.ruanx.cashbook.SQLite.MyDatabaseManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G40 on 2017/11/21.
 */

public class SearchActivity extends AppCompatActivity {
    /**
     * 数据库名
     */
  //  static final String DB_NAME = "myCashBook.db";
    /**
     * 表名
     */
    static final private  String TABLE_NAME="cash";
    public MyDatabaseManager dbManager;
    private SearchView mSearchView;
    private ListView mListView;
    private Button back;
    //private Typeface typeface;
    private LinearLayout searchActivityLayout;
    SQLiteDatabase db;
    private FilterAdapter adapter;
    private MyDatabaseHelper dbHelper;
    private List<Item> mItemList=new ArrayList<>();
    private List<Item> mItemListRes=new ArrayList<>();


    final private String BACKGROUN_IMAGE_PATH="backgroudImagePath";
    private int bgid;   // 1 2 3 4 对应4套主题，数字存储在SF中。
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        dbManager = new MyDatabaseManager(this);
       // dbHelper = new MyDatabaseHelper(this,DB_NAME,null,1);
        db = dbManager.getMyDB();
        mItemList=dbManager.initItemFromDB(mItemList);
        initView();
        initWindow();
        refreshResult();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Item item = mItemListRes.get(i);
               // Intent intent = new Intent(SearchActivity.this, ItemInfoActivity.class);
                Intent intent = new Intent(SearchActivity.this, AddItemActivity.class);
                int itemid =item.getItemId();
                intent.putExtra("item_id",itemid);
                startActivity(intent);
            }
        });
        // 设置搜索文本监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    mItemListRes=FilterText(newText);
                } else {
                    mItemListRes=new ArrayList<Item>();
                }
                refreshResult();
                return false;
            }
        });

    }

    private void refreshResult(){
        adapter=new FilterAdapter(getBaseContext(), R.layout.item_result, mItemListRes);
        mListView.setAdapter(adapter);
    }

    private void initView(){
        searchActivityLayout=findViewById(R.id.SearchActivityLayout);
        mSearchView = (SearchView) findViewById(R.id.searchView);
        mListView = (ListView) findViewById(R.id.listView);
        //updateBackGround();
        back=(Button)findViewById(R.id.backToMain);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
                searchActivityLayout.setBackgroundResource(R.mipmap.darksky_ver);
                Log.d("main background", "onCreate: " + 1);
                break;
            case "2":
                searchActivityLayout.setBackgroundResource(R.mipmap.forest_b_ver);
                Log.d("main background", "onCreate: " + 2);
                break;
            case "3":
                searchActivityLayout.setBackgroundResource(R.mipmap.dusk_ver);
                Log.d("main background", "onCreate: " + 3);
                break;
            case "4":
                searchActivityLayout.setBackgroundResource(R.mipmap.map_ver);
                Log.d("main background", "onCreate: " + 4);
                break;
            default:
                Bitmap bitmap = BitmapFactory.decodeFile(bgpath);
                if(bitmap!=null) {
                    searchActivityLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
                    //把bitmap转为drawable,layout为xml文件里的主layout
                }
                else{
                    searchActivityLayout.setBackgroundResource(R.mipmap.darksky_ver);
                    pref.edit().putString(BACKGROUN_IMAGE_PATH,"1");
                    Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /*搜索条件  可以改*/
    public List<Item> FilterText(String filterString){
        List<Item> mPersonListR=new ArrayList<>();
        for(int i=0;i<mItemList.size();i++) {
            if (mItemList.get(i).getComments().contains(filterString) ||
                    mItemList.get(i).getType().contains(filterString) ||
                    mItemList.get(i).getLabel().contains(filterString) ||
                    mItemList.get(i).getSubtype().contains(filterString) ||
                    String.valueOf(mItemList.get(i).getMoney()).contains(filterString)  )
            {
                mPersonListR.add(mItemList.get(i));
            }
        }
        return mPersonListR;
    }




    @Override
    protected void onDestroy(){
        super.onDestroy();
    }



    public class FilterAdapter extends ArrayAdapter<Item> {
        private int resourceID;
        private List<Item> mItemList=new ArrayList<>();
        public FilterAdapter(Context context, int resourceId, List<Item> objects) {
            super(context, resourceId, objects);
            mItemList=objects;
            resourceID=resourceId;
          //  typeface = ResourcesCompat.getFont(context, R.font.fanti);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout imageListView;
            // 获取数据
            Item item = getItem(position);
            String typeImage = item.getType();
            String typeName = item.getLabel();
            String typeSub=item.getSubtype();
            String comment=item.getComments();
            String cost=Double.toString(item.getMoney());
            String date=""+item.getYear()+"-"+item.getMonth()+"-"+item.getDay();
            // 系统显示列表时，首先实例化一个适配器（这里将实例化自定义的适配器）。
            // 当手动完成适配时，必须手动映射数据，这需要重写getView（）方法。
            // 系统在绘制列表的每一行的时候将调用此方法。
            // getView()有三个参数，
            // position表示将显示的是第几行，
            // covertView是从布局文件中inflate来的布局。
            // 我们用LayoutInflater的方法将定义好的image_item.xml文件提取成View实例用来显示。
            // 然后将xml文件中的各个组件实例化（简单的findViewById()方法）。
            // 这样便可以将数据对应到各个组件上了。
            //
            if(convertView == null) {
                imageListView = new LinearLayout(getContext());

                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater.inflate(resourceID, imageListView, true);//把image_item.xml布局解析到LinearLayout里面
            }
            else {
                imageListView = (LinearLayout)convertView;
            }

            // 获取控件,填充数据
            TextView textView_typeImage = imageListView.findViewById(R.id.type_image);
            TextView  textView_typeName= imageListView.findViewById(R.id.type_name);
            TextView  textView_comment= imageListView.findViewById(R.id.comment);
            TextView  textView_cost= imageListView.findViewById(R.id.cost);
            TextView textView_subtype=imageListView.findViewById(R.id.subtype_name);
            TextView textView_date=imageListView.findViewById(R.id.date_search_tv);

            if(typeName.equals("收入")){
                textView_typeImage.setText("收");
                textView_typeImage.setBackgroundResource(R.drawable.shape4);
            }else{
                textView_typeImage.setText("支");
                textView_typeImage.setBackgroundResource(R.drawable.shape3);
            }
            textView_typeName.setText(typeName);
            textView_comment.setText(comment);
            textView_cost.setText(cost);
            textView_subtype.setText(typeSub);
            textView_date.setText(date);
            return imageListView;
        }
    }

}