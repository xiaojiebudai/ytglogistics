package com.ytglogistics.www.ytglogistics.been;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by 木头 on 2016/12/8.
 */

public class AppInResult {
    public Long BeginTime;
    public Long EndTime;
    public Long JdTime;
    public Long PdTime;
    public String CarNo;
    public String ZxdzlNo;
    public String So;
    public int OperType;
    public String OrderId;
    public String PlaceId;
    public String QueueNo;
    public int Serial;
    public String StevedId;
    public String UserId;
    public String StevedName;
    public String UserCode;
    public String Clp;
    public JSONObject toJson() throws JSONException {
        JSONObject localItemObject = new JSONObject();
        localItemObject.put("BeginTime", BeginTime);
        localItemObject.put("EndTime", EndTime);
        localItemObject.put("JdTime", JdTime);
        localItemObject.put("PdTime", PdTime);
        localItemObject.put("CarNo", CarNo);
        localItemObject.put("So", So);
        localItemObject.put("OperType", OperType);
        localItemObject.put("OrderId", OrderId);
        localItemObject.put("PlaceId", PlaceId);
        localItemObject.put("QueueNo", QueueNo);
        localItemObject.put("ZxdzlNo", ZxdzlNo);
        localItemObject.put("Serial", Serial);
        localItemObject.put("StevedId", StevedId);
        localItemObject.put("UserId", UserId);
        localItemObject.put("StevedName", StevedName);
        localItemObject.put("UserCode", UserCode);
        localItemObject.put("Clp", Clp);
        return localItemObject;
    }
}
