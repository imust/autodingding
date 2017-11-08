package in.imust.autodingding;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    }
}
