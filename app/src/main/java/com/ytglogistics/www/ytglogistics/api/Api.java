package com.ytglogistics.www.ytglogistics.api;

public class Api {
    private static final boolean isDebug = false;

    /**
     * 主机地址
     */
    public static final String ONLINE = isDebug ? "http://120.24.184.18:808/Wcf/bllService.svc/"
            : "http://www.yplog.com.cn/Wcf/bllService.svc/";

    /**
     * 登陆
     *
     * @return
     */
    public static final String UserLogin() {
        return ONLINE + "UserLogin";
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public static final String GetMyInfo() {
        return ONLINE + "GetMyInfo";
    }

    public static final String GetAppMxCbmById() {
        return ONLINE + "GetAppMxCbmById";
    }

    /**
     * 收货
     *
     * @return
     */
    public static final String FinishQueue() {
        return ONLINE + "FinishQueue";
    }

    /**
     * 入仓数据列表
     *
     * @return
     */
    public static final String GetQueues() {
        return ONLINE + "GetQueues";
    }

    /**
     * 装卸队列表
     *
     * @return
     */
    public static final String GetSteved() {
        return ONLINE + "GetSteved";
    }

    public static final String GetUsers() {
        return ONLINE + "GetUsers";
    }

    /**
     * in保存  post  AppInResult
     *
     * @return
     */
    public static final String AppInCommit() {
        return ONLINE + "AppInCommit";
    }

    public static final String GetAppIn() {
        return ONLINE + "GetAppIn";
    }

    public static final String GetAppInMx() {
        return ONLINE + "GetAppInMx";
    }

    public static final String GetAppMxNumsById() {
        return ONLINE + "GetAppMxNumsById";
    }

    /**
     * in收获  get
     * serial : serial,
     * queueNo : yyNo
     *
     * @return
     */
    public static final String AppInMxCommit() {
        return ONLINE + "AppInMxCommit";
    }

    /**
     * 获取打印数据
     *
     * @return
     */
    public static final String PdaPallet() {
        return ONLINE + "PdaPallet";
    }

    public static final String AppMxCbmCommit() {
        return ONLINE + "AppMxCbmCommit";
    }

    public static final String GetOutQueue() {
        return ONLINE + "GetOutQueue";
    }

    public static final String GetAppOut() {
        return ONLINE + "GetAppOut";
    }

    public static final String GetAppOutMx() {
        return ONLINE + "GetAppOutMx";
    }

    public static final String AllPlaces() {
        return ONLINE + "AllPlaces";
    }

    public static final String GetAllQueues() {
        return ONLINE + "GetAllQueues";
    }

    public static final String GetRecSoIts() {
        return ONLINE + "GetRecSoIts";
    }

    public static final String ChangeSoLoca() {
        return ONLINE + "ChangeSoLoca";
    }

    public static final String PoQuery() {
        return ONLINE + "PoQuery";
    }

    public static final String PlaceStatus() {
        return ONLINE + "PlaceStatus";
    }

    public static final String PlaceLastInfo() {
        return ONLINE + "PlaceLastInfo";
    }

    public static final String PlaceOper() {
        return ONLINE + "PlaceOper";
    }

    public static final String QueryOrder() {
        return ONLINE + "QueryOrder";
    }

    public static final String FinishOutQueue() {
        return ONLINE + "FinishOutQueue";
    }

    public static final String AppOutMxCommit() {
        return ONLINE + "AppOutMxCommit";
    }

    public static final String AppMxNumCommit() {
        return ONLINE + "AppMxNumCommit";
    }

    public static final String PalletGet() {
        return ONLINE + "PalletGet";
    }

    public static final String GetMyGrant() {
        return ONLINE + "GetMyGrant";
    }

    public static final String ChagnePlace() {
        return ONLINE + "ChagnePlace";
    }

    public static final String GetZxDzl() {
        return ONLINE + "GetZxDzl";
    }

    public static final String PdaPalletBySo() {
        return ONLINE + "PdaPalletBySo";
    }
    public static final String TaskOutList() {
        return ONLINE + "TaskOutList";
    }   public static final String TaskOutConfirm() {
        return ONLINE + "TaskOutConfirm";
    }

}
