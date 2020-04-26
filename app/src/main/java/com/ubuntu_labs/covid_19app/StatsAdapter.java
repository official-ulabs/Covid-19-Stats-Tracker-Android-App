package com.ubuntu_labs.covid_19app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.MyViewHolder> {

    List<StatsJava> statsJavaList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView confirmed, recovered, deaths, country;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            confirmed = itemView.findViewById(R.id.confirmedStat);
            recovered = itemView.findViewById(R.id.recoveredStat);
            deaths = itemView.findViewById(R.id.deathStat);
            country = itemView.findViewById(R.id.countryStat);
        }
    }

    public StatsAdapter(List<StatsJava> statsJavaList) {
        this.statsJavaList = statsJavaList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stats_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        StatsJava data = statsJavaList.get(position);

        holder.country.setText(data.getCountry());
        holder.recovered.setText(data.getRecovered());
        holder.deaths.setText(data.getDeaths());
        holder.confirmed.setText(data.getConfirmed());
    }

    @Override
    public int getItemCount() {
        return statsJavaList.size();
    }
}
