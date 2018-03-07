package com.example.ruanx.cashbook.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SIRIUS on 2017/12/22.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    /**
     * 数据库名
     */
    public static final String DB_NAME = "myCashBook.db";

    /**
     * 数据库版本
     */
    public static final int VERSION = 1;

    /**
     * 建表语句
     */
    private static final String CREATE_CASH = "create table cash("
            +"id integer primary key autoincrement,"
            +"type text,"
            +"weight int,"
            +"label text,"
            +"subtype text,"
            +"comments text,"
            +"money real,"
            +"year int,"
            +"month int,"
            +"day int)";

    private static final String CREATE_BR = "create table br("
            +"id integer primary key autoincrement,"
            +"isborrow text,"
            +"isreturn text,"
            +"hisname text,"
            +"comments text,"
            +"year int,"
            +"month int,"
            +"day int,"
            +"money real,"
            +"weight int)";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        mContext = context;
    }

    public MyDatabaseHelper(Context context){
        super(context,DB_NAME,null,VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_CASH);    /*数据库创建成功的同时创建表*/
        db.execSQL(CREATE_BR);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion ){

    }

}
