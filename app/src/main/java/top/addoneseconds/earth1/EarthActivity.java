package top.addoneseconds.earth1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class EarthActivity extends AppCompatActivity {

    ImageView image;
    Button button;
    Button setWallpaperButton;
    Button setsetlockscreenWallpaperButton;
    static Bitmap bitmap = null;
    private int width1;
    private int height1;
    int size = 80;
    private int X = 0;
    private int Y = 0;
    private Vibrator vibrator;
    private SharedPreferences sharedPref;
    private IndicatorSeekBar indicatorSeekBarTIME;
    private IndicatorSeekBar indicatorSeekBarX;
    private IndicatorSeekBar indicatorSeekBarY;
    private IndicatorSeekBar indicatorSeekBarSIZE;
    Context context;

    @SuppressLint({"SdCardPath", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

        System.out.println("EarthActivity start");
        setContentView(R.layout.activity_earth);
        final int requestCode = 2;
        if (permissionUtil.isOwnPermisson(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.i("request", "own");
        } else {
            permissionUtil.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode);

        }
        if (permissionUtil.isOwnPermisson(this, Manifest.permission.READ_PHONE_STATE)) {
            Log.i("request", "own");
        } else {
            permissionUtil.requestPermission(this, Manifest.permission.READ_PHONE_STATE, requestCode);
        }
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        sharedPref = getSharedPreferences("canshu", MODE_PRIVATE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        X = sharedPref.getInt("X", 0);
        Y = sharedPref.getInt("Y", 0);
        size = sharedPref.getInt("size", 80);

        indicatorSeekBarTIME = findViewById(R.id.indicatorSeekBarTIME);
        indicatorSeekBarX = findViewById(R.id.indicatorSeekBarX);
        indicatorSeekBarY = findViewById(R.id.indicatorSeekBarY);
        indicatorSeekBarSIZE = findViewById(R.id.indicatorSeekBarSIZE);

        indicatorSeekBarSIZE.setProgress(sharedPref.getInt("size", 80));
        indicatorSeekBarX.setProgress(sharedPref.getInt("X", 0));
        indicatorSeekBarY.setProgress(sharedPref.getInt("Y", 0));

        ActionBar actionBar = getSupportActionBar();
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        width1 = wm.getDefaultDisplay().getWidth();
        height1 = wm.getDefaultDisplay().getHeight();
        if (actionBar != null) {
            actionBar.hide();
        }
        image = findViewById(R.id.imageView);
        button = findViewById(R.id.button);
        setWallpaperButton = findViewById(R.id.setWallpaper);
        setsetlockscreenWallpaperButton = findViewById(R.id.setlockscreenWallpaper);
        FutureTarget<Bitmap> futureTarget =
                Glide.with(context)
                        .asBitmap()
                        .load("https://addoneseconds.top/earth1_hd.webp")
                        .submit(2200, 2200);
        //同步
        button.setOnClickListener(view ->
        {
            Bitmap bm = null;
            vibrator.vibrate(100);
            Log.d("[+]", "Button clicked!");
            try {
                bm = futureTarget.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (isNetWorkConnected()) {
                Toast.makeText(EarthActivity.this, "开始同步", Toast.LENGTH_SHORT).show();
                image.setImageBitmap(bm);
            } else {
                Toast.makeText(EarthActivity.this, "无网络", Toast.LENGTH_SHORT).show();
            }
        });
        //长按同步
        button.setOnLongClickListener(v ->
        {
            vibrator.vibrate(200);
            ignoreBatteryOptimization(this);
            return true;
        });
        //设为锁屏壁纸
        setsetlockscreenWallpaperButton.setOnClickListener(v ->
        {
            vibrator.vibrate(200);
            if (isNetWorkConnected()) {
                Toast.makeText(EarthActivity.this, "锁屏壁纸设置成功", Toast.LENGTH_SHORT).show();
                startService(new Intent(getApplicationContext(), SyncService.class));
            } else {
                Toast.makeText(EarthActivity.this, "无网络", Toast.LENGTH_SHORT).show();
            }
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("X", indicatorSeekBarX.getProgress());
            editor.putInt("Y", indicatorSeekBarY.getProgress());
            editor.putInt("size", indicatorSeekBarSIZE.getProgress());
            editor.putInt("width", width1);
            editor.putString("type", "lockscreen");
            editor.apply();

        });
        //设为壁纸
        setWallpaperButton.setOnClickListener(view ->
        {
            vibrator.vibrate(100);
            if (isNetWorkConnected()) {
                Toast.makeText(EarthActivity.this, "全局壁纸设置成功", Toast.LENGTH_SHORT).show();
                startService(new Intent(getApplicationContext(), SyncService.class));
            } else {
                Toast.makeText(EarthActivity.this, "无网络", Toast.LENGTH_SHORT).show();
            }
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("X", indicatorSeekBarX.getProgress());
            editor.putInt("Y", indicatorSeekBarY.getProgress());
            editor.putInt("size", indicatorSeekBarSIZE.getProgress());
            editor.putInt("width", width1);
            editor.putString("type", "system");
            editor.apply();
        });

        indicatorSeekBarSIZE.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                //vibrator.vibrate(8);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                if (seekBar.getProgress() == 0) {
                    size = 1;
                } else if (seekBar.getProgress() > 0) {
                    size = seekBar.getProgress();
                }
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("size", indicatorSeekBarSIZE.getProgress());
                editor.apply();
                //Toast.makeText(EarthActivity.this, "大小:" + String.valueOf(size), Toast.LENGTH_SHORT).show();
                Log.v("SIZE停止滑动时的值：", String.valueOf(indicatorSeekBarSIZE.getProgress()));
            }
        });
        indicatorSeekBarX.setOnSeekChangeListener(new OnSeekChangeListener() {

            @Override
            public void onSeeking(SeekParams seekParams) {
                //vibrator.vibrate(8);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("X", indicatorSeekBarX.getProgress());
                editor.apply();
                //Toast.makeText(EarthActivity.this, "X轴:" + String.valueOf(X), Toast.LENGTH_SHORT).show();
                Log.v("X停止滑动时的值：", String.valueOf(indicatorSeekBarX.getProgress()));
            }
        });
        indicatorSeekBarY.setOnSeekChangeListener(new OnSeekChangeListener() {

            @Override
            public void onSeeking(SeekParams seekParams) {
                //vibrator.vibrate(8);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("Y", indicatorSeekBarY.getProgress());
                editor.apply();
                //Toast.makeText(EarthActivity.this, "Y轴:" + String.valueOf(Y), Toast.LENGTH_SHORT).show();
                Log.v("Y停止滑动时的值：", String.valueOf(indicatorSeekBarY.getProgress()));
            }
        });
        indicatorSeekBarTIME.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                int time = 0;
                switch (indicatorSeekBarTIME.getProgress()) {
                    case 0:
                        time = 10 * 60 * 1000;
                        break;
                    case 25:
                        time = 20 * 60 * 1000;
                        break;
                    case 50:
                        time = 30 * 60 * 1000;
                        break;
                    case 75:
                        time = 60 * 60 * 1000;
                        break;
                    case 100:
                        time = 120 * 60 * 1000;
                        break;
                }
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("TIME", time);
                editor.apply();
                Toast.makeText(EarthActivity.this, "同步时间间隔：" + String.valueOf(time / (60 * 1000)) + "分钟", Toast.LENGTH_SHORT).show();
                Log.v("TIME停止滑动时的值：", String.valueOf(time / (60 * 1000)) + "分钟");
            }
        });
        setSupportActionBar(toolbar);
    }


    private void showSimpleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon);
        builder.setTitle(R.string.simple_dialog);
        builder.setMessage(R.string.dialog_message);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void ignoreBatteryOptimization(Activity activity) {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean hasIgnored = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasIgnored = powerManager.isIgnoringBatteryOptimizations(activity.getPackageName());
        }
        if (!hasIgnored) {
            @SuppressLint("BatteryLife") Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            startActivity(intent);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void download(final String url) {

        new AsyncTask<Void, Integer, File>() {

            @Override
            protected File doInBackground(Void... params) {
                File file = null;
                try {
                    FutureTarget<File> future = Glide
                            .with(context)
                            .load(url)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

                    file = future.get();

                    // 首先保存图片
                    File pictureFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile();

                    File appDir = new File(pictureFolder, "earth");
                    if (!appDir.exists()) {
                        appDir.mkdirs();
                    }
                    String fileName = System.currentTimeMillis() + ".webp";
                    File destFile = new File(appDir, fileName);

                    copy(file, destFile);

                    // 最后通知图库更新
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.fromFile(new File(destFile.getPath()))));


                } catch (Exception e) {
                    Log.e(null, e.getMessage());
                }
                return file;
            }

            @Override
            protected void onPostExecute(File file) {

                Toast.makeText(context, "图片已保存", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }
        }.execute();
    }

    public void copy(File source, File target) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                download("https://addoneseconds.top/earth1_hd.webp");
                break;
            case R.id.info:
                showSimpleDialog();
                break;
            default:
        }
        return true;
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
}