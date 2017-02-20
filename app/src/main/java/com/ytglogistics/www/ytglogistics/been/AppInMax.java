package com.ytglogistics.www.ytglogistics.been;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2016/12/9.
 */

public class AppInMax {
    public long RowId;
    public long InId;
    public String So;
    public String Loca;
    public int Soquan;
    public double Format;
    public int Ttlpcs;
    public double Leng;
    public double Wide;
    public double High;
    public double Rweight;
    public double Cbm;
    public double Unitwei;
    public String Po;
    public String Skn;
    public double BookingCbm;
    public double CbmRate;
    public int OutCtn;
    public String OutKeyid;
    public int PaperCtn;
    public int OutItem;
    public String QtyStatus;
    public boolean isSelect;//是否选中
    public JSONObject toJson() throws JSONException {
        JSONObject localItemObject = new JSONObject();
        localItemObject.put("RowId", RowId);
        localItemObject.put("InId", InId);
        localItemObject.put("So", So);
        localItemObject.put("Loca", Loca);
        localItemObject.put("Soquan", Soquan);
        localItemObject.put("Format", Format);
        localItemObject.put("Ttlpcs", Ttlpcs);
        localItemObject.put("Leng", Leng);
        localItemObject.put("Wide", Wide);
        localItemObject.put("High", High);
        localItemObject.put("Rweight", Rweight);
        localItemObject.put("Cbm", Cbm);
        localItemObject.put("Unitwei", Unitwei);
        localItemObject.put("Po", Po);
        localItemObject.put("Skn", Skn);
        localItemObject.put("BookingCbm", BookingCbm);
        localItemObject.put("CbmRate", CbmRate);
        localItemObject.put("OutKeyid", OutKeyid);
        localItemObject.put("PaperCtn", PaperCtn);
        localItemObject.put("OutItem", OutItem);
        localItemObject.put("QtyStatus", QtyStatus);
        localItemObject.put("OutCtn", OutCtn);
        return localItemObject;
    }



}
