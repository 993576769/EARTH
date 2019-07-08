package top.addoneseconds.earth1;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static top.addoneseconds.earth1.EarthActivity.bitmap;

public class SyncService extends Service {
    private int a;

    @Override
    public void onCreate() {
        SharedPreferences sharedPref = getSharedPreferences("canshu", Context.MODE_PRIVATE);
        if (isNetWorkConnected()) {
            a = sharedPref.getInt("TIME", 10 * 60 * 1000);
            handler.removeCallbacks(runnable);
            handler1.removeCallbacks(runnable1);
            if (Objects.equals(sharedPref.getString("type", "null"), "system")) {
                handler.removeCallbacks(runnable);
                handler1.removeCallbacks(runnable1);
                handler.post(runnable);
            } else if (Objects.equals(sharedPref.getString("type", "null"), "lockscreen")) {
                handler.removeCallbacks(runnable);
                handler1.removeCallbacks(runnable1);
                handler1.post(runnable1);
            }
        } else {
            System.out.println("无网络");
        }
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sharedPref = getSharedPreferences("canshu", Context.MODE_PRIVATE);
        if (isNetWorkConnected()) {
            a = sharedPref.getInt("TIME", 10 * 60 * 1000);
            handler.removeCallbacks(runnable);
            handler1.removeCallbacks(runnable1);
            if (Objects.equals(sharedPref.getString("type", "null"), "system")) {
                handler.removeCallbacks(runnable);
                handler1.removeCallbacks(runnable1);
                handler.post(runnable);
            } else if (Objects.equals(sharedPref.getString("type", "null"), "lockscreen")) {
                handler.removeCallbacks(runnable);
                handler1.removeCallbacks(runnable1);
                handler1.post(runnable1);
            }
        } else {
            System.out.println("无网络");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isNetWorkConnected()) {
                System.out.println("桌面壁纸同步");
                try{
                    setwp();

                }catch (Exception e){
                    Log.i(null,"err");
                }
            } else {
                System.out.println("桌面壁纸无法同步");
            }
            handler.postDelayed(this, a);
        }
    };
    Handler handler1 = new Handler();
    Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            if (isNetWorkConnected()) {
                System.out.println("锁屏壁纸同步");
                setlockscreenwp();
                try{
                    setlockscreenwp();
                }catch (Exception e){
                    Log.i(null,"err");
                }
            } else {
                System.out.println("锁屏壁纸无法同步");
            }
            handler1.postDelayed(this, a);
        }
    };

    public void setwp() {
        SharedPreferences sharedPref = getSharedPreferences("canshu", Context.MODE_PRIVATE);
        int X = sharedPref.getInt("X", 0);
        int Y = sharedPref.getInt("Y", 0);
        int size = sharedPref.getInt("size", 80);
        int width1 = sharedPref.getInt("width", 1080);
        int width = 2200;
        int height = 2200;
        int height1 = width1;
        int newWidth = width1 * size / 150;
        int newHeight = newWidth;
        Log.d("newWidth", String.valueOf(newWidth));
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newWidth) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(getbitmap(this), 0, 0, 2200, 2200, matrix, true);
        bitmap = Bitmap.createBitmap(width1, height1, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Rect baseRect = new Rect(bitmap.getWidth() / 2 - (newbm.getWidth() / 2) + X, bitmap.getHeight() / 2 - (newbm.getWidth() / 2) + Y, bitmap.getWidth() / 2 + (newbm.getWidth() / 2) + X, bitmap.getHeight() / 2 + (newbm.getWidth() / 2) + Y);
        canvas.drawColor(Color.RED, PorterDuff.Mode.CLEAR);
        canvas.drawBitmap(newbm, null, baseRect, null);
        WallpaperManager w_manager = WallpaperManager.getInstance(this);
        try {
            w_manager.setBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        deleteFolder(getCacheDir());
    }

    public void setlockscreenwp() {
        SharedPreferences sharedPref = getSharedPreferences("canshu", Context.MODE_PRIVATE);
        int X = sharedPref.getInt("X", 0);
        int Y = sharedPref.getInt("Y", 0);
        int size = sharedPref.getInt("size", 80);
        int width1 = sharedPref.getInt("width", 1080);
        int width = 2200;
        int height = 2200;
        int height1 = width1;
        int newWidth = width1 * size / 150;
        int newHeight = newWidth;
        Log.d("newWidth", String.valueOf(newWidth));
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newWidth) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(getbitmap(this), 0, 0, 2200, 2200, matrix, true);
        bitmap = Bitmap.createBitmap(width1, height1, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Rect baseRect = new Rect(bitmap.getWidth() / 2 - (newbm.getWidth() / 2) + X, bitmap.getHeight() / 2 - (newbm.getWidth() / 2) + Y, bitmap.getWidth() / 2 + (newbm.getWidth() / 2) + X, bitmap.getHeight() / 2 + (newbm.getWidth() / 2) + Y);
        canvas.drawBitmap(newbm, null, baseRect, null);
        WallpaperManager w_manager = WallpaperManager.getInstance(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                w_manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
            } catch (IOException ignored) {
            }
        }
        deleteFolder(getCacheDir());
    }

    public boolean isNetWorkConnected() {
        boolean isConnected = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = cm.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isAvailable() && activeInfo.isConnected()) {
            isConnected = true;
        }
        return isConnected;
    }

    public void deleteFolder(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                deleteFolder(file1);
            }
        }
        file.delete();
    }

    public Bitmap getbitmap(Context context) {
        Bitmap bitmap;
        try {
            FutureTarget<Bitmap> futureTarget =
                    Glide.with(context)
                            .asBitmap()
                            .load("https://addoneseconds.top/earth1_hd.webp")
                            .submit(2200, 2200);
            bitmap = futureTarget.get();
            Glide.with(context).clear(futureTarget);
            return bitmap;
        } catch (Exception e) {
            Log.e(null, e.toString());
        }
        return null;
    }


}

