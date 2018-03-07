package com.example.ruanx.cashbook.Model;

/**
 * Created by Administrator on 2017/12/27.
 */

public class Account_item {
    private String month;
    private String sr;
    private String zc;
    private String jy;
    public Account_item(String month, String sr, String zc, String jy){
        this.month=month;
        this.sr=sr;
        this.zc=zc;
        this.jy=jy;
    }
    public String getmonth(){ return month; }
    public String getsr(){return sr;}
    public String getzc(){return zc;}
    public String getjy(){return jy;}
}
