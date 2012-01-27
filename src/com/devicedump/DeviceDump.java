package com.devicedump;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.text.format.DateUtils;
import android.util.LogPrinter;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

import com.google.gson.stream.JsonWriter;

public class DeviceDump extends Activity {
    private TelephonyManager tManager;
    private myPhoneStateListener mpListener;
    private WifiManager wManager;
    private ConnectivityManager cManager;
    
    private TextView mdn_tv;
    private TextView imsi_tv;
    private TextView manufacturer_tv;
    private TextView device_tv;
    private TextView build_id_tv;
    private TextView imei_tv;
    private TextView imei_sv_tv;
    private TextView iccid_tv;
    private TextView sdk_tv;
    private TextView network_name_tv;
    private TextView apn_tv;
//    private TextView radio_version_tv;
    private TextView is_roaming_tv;
    private TextView kernel_version_tv;
    private TextView ssid_tv;
    private TextView mac_add_tv;
    private TextView ip_add_tv;
    private TextView signal_strength_tv;
    private TextView mobile_network_tv;
    private TextView mobile_state_tv;
    private TextView cell_strength_tv;
    private TextView service_state_tv;
    private TextView uptime_tv;    

    private HashMap<String,String> deviceInfo = new HashMap<String,String>();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mpListener = new myPhoneStateListener();
        tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        wManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        
        tManager.listen(mpListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        tManager.listen(mpListener, PhoneStateListener.LISTEN_SERVICE_STATE);
        
        dumpState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        tManager.listen(mpListener, PhoneStateListener.LISTEN_NONE); //turn off listener
        mHandler.removeMessages(EVENT_TICK);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        tManager.listen(mpListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS); //start listener again
        tManager.listen(mpListener, PhoneStateListener.LISTEN_SERVICE_STATE);
        mHandler.sendEmptyMessageDelayed(EVENT_TICK, 1000);
    }
    
    public void refresh(View button) {
        dumpState();
    }

    public void dumpState() {
        WifiInfo wInfo = wManager.getConnectionInfo();

        String mdn = tManager.getLine1Number();
        mdn_tv = (TextView) this.findViewById(R.id.mdn_textview);
        mdn_tv.setText("MDN: " + mdn);
        deviceInfo.put("MDN", mdn);

        String imsi = tManager.getSubscriberId();
        imsi_tv = (TextView) this.findViewById(R.id.imsi_textview);
        imsi_tv.setText("IMSI: " + imsi);
        deviceInfo.put("IMSI", imsi);

        String manufacturer = Build.MANUFACTURER;
        manufacturer_tv = (TextView) this.findViewById(R.id.manufacturer_textview);
        manufacturer_tv.setText("Manufacturer: " + manufacturer);
        deviceInfo.put("Manufacturer",manufacturer);

        String device_name = Build.MODEL;
        String device_code = Build.DEVICE;
        String device_output = device_name + " / " + device_code;
        device_tv = (TextView) this.findViewById(R.id.device_textview);
        device_tv.setText("Device: " + device_output);
        deviceInfo.put("Device", device_output);

        String build_id = Build.ID;
        String build_version_inc = Build.VERSION.INCREMENTAL;
        String build_output = build_id + " / " + build_version_inc;
        build_id_tv = (TextView) this.findViewById(R.id.build_id_textview);
        build_id_tv.setText("Build ID: " + build_output);
        deviceInfo.put("Build ID", build_output);

        String imei = tManager.getDeviceId();
        imei_tv = (TextView) this.findViewById(R.id.imei_textview);
        imei_tv.setText("IMEI/MEID: " + imei);
        deviceInfo.put("IMEI/MEID", imei);
        
        String imei_sv = tManager.getDeviceSoftwareVersion();
        imei_sv_tv = (TextView) this.findViewById(R.id.imei_sv_textview);
        imei_sv_tv.setText("IMEI SV: " + imei_sv);
        deviceInfo.put("IMEI SV", imei_sv);
        
        String iccid = tManager.getSimSerialNumber();
        iccid_tv = (TextView) this.findViewById(R.id.iccid_textview);
        iccid_tv.setText("ICCID: " + iccid);
        deviceInfo.put("ICCID", iccid);

        String sdk_version = Build.VERSION.RELEASE;
        sdk_tv = (TextView) this.findViewById(R.id.sdk_textview);
        sdk_tv.setText("Android Version: " + sdk_version);
        deviceInfo.put("Android Version", sdk_version);

        String network_name = tManager.getNetworkOperatorName();
        String network_id = tManager.getNetworkOperator();
        String network_output = network_name + " (" + network_id + ")";
        network_name_tv = (TextView) this.findViewById(R.id.network_name_textview);
        network_name_tv.setText("Network: " + network_output);
        deviceInfo.put("Network", network_output);

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
        apn_tv = (TextView) this.findViewById(R.id.apn_textview);
        apn_tv.setText("APN: "  + apn_name);
        deviceInfo.put("APN", apn_name);

//        String radio_version = Build.RADIO;         // not available in sdk v.7
//        TextView radio_version_tv = (TextView) this.findViewById(R.id.radio_version_textview);
//        radio_version_tv.setText("Baseband: " + radio_version);
//        deviceInfo.put("Baseband", radio_version);

        boolean is_roaming = tManager.isNetworkRoaming();
        is_roaming_tv = (TextView) this.findViewById(R.id.is_roaming_textview);
        is_roaming_tv.setText("Roaming: " + is_roaming);
        if (is_roaming) {
            deviceInfo.put("Roaming", "true");
        } else {
            deviceInfo.put("Roaming", "false");
        }
        
        String kernel_version = System.getProperty("os.version");
        kernel_version_tv = (TextView) this.findViewById(R.id.kernel_version_textview);
        kernel_version_tv.setText("Kernel: " + kernel_version);
        deviceInfo.put("Kernel", kernel_version);

        String ssid = wInfo.getSSID();
        ssid_tv = (TextView) this.findViewById(R.id.ssid_textview);
        ssid_tv.setText("Wi-Fi Connection: " + ssid);
        deviceInfo.put("Wi-Fi Connection", ssid);
        
        String mac_add = wInfo.getMacAddress();
        mac_add_tv = (TextView) this.findViewById(R.id.mac_add_textview);
        mac_add_tv.setText("Wi-Fi MAC Address: " + mac_add);
        deviceInfo.put("Wi-Fi MAC Address", mac_add);

        int ip_add_int = wInfo.getIpAddress();
        String ip_add = intToIp(ip_add_int);
        ip_add_tv = (TextView) this.findViewById(R.id.ip_add_textview);
        ip_add_tv.setText("Wi-Fi IP Address: " + ip_add);
        deviceInfo.put("Wi-Fi IP Address", ip_add);
        
        int rssi = wInfo.getRssi();
        int signal_strength = wManager.calculateSignalLevel(rssi, 40);
        signal_strength_tv = (TextView) this.findViewById(R.id.signal_strength_textview);
        signal_strength_tv.setText("Wi-Fi Signal Strength: " + signal_strength + "/40");
        deviceInfo.put("Wi-Fi Signal Strength", signal_strength + "/40");

        NetworkInfo network_info = cManager.getActiveNetworkInfo();
        mobile_network_tv = (TextView) this.findViewById(R.id.mobile_type_textview);
        mobile_state_tv = (TextView) this.findViewById(R.id.mobile_state_textview);
        if (network_info != null) {
            String mobile_network_name = network_info.getSubtypeName();
            mobile_network_tv.setText("Mobile Network Type: " + mobile_network_name);
            deviceInfo.put("Mobile Network Type", mobile_network_name);

            String mobile_state = network_info.getState().name();
            mobile_state_tv.setText("Mobile Network State: " + mobile_state);
            deviceInfo.put("Mobile Network State", mobile_state);
        } else {
            mobile_network_tv.setText("Mobile Network Type: unknown");
            deviceInfo.put("Mobile Network Type", "unknown");
            mobile_state_tv.setText("Mobile Network State: disconnected");
            deviceInfo.put("Mobile Network State", "disconnected");
        }
    }

