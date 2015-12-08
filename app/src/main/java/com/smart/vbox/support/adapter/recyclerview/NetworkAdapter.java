package com.smart.vbox.support.adapter.recyclerview;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smart.vbox.R;
import com.smart.vbox.support.helper.SignalColor;
import com.smart.vbox.support.utils.CalcUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Hanqing on 2015/11/24.
 */
public class NetworkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private Context context;
    private OnFeedItemClickListener onFeedItemClickListener;
    private List<ScanResult> networkList = new ArrayList<>();

    public void setNetworkList(List<ScanResult> networkList) {
        this.networkList = networkList;
        notifyDataSetChanged();
    }

    public NetworkAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_network, parent, false);
        final CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view);
        cellFeedViewHolder.itemLayout.setOnClickListener(this);
        return cellFeedViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final CellFeedViewHolder holder = (CellFeedViewHolder) viewHolder;
        bindDefaultSearchItem(position, holder);
    }

    private void bindDefaultSearchItem(int position, CellFeedViewHolder holder) {
        final ScanResult network = networkList.get(position);
        holder.ssid.setText(network.SSID);
        holder.bssid.setText(network.BSSID);
        holder.signal.setText(network.level + " dBm");
        holder.signal.setTextColor(SignalColor.getColor(network.level));
        holder.channel.setText("Channel" + ": " + CalcUtils.frequencyConvert(network.frequency));

        holder.itemLayout.setTag(network);

        /* Check ESS */
        if (network.capabilities.contains("ESS")) {
            holder.capBadgeEss.setVisibility(View.VISIBLE);
        } else {
            holder.capBadgeEss.setVisibility(View.INVISIBLE);
        }

        /* Check cryptography */
        if (network.capabilities.contains("WPA2-")) {
            holder.capBadgeCrypto.setImageResource(R.drawable.cap_badge_wpa2);
        } else if (network.capabilities.contains("WPA-")) {
            holder.capBadgeCrypto.setImageResource(R.drawable.cap_badge_wpa);
        } else if (network.capabilities.contains("WEP")) {
            holder.capBadgeCrypto.setImageResource(R.drawable.cap_badge_wep);
        } else {
            holder.capBadgeCrypto.setImageResource(R.drawable.cap_badge_open);
        }

        /* Check WPS */
        if (network.capabilities.contains("WPS")) {
            holder.capBadgeWps.setVisibility(View.VISIBLE);
        } else {
            holder.capBadgeWps.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return networkList.size();
    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        if (viewId == R.id.network_item_layout) {
            if (onFeedItemClickListener != null) {
                onFeedItemClickListener.onFeedClick((ScanResult) view.getTag());
            }
        }
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {
        this.onFeedItemClickListener = onFeedItemClickListener;
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.network_ssid)
        TextView ssid;
        @Bind(R.id.network_bssid)
        TextView bssid;
        @Bind(R.id.network_signal)
        TextView signal;
        @Bind(R.id.network_channel)
        TextView channel;
        @Bind(R.id.cap_badge_ess)
        ImageView capBadgeEss;
        @Bind(R.id.cap_badge_crypto)
        ImageView capBadgeCrypto;
        @Bind(R.id.cap_badge_wps)
        ImageView capBadgeWps;
        @Bind(R.id.network_item_layout)
        RelativeLayout itemLayout;


        public CellFeedViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnFeedItemClickListener {
        public void onFeedClick(ScanResult scanResult);
    }

}
