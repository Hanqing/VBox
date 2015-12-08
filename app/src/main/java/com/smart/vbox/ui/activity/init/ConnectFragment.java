package com.smart.vbox.ui.activity.init;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.smart.vbox.R;
import com.smart.vbox.support.WiFi;
import com.smart.vbox.support.WiFiConnecter;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Hanqing on 2015/11/24.
 */
public class ConnectFragment extends DialogFragment {

    private static final String SCAN_RESULT = "scan_result";

    @Bind(R.id.wifi_pwd_edit)
    AppCompatEditText wifiPwdEdit;

    @Bind(R.id.connect_confirm)
    TextView connectTv;

    private ScanResult mScanResult;

    public ConnectFragment newInstance(ScanResult scanResult) {
        ConnectFragment f = new ConnectFragment();
        Bundle b = new Bundle();
        b.putParcelable(SCAN_RESULT, scanResult);
        f.setArguments(b);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScanResult = getArguments().getParcelable(SCAN_RESULT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_connect, null);
        ButterKnife.bind(this, view);

        connectTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pwdLen = wifiPwdEdit.getText().length();

                if (pwdLen < 8) {
                    wifiPwdEdit.setError("At least 8 characters");
                    wifiPwdEdit.requestFocus();
                    return;
                }
                //TODO 连接WIFI
                WifiManager mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
                WiFi.connectToNewNetwork(mWifiManager, mScanResult, wifiPwdEdit.getText().toString());
            }
        });

        builder.setView(view);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return builder.create();
    }
}
