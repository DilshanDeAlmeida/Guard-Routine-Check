package com.example.guardcheck;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HistoryListAdapter extends ArrayAdapter<History> {

    private Context mContext;
    private int mResource;

    public HistoryListAdapter(Context context, int resource, ArrayList<History> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get history information
        String date = getItem(position).getDate();
        String location = getItem(position).getLocation();
        String remark = getItem(position).getRemark();
        String guard = getItem(position).getGuard();

        History historyobj = new History(date,location,remark,guard);
        LayoutInflater inflator = LayoutInflater.from(mContext);
        convertView = inflator.inflate(mResource,parent,false);

        TextView tvDate = (TextView)convertView.findViewById(R.id.textView1);
        TextView tvLocation = (TextView)convertView.findViewById(R.id.textView2);
        TextView tvRemark = (TextView)convertView.findViewById(R.id.textView3);
        TextView tvGuard = (TextView)convertView.findViewById(R.id.textView4);

        tvDate.setText(date);
        tvLocation.setText(location);
        tvRemark.setText(remark);
        tvGuard.setText(guard);

        return convertView;
    }
}
