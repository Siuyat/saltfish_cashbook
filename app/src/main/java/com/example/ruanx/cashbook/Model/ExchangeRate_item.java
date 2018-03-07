package com.example.ruanx.cashbook.Model;

/**
 * Created by G40 on 2018/1/2.
 */

public class ExchangeRate_item {
    private String currencyName;//货币名称
    private String unit;//交易单位
    private String xianhuimairujia;//现汇买入价
    private String xianchaomairujia;//现钞买入价
    private String xianchaomaichujia;//现钞卖出价
    private String zhonghangzhesuanjia;//中行结算价


    public ExchangeRate_item(String currencyName, String xianhuimairujia){
        this.currencyName=currencyName;
        this.xianhuimairujia=xianhuimairujia;
    }

    public String getCurrencyName(){
        return currencyName;
    }

    public String getXianhuimairujia(){
        return xianhuimairujia;
    }




}
