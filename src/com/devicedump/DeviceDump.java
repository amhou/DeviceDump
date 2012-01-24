package com.devicedump;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.view.View;
import android.widget.TextView;
import android.telephony.TelephonyManager;
import android.content.Context;
import android.provider.Settings;

public class DeviceDump extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        dumpState();
    }
    
    public void refresh(View button) {
        dumpState();
    }

    public void dumpState() {
        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        String mdn = tManager.getLine1Number();
        TextView mdn_tv = (TextView) this.findViewById(R.id.mdn_textview);
        mdn_tv.setText("MDN: " + mdn);

        String imsi = tManager.getSubscriberId();
        TextView imsi_tv = (TextView) this.findViewById(R.id.imsi_textview);
        imsi_tv.setText("IMSI: " + imsi);

        String manufacturer = Build.MANUFACTURER;
        TextView manufacturer_tv = (TextView) this.findViewById(R.id.manufacturer_textview);
        manufacturer_tv.setText("Manufacturer: " + manufacturer);

        String device_name = Build.MODEL;
        String device_code = Build.DEVICE;
        TextView device_tv = (TextView) this.findViewById(R.id.device_textview);
        device_tv.setText("Device: " + device_name + " / " + device_code);

        String build_id = Build.ID;
        String build_version_inc = Build.VERSION.INCREMENTAL;
        TextView build_id_tv = (TextView) this.findViewById(R.id.build_id_textview);
        build_id_tv.setText("Build ID: " + build_id + " / " + build_version_inc);

        String imei = tManager.getDeviceId();
        TextView imei_tv = (TextView) this.findViewById(R.id.imei_textview);
        imei_tv.setText("IMEI/MEID: " + imei);
        
        
        String imei_sv = tManager.getDeviceSoftwareVersion();
        TextView imei_sv_tv = (TextView) this.findViewById(R.id.imei_sv_textview);
        imei_sv_tv.setText("IMEI SV: " + imei_sv);
        
        String iccid = tManager.getSimSerialNumber();
        TextView iccid_tv = (TextView) this.findViewById(R.id.iccid_textview);
        iccid_tv.setText("ICCID: " + iccid);

        String sdk_version = Build.VERSION.RELEASE;
        TextView sdk_tv = (TextView) this.findViewById(R.id.sdk_textview);
        sdk_tv.setText("Android Version: " + sdk_version);

        String network_name = tManager.getNetworkOperatorName();
        String network_id = tManager.getNetworkOperator();
        TextView network_name_tv = (TextView) this.findViewById(R.id.network_name_textview);
        network_name_tv.setText("Network: " + network_name + " (" + network_id + ")");

        String apn_name = null;
        Cursor c = getContentResolver().query(Uri.parse("content://telephony/carriers/preferapn"), new String[]{"name","apn"},"current=1",null,null);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    apn_name = c.getString(0) + " / " + c.getString(1);
                }
            } finally {
                c.close();
            }
        }
        TextView apn_tv = (TextView) this.findViewById(R.id.apn_textview);
        apn_tv.setText("APN: "  + apn_name);

//        String radio_version = Build.getRadioVersion();
//        TextView radio_version_tv = (TextView) this.findViewById(R.id.radio_version_textview);
//        radio_version_tv.setText("Baseband: " + radio_version);

        boolean is_roaming = tManager.isNetworkRoaming();
        TextView is_roaming_tv = (TextView) this.findViewById(R.id.is_roaming_textview);
        is_roaming_tv.setText("Roaming: " + is_roaming);
        
        String kernel_version = System.getProperty("os.version");
        TextView kernel_version_tv = (TextView) this.findViewById(R.id.kernel_version_textview);
        kernel_version_tv.setText("Kernel: " + kernel_version);
        
        String airplane = Settings.System.AIRPLANE_MODE_ON;
        TextView airplane_tv = (TextView) this.findViewById(R.id.airplane_textview);
        airplane_tv.setText("Airplane Mode: " + airplane);
    }
}
