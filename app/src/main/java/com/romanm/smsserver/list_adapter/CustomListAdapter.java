package com.romanm.smsserver.list_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.romanm.smsserver.R;

import com.romanm.smsserver.client_locker.BlockedClients;

import java.util.List;

public class CustomListAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<BlockedClients> listData;
    private Context context;

    public CustomListAdapter(Context context, List<BlockedClients> listData) {
        this.listData = listData;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_layout, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView)convertView.findViewById(R.id.blockIcon);
            holder.ipAddress = (TextView)convertView.findViewById(R.id.textView_ip);
            holder.blockTime = (TextView)convertView.findViewById(R.id.textView_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        BlockedClients blockedClients = listData.get(position);
        holder.ipAddress.setText(blockedClients.getIpAddress());
        holder.blockTime.setText(blockedClients.getBlockTime());
        holder.imageView.setImageResource(R.mipmap.ervb_lock);


        return convertView;
    }


    static class ViewHolder {
        ImageView imageView;
        TextView ipAddress;
        TextView blockTime;
    }
}
