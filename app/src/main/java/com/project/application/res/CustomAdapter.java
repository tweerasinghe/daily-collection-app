package com.project.application.res;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.project.application.myapplication.R;

public class CustomAdapter extends ArrayAdapter<PaymentHolder> implements View.OnClickListener {
    private ArrayList<PaymentHolder> dataSet;
    Context mContext;

    public CustomAdapter(ArrayList<PaymentHolder> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext = context;

    }

    private static class ViewHolder {
        TextView ticketID, vehicleNo, Date, time, balance;


    }


    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        PaymentHolder dataModel = (PaymentHolder) object;

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PaymentHolder dataModel = getItem(position);

        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.ticketID = (TextView) convertView.findViewById(R.id.historyTicketID);
            viewHolder.vehicleNo = (TextView) convertView.findViewById(R.id.historyVehicleNo);
            viewHolder.Date = (TextView) convertView.findViewById(R.id.historyDate);
            viewHolder.time = (TextView) convertView.findViewById(R.id.historyTime);
            viewHolder.balance = (TextView) convertView.findViewById(R.id.vehBalance);


            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.ticketID.setText(dataModel.getTicketID());
        viewHolder.vehicleNo.setText(dataModel.getVehicleNo());
        viewHolder.Date.setText(dataModel.getDate());
        viewHolder.time.setText(dataModel.getTime());
        viewHolder.balance.setText(dataModel.getBalance());

        return convertView;
    }
}
