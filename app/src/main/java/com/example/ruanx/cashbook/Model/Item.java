package com.example.ruanx.cashbook.Model;

import java.io.Serializable;

/**
 * Created by SIRIUS on 2017/12/22.
 */

public class Item implements Serializable {
    private String label;
    private String comments;
    private String subtype;
    private int day;
    private String type;
    private int month;
    private int year;
    private double money;
    private int weight;
    private  int itemId;
    public Item( String type, String label, String subtype,String comments, int day, int month, int year, double money){
        this.label = label;
        this.subtype=subtype;
        this.comments = comments;
        this.day = day;
        this.type = type;
        this.month = month;
        this.year = year;
        this.money = money;
        this.weight = year*10000+month*100+day;    //时间越新，weight越大，显示时排前面
    }

    public String getLabel(){
        return label;
    }

    public String getComments(){
        return comments;
    }

    public String getType(){
        return type;
    }

    public int getDay(){
        return day;
    }

    public int getMonth(){
        return month;
    }

    public int getYear(){
        return year;
    }

    public int getWeight(){
        return weight;
    }

    public double getMoney(){
        return money;
    }

    public void  setItemId(int itemId){
        this.itemId=itemId;
    }

    public int getItemId(){
        return itemId;
    }

    public String getSubtype(){
        return subtype;
    }

    public void setSubtype(String subtype){
        this.subtype=subtype;
    }
}
