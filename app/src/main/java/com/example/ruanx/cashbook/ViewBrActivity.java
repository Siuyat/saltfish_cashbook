package com.example.ruanx.cashbook;

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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ruanx.cashbook.Adapter.CommonAdapter;
import com.example.ruanx.cashbook.Adapter.ViewHolder;
import com.example.ruanx.cashbook.Model.BR_item;
import com.example.ruanx.cashbook.SQLite.MyDatabaseManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G40 on 2017/12/29.
 */

public class ViewBrActivity extends AppCompatActivity {
    private TextView moneyBor;
    private TextView moneyRen;
    private Button Btn_back;
    private Button Btn_add;
    static final int addBritem=1002;
    private RecyclerView recycler_br;
    private MyDatabaseManager dbManager;
    private List<BR_item> BrList=new ArrayList<>();
    private CommonAdapter<BR_item> adapter;
    private SharedPreferences pref;
    final private String BACKGROUN_IMAGE_PATH="backgroudImagePath";
    private LinearLayout linearLayout;
    final CharSequence  choices[]={"更改为已还/已收","删除","取消"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_br);
        dbManager = new MyDatabaseManager(this);

        initView();
        initWindow();
        updateBackGround();
        recyclerview();

    }



    //从活动返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == addBritem ) {
            dataUpdate();
            adapter.notifyDataSetChanged();
            initShow();
        }
    }


    private void initShow(){

    }

    // 更新状态
    private void dataUpdate() {
        BrList=dbManager.initBrItemFromDB(BrList);
        String bor=String.valueOf(dbManager.getBorrowTotal());
        moneyBor.setText(bor);
        String ret=String.valueOf(dbManager.getRetTotal());
        moneyRen.setText(ret);
    }

    private void initView(){
        moneyBor=findViewById(R.id.money_bor);
        moneyRen=findViewById(R.id.money_ren);
        linearLayout=(LinearLayout)findViewById(R.id.actvity_view_vr_ll);

        recycler_br=findViewById(R.id.recycler_br);
        Btn_back=findViewById(R.id.br_to_main);
        Btn_add=findViewById(R.id.add_br_item);
        Btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Btn_add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ViewBrActivity.this,AddBRActivity.class);
                startActivityForResult(intent,addBritem);
            }
        });
    }

    private void recyclerview(){
        recycler_br.setLayoutManager(new LinearLayoutManager(this));
        dataUpdate();
        adapter=new CommonAdapter<BR_item>(this, R.layout.br_item,BrList) {
            @Override
            protected void convert(ViewHolder holder, BR_item br_item) {
                TextView cycle_isborrow=holder.getView(R.id.cycle_isborrow);
                TextView his_name=holder.getView(R.id.his_name);
                TextView money_br_t=holder.getView(R.id.money_br_t);
                TextView money_re=holder.getView(R.id.money_re);
                TextView comments_view_br=holder.getView(R.id.comments_view_br);
                TextView date_view_br=holder.getView(R.id.date_view_br);
                cycle_isborrow.setText(br_item.getIsBorrow());
                his_name.setText(br_item.getHisName());

                money_br_t.setText(String.valueOf(br_item.getMoney()));
                money_re.setText(br_item.getIsReturnSta());
                comments_view_br.setText(br_item.getComments());
                //date_view_br.setText("hhh");
                date_view_br.setText(""+String.valueOf(br_item.getYear())+"-"+String.valueOf(br_item.getMonth())+"-"+String.valueOf(br_item.getDay()));
                if(br_item.getIsBorrow().equals("入")){
                    cycle_isborrow.setBackgroundResource(R.drawable.shape3);
                }else{
                    cycle_isborrow.setBackgroundResource(R.drawable.shape4);
                }
            }
        };
        adapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                // no action
            }

            @Override
            public void onLongClick(final int position) {
                final BR_item br_item= BrList.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewBrActivity.this);
                //items使用全局的finalCharSequence数组声明
                builder.setItems(choices,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String select_item = choices[which].toString();
                        switch (which){
                            case 0:
                                //更改为已还款/已收款
                                //把未完成的借入借出改为已完成
                                br_item.setIsReturn("Y");
                                adapter.notifyDataSetChanged();
                                //更改数据库
                                dbManager.updateIsReturn(br_item);
                                //更新借款和欠款
                                String bor=String.valueOf(dbManager.getBorrowTotal());
                                moneyBor.setText(bor);
                                String ret=String.valueOf(dbManager.getRetTotal());
                                moneyRen.setText(ret);
                                dialog.dismiss();
                                break;
                            case 1:
                                //删除该条目
                                adapter.removeItem(position);
                                //更改数据库
                                dbManager.deleteBrItemFromDB(br_item.getBrItemId());
                                //更新借款和欠款
                                String bor1=String.valueOf(dbManager.getBorrowTotal());
                                moneyBor.setText(bor1);
                                String ret1=String.valueOf(dbManager.getRetTotal());
                                moneyRen.setText(ret1);
                                dialog.dismiss();
                                break;
                            case 2://取消
                                dialog.dismiss();
                                break;
                            default:
                                dialog.dismiss();
                                break;
                        }
                    }
                });
                builder.show();

            }
        });
        recycler_br.setAdapter(adapter);
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
