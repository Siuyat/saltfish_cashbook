package com.example.ruanx.cashbook.Model;

/**
 * Created by Administrator on 2017/12/29.
 */

public class BT_Item {
    private double money;
    private double percentage;
    private String label;

    public BT_Item(double money, double per, String label){
        this.money=money;
        this.label=label;
        this.percentage =per;
    }
    public double getmoney(){ return money; }
    public double getper(){ return percentage;}
    public String getlabel(){return label;}
}
