package com.example.ruanx.cashbook.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.example.ruanx.cashbook.Model.BR_item;
import com.example.ruanx.cashbook.Model.Item;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Created by SIRIUS on 2017/11/16.
 * database的管理类
 */

public class MyDatabaseManager {

    private MyDatabaseHelper helper;
    private SQLiteDatabase db;
    public MyDatabaseManager(Context context) {
        helper = new MyDatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    /**
     * add item to db
     */
    public void addItemToDB(Item item){
        ContentValues values = new ContentValues();
        values.put("label", item.getLabel());
        values.put("comments",item.getComments());
        values.put("type",item.getType());
        values.put("subtype",item.getSubtype());
        values.put("day",item.getDay());
        values.put("month",item.getMonth());
        values.put("year",item.getYear());
        values.put("weight",item.getWeight());
        values.put("money",item.getMoney());
        db.insert("cash",null,values);//插入数据
        values.clear();
    }

    /**
     * add BRitem to db
     */
    public void addBRItemToDB(BR_item br_item){
        ContentValues values = new ContentValues();
        values.put("isborrow", br_item.getIsBorrow());
        values.put("hisname",br_item.getHisName());
        values.put("isreturn", br_item.getIsReturn());
        values.put("comments",br_item.getComments());
        values.put("day",br_item.getDay());
        values.put("month",br_item.getMonth());
        values.put("year",br_item.getYear());
        values.put("weight",br_item.getWeight());
        values.put("money",br_item.getMoney());
        db.insert("br",null,values);//插入数据
        values.clear();
    }

    public List<Item> initItemFromDB(List<Item> mItemList){
        if(!mItemList.isEmpty()){
            mItemList.clear();  //清空
        }
        //查询表中所有数据
        //按weight倒顺排序，日期最新在前面。
        Cursor cursor = db.rawQuery("select * from cash order by weight desc,id desc;",null);
        if(cursor.moveToFirst()){
            do{
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String subtype = cursor.getString(cursor.getColumnIndex("subtype"));
                String label = cursor.getString(cursor.getColumnIndex("label"));
                String comments = cursor.getString(cursor.getColumnIndex("comments"));
                int day = cursor.getInt(cursor.getColumnIndex("day"));
                int month = cursor.getInt(cursor.getColumnIndex("month"));
                int year = cursor.getInt(cursor.getColumnIndex("year"));
                double money = cursor.getDouble(cursor.getColumnIndex("money"));
                Item item = new Item(type,label,subtype,comments,day,month,year,money);
                item.setItemId(cursor.getInt(cursor.getColumnIndex("id")));
                mItemList.add(item);
                Log.d("fromDB", "initItemFromDB: "+label);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return mItemList;
    }





    public List<BR_item> initBrItemFromDB( List<BR_item> mItemList){
        if(!mItemList.isEmpty()){
            mItemList.clear();  //清空
        }
        //查询表中所有数据
        //按weight倒顺排序，日期最新在前面。

        Cursor cursor = db.rawQuery("select * from br order by weight desc,id desc;",null);
        if(cursor.moveToFirst()){
            do{
                String isborrow = cursor.getString(cursor.getColumnIndex("isborrow"));
                String isreturn = cursor.getString(cursor.getColumnIndex("isreturn"));
                String hisname = cursor.getString(cursor.getColumnIndex("hisname"));
                String comments = cursor.getString(cursor.getColumnIndex("comments"));
                int day = cursor.getInt(cursor.getColumnIndex("day"));
                int month = cursor.getInt(cursor.getColumnIndex("month"));
                int year = cursor.getInt(cursor.getColumnIndex("year"));
                int id=cursor.getInt(cursor.getColumnIndex("id"));
                double money = cursor.getDouble(cursor.getColumnIndex("money"));
                BR_item item = new BR_item(isborrow,hisname,isreturn,money,comments,day,month,year);
                item.setBrItemId(id);
                mItemList.add(item);
                Log.d("fromDB", "initItemFromDB: "+comments);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return mItemList;
    }


    /**
     * 得到某年某月的总花费
     */
    public double getMonthTotal(int year, int month){
        String sql = "select sum(money) from cash where year = "+year+" and month = "+month+" and type = '支出';";
        Cursor cursor = db.rawQuery(sql,null);
        double ans = 0;
        if(cursor.moveToFirst()){
            ans = cursor.getDouble(0);
        }
        return ans;
    }

    /**
     * 得到某年某月某日的总花费
     */
    public double getDayTotal(int year, int month, int day){
        String sql = "select sum(money) from cash where year = "+year+" and month = "+month+" and day = "+day+" and type = '支出';";
        Cursor cursor = db.rawQuery(sql,null);
        double ans = 0;
        if(cursor.moveToFirst()){
            ans = cursor.getDouble(0);
        }
        return ans;
    }

    /**
     * 得到欠款
     */
    public double getBorrowTotal(){
        String sql = "select sum(money) from br where isborrow ='入'  and isreturn = 'N';";
        Cursor cursor = db.rawQuery(sql,null);
        double ans = 0;
        if(cursor.moveToFirst()){
            ans = cursor.getDouble(0);
        }
        return ans;
    }

    /**
     * 得到借款
     */
    public double getRetTotal(){
        String sql = "select sum(money) from br where isborrow ='出'  and isreturn = 'N';";
        Cursor cursor = db.rawQuery(sql,null);
        double ans = 0;
        if(cursor.moveToFirst()){
            ans = cursor.getDouble(0);
        }
        return ans;
    }

    /**
     * 删除借款和欠款
     */
    public void deleteBrItemFromDB(int BritemId){
        String sql = "delete from br where id = '"+BritemId+"';";
        db.execSQL(sql);
    }

    /**
     * 得到某年某月收入
     * @param year
     * @param month
     * @return ans
     */
    public double getMonthShouru(int year, int month){
        String sql = "select sum(money) from cash where year = "+year+" and month = "+month+" and type = '收入';";
        Cursor cursor = db.rawQuery(sql,null);
        double ans = 0;
        if(cursor.moveToFirst()){
            ans = cursor.getDouble(0);
        }
        return ans;
    }

    /**
     * 得到某年收入
     */
    public double getYearShouru(int year){
        String sql = "select sum(money) from cash where year = "+year+" and type = '收入';";
        Cursor cursor = db.rawQuery(sql,null);
        double ans = 0;
        if(cursor.moveToFirst()){
            ans = cursor.getDouble(0);
        }
        return ans;
    }

    /**
     * 得到某年花费
     */
    public double getYearTotal(int year){
        String sql = "select sum(money) from cash where year = "+year+" and type = '支出';";
        Cursor cursor = db.rawQuery(sql,null);
        double ans = 0;
        if(cursor.moveToFirst()){
            ans = cursor.getDouble(0);
        }
        return ans;
    }

    /**
     * 得到某分类的ArrayList，按日期降序排列
     */
    public List<Item> getItemFromDBByLabel(List<Item> mItemList, String searchkey){
        if(!mItemList.isEmpty()){
            mItemList.clear();  //清空
        }
        //查询表中所有数据，按weight倒顺排序，日期最新在前面。
        String sql = "select * from cash where label = '"+searchkey+"'order by weight desc,id desc;";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor.moveToFirst()){
            do{
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String subtype = cursor.getString(cursor.getColumnIndex("subtype"));
                String label = cursor.getString(cursor.getColumnIndex("label"));
                String comments = cursor.getString(cursor.getColumnIndex("comments"));
                int day = cursor.getInt(cursor.getColumnIndex("day"));
                int month = cursor.getInt(cursor.getColumnIndex("month"));
                int year = cursor.getInt(cursor.getColumnIndex("year"));
                double money = cursor.getDouble(cursor.getColumnIndex("money"));
                Item item = new Item(type,label,subtype,comments,day,month,year,money);
                mItemList.add(item);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return mItemList;
    }

    /**
     * 得到最常用label
     */
    public String[] getMostUseLabel(){
        String[] str = {"支出","购物消费","日常用品"};
        String sql = "select type, label, subtype, count(subtype) from cash group by subtype order by count(subtype) desc";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor.moveToFirst()){
            String type = cursor.getString(cursor.getColumnIndex("type"));
            String subtype = cursor.getString(cursor.getColumnIndex("subtype"));
            String label = cursor.getString(cursor.getColumnIndex("label"));
            str[0] = type;  str[1] = label; str[2] = subtype;
            String c = cursor.getString(cursor.getColumnIndex("count(subtype)"));
            Log.d("count", "getMostUseLabel: "+c);
        }
        cursor.close();
        return str;
    }


    /**
     * 更改借款还款状态
     */
    public void updateIsReturn(BR_item brItem){
        ContentValues values = new ContentValues();
        values.put("isborrow", brItem.getIsBorrow());
        values.put("comments",brItem.getComments());
        values.put("isreturn",brItem.getIsReturn());
        values.put("hisname",brItem.getHisName());
        values.put("day",brItem.getDay());
        values.put("month",brItem.getMonth());
        values.put("year",brItem.getYear());
        values.put("weight",brItem.getWeight());
        values.put("money",brItem.getMoney());
        db.update("br", values, "id=?", new String[]{""+brItem.getBrItemId()});
        values.clear();
    }





    /**
     * update Item
     */
    public void updateItem(int itemid, Item item){
        ContentValues values = new ContentValues();
        values.put("label", item.getLabel());
        values.put("comments",item.getComments());
        values.put("type",item.getType());
        values.put("subtype",item.getSubtype());
        values.put("day",item.getDay());
        values.put("month",item.getMonth());
        values.put("year",item.getYear());
        values.put("weight",item.getWeight());
        values.put("money",item.getMoney());
        db.update("cash", values, "id=?", new String[]{""+itemid});
        values.clear();
    }


    /**
     * delete item
     */
    public void deleteItemFromDB(Item item){
        int id = item.getItemId();
        String sql = "delete from cash where id = '"+id+"';";
        db.execSQL(sql);
    }

    /**
     * 依据id返回ITEM
     */
    public Item getItemByID(int id){
        String sql = "select * from cash where id = "+id;
        Cursor cursor = db.rawQuery(sql,null);
        cursor.moveToFirst();
        String type = cursor.getString(cursor.getColumnIndex("type"));
        String subtype = cursor.getString(cursor.getColumnIndex("subtype"));
        String label = cursor.getString(cursor.getColumnIndex("label"));
        String comments = cursor.getString(cursor.getColumnIndex("comments"));
        int day = cursor.getInt(cursor.getColumnIndex("day"));
        int month = cursor.getInt(cursor.getColumnIndex("month"));
        int year = cursor.getInt(cursor.getColumnIndex("year"));
        double money = cursor.getDouble(cursor.getColumnIndex("money"));
        Item item = new Item(type,label,subtype,comments,day,month,year,money);
        return item;
    }


    public MyDatabaseHelper getMyDatabaseHelper(){
        return helper;
    }
    public SQLiteDatabase getMyDB(){
        return db;
    }

    //得到一段时间内的总花费
    public double getTimeTotal(int s_weight, int e_weight){
        String sql = "select sum(money) from cash where weight > "+s_weight+" and weight < "+e_weight+" and type = '支出';";
        Cursor cursor = db.rawQuery(sql,null);
        double ans = 0;
        if(cursor.moveToFirst()){
            ans = cursor.getDouble(0);
        }
        return ans;
    }

    public double getLabelTotal(int s_weight, int e_weight,String label){
        String sql = "select sum(money) from cash where label = '"+label+"' and weight > "+s_weight+" and weight < "+e_weight+" and type = '支出';";
        Cursor cursor = db.rawQuery(sql,null);
        double ans = 0;
        if(cursor.moveToFirst()){
            ans = cursor.getDouble(0);
        }
        return ans;
    }


    /*
    public void ex(String path){
        SQLiteExportExcel test=new SQLiteExportExcel(db,path);
       // test.exportData();
    }
    */

    public void exPartTime(String path, int begin, int end){
        SQLiteExportExcel test=new SQLiteExportExcel(db,path,begin,end);
        test.exportData(begin, end);
    }



    public class SQLiteExportExcel {
        private String mDestXmlFilename;
        private SQLiteDatabase mDb;
        private int begin;
        private int end;
        String path;

        public SQLiteExportExcel(SQLiteDatabase db, String apath, int begin, int end) {
            mDb = db;
            path = apath;
            this.begin = begin;
            this.end = end;
        }

        public void exportData( int begin, int end ) {

            try {
                writeExcel("cash");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 生成一个Excel文件
         *
         * @paramfileName
         *            要生成的Excel文件名
         */
        public void writeExcel(String tableName) {
            WritableWorkbook wwb = null;
            String fileName;

            fileName = path+"/" + tableName + ".xls";
            Log.d("写路径", "writeExcel: "+fileName);

            //String sql = "select * from " + tableName+" order by weight,id";
            String sql = "select * from " + tableName+" where weight > "+begin+" and weight < "+end+" order by weight,id";

            Cursor cur = mDb.rawQuery(sql, new String[0]);
            int numcols = cur.getColumnCount();
            int numrows = cur.getCount();
            String records[][] = new String[numrows + 1][6];// 存放答案，多一行标题行
            records[0]=new String[]{"收入/支出","日期","条目","明细","备注","金额"};
            // c 0-9
            // id type weight label subtype comment  money year month day
            int r = 1;
            if (cur.moveToFirst()) {
                while (cur.getPosition() < cur.getCount()) {
                    for (int c = 0; c < 6; c++) {
                        records[r][c]=cur.getString(c+1);
                    }
                    cur.moveToNext();
                    r++;
                }

                cur.close();
            }
            try {
                //首先要删除已经存在的xls文件
                deleteXslFile(tableName+".xls");
                // 首先要使用Workbook类的工厂方法创建一个可写入的工作薄(Workbook)对象
                wwb = Workbook.createWorkbook(new File(fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (wwb != null) {
                // 创建一个可写入的工作表
                // Workbook的createSheet方法有两个参数，第一个是工作表的名称，第二个是工作表在工作薄中的位置
                WritableSheet ws = wwb.createSheet("sheet1", 0);

                // 下面开始添加单元格
                for (int i = 0; i < numrows + 1; i++) {
                    for (int j = 0; j < 6; j++) {
                        // 这里需要注意的是，在Excel中，第一个参数表示列，第二个表示行
                        Label labelC = new Label(j, i, records[i][j]);
                        //		Log.i("Newvalue" + i + " " + j, records[i][j]);
                        try {
                            // 将生成的单元格添加到工作表中
                            ws.addCell(labelC);
                        } catch (RowsExceededException e) {
                            e.printStackTrace();
                        } catch (WriteException e) {
                            e.printStackTrace();
                        }

                    }
                }

                try {
                    // 从内存中写入文件中
                    wwb.write();
                    // 关闭资源，释放内存
                    wwb.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }

        private  void deleteXslFile(String name)  {
            File file = new File(path+"/");
            deleteFile(file,name);
        }

        private  void deleteFile(File file,String name) {
            File Array[] = file.listFiles();
            for (File f : Array) {
                if (f.isFile()) {// 如果是文件
                    if (f.getName().equals(name)) {
                        f.delete();
                        System.out.println("删除文件成功");
                        return;
                    }
                }
            }
            System.out.println("删除文件失败,该文件不存在");
        }
    }
}
