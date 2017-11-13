package in.imust.autodingding;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NetworkConnectChangedReceiver extends BroadcastReceiver{

    public static final String PRE_NAME = "auto_dingding";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                NetworkInfo.State state = networkInfo.getState();
                boolean isConnected = state == NetworkInfo.State.CONNECTED;
                if (isConnected) {
                    onWifiConnected1(networkInfo.getExtraInfo().replace("\"",""), context);
                }
            }
        }
    }

    private void onWifiConnected1(String name, Context context) {
        if ("MHOME-dev".equalsIgnoreCase(name) || "MHOME-dev_5G".equalsIgnoreCase(name) || "MHOME-JJ".equalsIgnoreCase(name)) {
            SharedPreferences pref = context.getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE);
            String key = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
            if (!pref.getBoolean(key, false)) {
                pref.edit().putBoolean(key, true).apply();
                startDingding(context);
            }
        }
    }
    private void startDingding(Context context) {
        Uri uri = Uri.parse("icebox://action/run_app/com.alibaba.android.rimet");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        // 立刻显示通知
        showNotification(context, System.currentTimeMillis());


        // 预备下班时显示通知
        Date date = new Date();
        date.setHours(18);
        date.setMinutes(1);
        showNotification(context, date.getTime());
    }

    public void showNotification(Context context, long time) {
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentInfo("GFW MUST DIE");
        builder.setContentText("GFW MUST DIE");
        builder.setContentTitle("打卡提醒");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker("该打卡啦");
        builder.setAutoCancel(true);

        builder.setWhen(time);//设置时间，设置为系统当前的时间

        Uri uri = Uri.parse("icebox://action/run_app/com.alibaba.android.rimet");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


//        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        /**
         * vibrate属性是一个长整型的数组，用于设置手机静止和振动的时长，以毫秒为单位。
         * 参数中下标为0的值表示手机静止的时长，下标为1的值表示手机振动的时长， 下标为2的值又表示手机静止的时长，以此类推。
         */
//        long[] vibrates = { 0, 1000, 1000, 1000 };
//        notification.vibrate = vibrates;

        /**
         * 手机处于锁屏状态时， LED灯就会不停地闪烁， 提醒用户去查看手机,下面是绿色的灯光一 闪一闪的效果
         */
        notification.ledARGB = Color.GREEN;// 控制 LED 灯的颜色，一般有红绿蓝三种颜色可选
        notification.ledOnMS = 500;// 指定 LED 灯亮起的时长，以毫秒为单位
        notification.ledOffMS = 500;// 指定 LED 灯暗去的时长，也是以毫秒为单位
        notification.flags = Notification.FLAG_SHOW_LIGHTS;// 指定通知的一些行为，其中就包括显示
        // LED 灯这一选项

//        Uri uri1 = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.mario);
//        notification.sound = uri1;

//        notification.defaults = Notification.DEFAULT_ALL;


        manager.notify(1, notification);
    }

}
