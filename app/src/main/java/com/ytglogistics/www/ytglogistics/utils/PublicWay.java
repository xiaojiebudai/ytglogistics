package com.ytglogistics.www.ytglogistics.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.ytglogistics.www.ytglogistics.activity.ImageSelectActivity;

/**
 * Created by Administrator on 2016/12/23.
 */

public class PublicWay {
    /**
     * 选择图片
     *
     * @param requestCode
     * @param doCrop      裁剪
     * @param mode        模式
     * @author xl
     * @date:2016-8-2下午4:06:03
     * @description 支持单图选择模式
     */
    public static void startImageSelectActivity(Activity act, int requestCode,
                                                boolean doCrop, boolean isIdCard, int mode) {
        Intent intent = new Intent(act, ImageSelectActivity.class);
        intent.putExtra(ImageSelectActivity.KEY_MODE, mode);
        intent.putExtra(ImageSelectActivity.KEY_DO_CROP, doCrop);
        intent.putExtra(ImageSelectActivity.KEY_ISIDCARD, isIdCard);
        act.startActivityForResult(intent, requestCode);
    }
    /**
     * 启动应用
     */
    public static void startApp(Context context,String packegeName,String fristPage) {
        // TODO Auto-generated method stub
        // 打开应用
        Intent intent4 = new Intent();
//        intent4.setComponent(new ComponentName("cn.szhysy.person",
//                "cn.szhysy.person.activity.WelcomeActivity"));
        intent4.setComponent(new ComponentName(packegeName,
                fristPage));
        intent4.setAction(Intent.ACTION_VIEW);
        intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent4);
    }
}
