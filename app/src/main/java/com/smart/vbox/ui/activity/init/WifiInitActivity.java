package com.smart.vbox.ui.activity.init;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.smart.vbox.R;
import com.smart.vbox.model.event.WifiScanEvent;
import com.smart.vbox.support.WiFiConnecter;
import com.smart.vbox.support.adapter.recyclerview.NetworkAdapter;
import com.smart.vbox.ui.activity.BaseActivity;
import com.smart.vbox.ui.activity.MainActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by Hanqing on 2015/11/24.
 * TODO:
 * 1.使用EventBus统一管理扫描WIFI信息，连接状态信息；
 * 2.WIFI扫描，WIFI连接放到WiFiConnecter中处理；
 */
public class WifiInitActivity extends BaseActivity implements NetworkAdapter.OnFeedItemClickListener {
    private NetworkAdapter networkAdapter;
    private WiFiConnecter mWifiConnecter;

    @Bind(R.id.rv_network)
    RecyclerView rvNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_init);
        ButterKnife.bind(this);

        setupFeed();

        EventBus.getDefault().register(this);
        mWifiConnecter = new WiFiConnecter(this);
    }

    public void onEvent(WifiScanEvent event) {
        List<ScanResult> results = event.getScanResult();

        Collections.sort(results, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                return lhs.level - rhs.level;
            }
        });

        networkAdapter.setNetworkList(results);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onFeedClick(ScanResult scanResult) {
        //TODO
        ConnectFragment connectFragment =
                new ConnectFragment().newInstance(scanResult);

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        connectFragment.show(transaction, "dialog_connect");
    }

}


