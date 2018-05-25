package com.opweather.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.opweather.R;
import com.opweather.bean.HourForecastsWeatherData;

import java.util.ArrayList;
import java.util.List;

public class HourForecastAdapter extends RecyclerView.Adapter<HourForecastAdapter.ViewHolder> {

    private Context mContext;
    private List<HourForecastsWeatherData> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageViewIcon;
        public TextView mTextViewTemperature;
        public TextView mTextViewTime;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextViewTime = itemView.findViewById(R.id.textViewTime);
            mImageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            mTextViewTemperature = itemView.findViewById(R.id.textViewTemperature);
        }
    }

    public HourForecastAdapter(Context context) {
        mContext = context;
        mDataset = new ArrayList<>();
    }

    public void bindForecastData(List<HourForecastsWeatherData> dataset) {
        mDataset = dataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hour_forecast_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextViewTime.setText(mDataset.get(position).getHourText());
        holder.mImageViewIcon.setImageResource(mDataset.get(position).getWeatherIconId());
        holder.mTextViewTemperature.setText(mDataset.get(position).getTemperature());
    }

    @Override
    public int getItemCount() {
        return mDataset == null ? 0 : mDataset.size();
    }
}
