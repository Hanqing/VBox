package com.smart.vbox.model.event;

import android.net.wifi.ScanResult;

import java.util.List;

/**
 * Created by Hanqing on 2015/11/26.
 */
public class WifiScanEvent {

    private List<ScanResult> mScanResults;

    public WifiScanEvent(List<ScanResult> results) {
        mScanResults = results;
    }

    public List<ScanResult> getScanResult() {
        return mScanResults;
    }

}

