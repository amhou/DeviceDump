package com.devicedump;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.telephony.TelephonyManager;
import android.provider.Settings;
import android.content.Context;

public class DeviceDump extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void dumpState(View button) {
        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tManager.getDeviceId();
        TextView imei_tv = (TextView) this.findViewById(R.id.imei_textview);
        imei_tv.setText("IMEI: " + imei);

        String mdn = tManager.getLine1Number();
        TextView mdn_tv = (TextView) this.findViewById(R.id.mdn_textview);
        mdn_tv.setText("MDN: " + mdn);

        String imsi = tManager.getSubscriberId();
        TextView imsi_tv = (TextView) this.findViewById(R.id.imsi_textview);
        imsi_tv.setText("IMSI: " + imsi);

        String apn = Settings.ACTION_APN_SETTINGS;
        TextView apn_tv = (TextView) this.findViewById(R.id.apn_textview);
        apn_tv.setText("APN: " + apn);
    }
}
