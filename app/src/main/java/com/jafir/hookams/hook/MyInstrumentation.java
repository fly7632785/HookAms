package com.jafir.hookams.hook;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * Created by huangjian on 2016/7/28.
 */
public class MyInstrumentation extends Instrumentation {

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        // 在Instrumentation捕获桩，然后进行替换绕过
        if(className.equals("com.jafir.hookams.PreBuryActivity") && intent != null){
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                //取出要替换的类名
                String realActivity = bundle.getString("realActivity");
                if(!TextUtils.isEmpty(realActivity)){
                    return super.newActivity(cl, realActivity, intent);
                }
            }
        }
        return super.newActivity(cl, className, intent);
    }
}
