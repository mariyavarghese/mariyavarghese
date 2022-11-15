package com.oges.ttsec.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oges.ttsec.R;

import java.util.List;

public class ZoneAdapter extends RecyclerView.Adapter<ZoneAdapter.ZoneViewHolder> {

private Context context;
private List<String> zoneList;

    public ZoneAdapter(Context context, List<String> zoneList) {
        this.context=context;
        this.zoneList=zoneList;
    }

    @NonNull
    @Override
    public ZoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_view_layout, null);
        return new ZoneAdapter.ZoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ZoneViewHolder holder, int position) {
        String str_zone = zoneList.get(position);
        holder.tv_zone.setText(str_zone);

    }

    @Override
    public int getItemCount() {
        return zoneList.size();
    }

    public class ZoneViewHolder extends RecyclerView.ViewHolder{

        TextView tv_zone;
        public ZoneViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_zone=itemView.findViewById(R.id.tv_zone);
        }
    }
}
