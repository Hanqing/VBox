package com.smart.vbox.ui.activity.init;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.smart.vbox.R;
import com.smart.vbox.bean.WirelessNetwork;
import com.smart.vbox.support.adapter.recyclerview.NetworkAdapter;
import com.smart.vbox.support.utils.CalcUtils;
import com.smart.vbox.ui.activity.BaseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Hanqing on 2015/11/24.
 */
public class WifiInitActivity extends BaseActivity implements NetworkAdapter.OnFeedItemClickListener {
    private WifiManager wifiManager;
    private NetworkAdapter networkAdapter;
    private BroadcastReceiver wifiScanReceiver;
    private boolean isScanning = true;

    @Bind(R.id.rv_network)
    RecyclerView rvNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_init);
        ButterKnife.bind(this);

        setupFeed();

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        wifiScanReceiver = new WifiScanReceiver();
        registerReceiver(wifiScanReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        startScanning();
    }

    private void setupFeed() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvNetwork.setLayoutManager(linearLayoutManager);

        networkAdapter = new NetworkAdapter(this);
        networkAdapter.setOnFeedItemClickListener(this);
        rvNetwork.setAdapter(networkAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScanning();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiScanReceiver);
        stopScanning();

    }

    private void startScanning() {
        isScanning = true;

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        /* Start AsyncTask to scan for networks in the background */
        new WifiScanAsyncTask().execute();
    }

    private void stopScanning() {
        isScanning = false;
    }

    public class WifiScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> results = wifiManager.getScanResults();
            List<WirelessNetwork> networkList = new ArrayList<>();
            for (android.net.wifi.ScanResult result : results) {
                WirelessNetwork network = new WirelessNetwork(result.BSSID, result.SSID,
                        CalcUtils.frequencyConvert(result.frequency), result.level,
                        result.capabilities, new Date().getTime()
                );
                networkList.add(network);
            }


            Collections.sort(networkList, new Comparator<WirelessNetwork>() {
                @Override
                public int compare(WirelessNetwork lhs, WirelessNetwork rhs) {
                    return lhs.getSignal() - rhs.getSignal();
                }
            });


            networkAdapter.setNetworkList(networkList);
        }
    }

    public class WifiScanAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            while (isScanning) {
                wifiManager.startScan();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    public void onFeedClick(String ssid) {
        ConnectFragment connectFragment =
                new ConnectFragment().newInstance(ssid);

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        connectFragment.show(transaction, "dialog_connect");
    }

}