    public void dumpLog(View button) {
        dumpState();
        LogPrinter logPrinter = new LogPrinter(4, "DeviceDump");
        logPrinter.println("Device Dump");
        for (String key : deviceInfo.keySet()) {
            logPrinter.println(key + ": " + deviceInfo.get(key));
        }
        logPrinter.println(cell_strength_tv.getText().toString());
        long uptime = SystemClock.elapsedRealtime();        
        logPrinter.println("Uptime: " + DateUtils.formatElapsedTime(uptime / 1000));
    }

    private class myPhoneStateListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            cell_strength_tv = (TextView) findViewById(R.id.cell_strength_textview);
            if (signalStrength.isGsm()) {
                int cell_strength = signalStrength.getGsmSignalStrength();
                if (cell_strength != 99) {
                    cell_strength_tv.setText("Cell Signal Strength: " + asu2Dbm(cell_strength) + " dBm");
                } else {
                    cell_strength_tv.setText("Cell Signal Strength: Unknown");
                }
            } else {
                int cell_strength = signalStrength.getEvdoDbm();
                cell_strength_tv.setText("Cell Singal Strength: " + cell_strength + " dBm");
            }
        }

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            super.onServiceStateChanged(serviceState);
            service_state_tv = (TextView) findViewById(R.id.service_state_textview);
            int serv_state = serviceState.getState();
            String serv_string;
            if (serv_state == 0) {
                serv_string = "In Service";
            } else if (serv_state == 1) {
                serv_string = "Out of Service";
            } else if (serv_state == 2) {
                serv_string = "Emergency Only";
            } else if (serv_state == 3) {
                serv_string = "Radio Powered Off";
            } else {
                serv_string = "ERROR";
            }
            service_state_tv.setText("Service State: " + serv_string);
        }
        
        private String asu2Dbm(int asu) {
            Integer dbm = -113 + (2*asu);
            return dbm.toString();
        }
    }

    private static final int EVENT_TICK = 1;
    
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_TICK:
                    updateBatteryStats();
                    sendEmptyMessageDelayed(EVENT_TICK, 1000);

                    break;
            }
        }
    };
    
    private void updateBatteryStats() {
        long uptime = SystemClock.elapsedRealtime();
        uptime_tv = (TextView) findViewById(R.id.uptime_textview);
        uptime_tv.setText("Uptime: " + DateUtils.formatElapsedTime(uptime / 1000));
    }
    
    private String intToIp(int i) {
        return (i & 0xFF) + "." +
               ((i >> 8 ) & 0xFF) + "." +
               ((i >> 16 ) & 0xFF) + "." +
               ((i >> 24 ) & 0xFF );
    }
}