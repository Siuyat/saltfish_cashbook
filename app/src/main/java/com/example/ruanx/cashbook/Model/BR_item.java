package com.example.ruanx.cashbook.Model;

import java.io.Serializable;

/**
 * Created by G40 on 2017/12/28.
 */

public class BR_item implements Serializable {
    private String isBorrow;//入  我借别人的 或者 出  我借给别人
    private String hisName;
    private String isReturn;// N Y
    private double money;
    private String comments;
    private int month;
    private int year;
    private int day;
    private int weight;
    private int brItemId;

    public BR_item(String isBorrow, String hisName, String isReturn, double money, String comments, int day, int month, int year){
        this.isBorrow = isBorrow;
        this.comments = comments;
        this.hisName=hisName;
        this.day = day;
        this.isReturn=isReturn;
        this.month = month;
        this.year = year;
        this.money = money;
        this.weight = year*10000+month*100+day;    //时间越新，weight越大，显示时排前面
    }

    public String getIsReturn(){
        return isReturn;
    }

    public String getIsReturnSta(){
        String status="";
        if(isReturn.equals("N") && isBorrow.equals("入")) {
            status= "未还";
        }else if(isReturn.equals("Y") && isBorrow.equals("入")) {
            status= "已还";
        }else if(isReturn.equals("N") && isBorrow.equals("出")) {
            status= "未收";
        }else if(isReturn.equals("Y") && isBorrow.equals("出")) {
            status= "已收";
        }
        return status;
    }

    public void setIsReturn(String isReturn){
        this.isReturn=isReturn;
    }


    public void setBrItemId(int BrItemId){
        this.brItemId=BrItemId;
    }
    public int  getBrItemId(){
       return  brItemId;
    }

    public String getHisName(){
        return hisName;
    }

    public String getComments(){
        return comments;
    }

    public String getIsBorrow(){
        return isBorrow;
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

}
