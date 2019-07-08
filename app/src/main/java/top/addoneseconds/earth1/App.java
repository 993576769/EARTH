package top.addoneseconds.earth1;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fanjun.keeplive.KeepLive;
import com.fanjun.keeplive.config.ForegroundNotification;
import com.fanjun.keeplive.config.ForegroundNotificationClickListener;
import com.fanjun.keeplive.config.KeepLiveService;

import java.util.ArrayList;

import top.addoneseconds.earth1.R;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("App start");
        //定义前台服务的默认样式。即标题、描述和图标
        ForegroundNotification foregroundNotification = new ForegroundNotification("壁纸自动同步服务运行中",null, R.drawable.icon,
                //定义前台服务的通知点击事件
                (context, intent) -> {
                    System.out.println("service start");
                });
        //启动保活服务
        KeepLive.startWork(this, KeepLive.RunMode.ENERGY, foregroundNotification,
                //你需要保活的服务，如socket连接、定时任务等，建议不用匿名内部类的方式在这里写
                new KeepLiveService() {
                    /**
                     * 运行中
                     * 由于服务可能会多次自动启动，该方法可能重复调用
                     */
                    @Override
                    public void onWorking() {
                        try{
                            startService(new Intent(getApplicationContext(), SyncService.class));

                        }catch (Exception e){
                            Log.i(null,"Err");
                        }
                    }
                    /**
                     * 服务终止
                     * 由于服务可能会被多次终止，该方法可能重复调用，需同onWorking配套使用，如注册和注销broadcast
                     */
                    @Override
                    public void onStop() {
                        try{
                            stopService(new Intent(getApplicationContext(), SyncService.class));
                        }catch (Exception e){
                            Log.i(null,"Err");
                        }
                    }
                }
        );
    }
}
