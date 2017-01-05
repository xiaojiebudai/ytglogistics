package com.ytglogistics.www.ytglogistics.been;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2016/12/21.
 */

public class DataCbm {
    public long Serial;//序号
    public long InMxId;//进仓单号
    public String So;//SO
    public String Po;//PO
    public String Skn;//SKN
    public double Ctnno;//箱号
    public double Leng;//长
    public double Wide;//宽
    public double High;//高
    public double Unitwei;//单重
    public int Soquan;//箱数
    public String Palletid;
    public boolean isSelect;//是否选中
    public JSONObject toJson() throws JSONException {
        JSONObject localItemObject = new JSONObject();
        localItemObject.put("Serial", Serial);
        localItemObject.put("InMxId", InMxId);
        localItemObject.put("So", So);
        localItemObject.put("Po", Po);
        localItemObject.put("Skn", Skn);
        localItemObject.put("Ctnno", Ctnno);
        localItemObject.put("Leng", Leng);
        localItemObject.put("Wide", Wide);
        localItemObject.put("High", High);
        localItemObject.put("Unitwei", Unitwei);
        localItemObject.put("Soquan", Soquan);
        localItemObject.put("Palletid", Palletid);
        return localItemObject;
    }

}
