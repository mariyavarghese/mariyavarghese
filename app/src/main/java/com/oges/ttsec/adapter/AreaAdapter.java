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

public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.AreaViewHolder> {

private Context context;
private List<String> areaList;

    public AreaAdapter(Context context, List<String> areaList) {
        this.context=context;
        this.areaList=areaList;
    }

    @NonNull
    @Override
    public AreaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_view_layout, null);
        return new AreaAdapter.AreaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AreaViewHolder holder, int position) {
        String str_zone = areaList.get(position);
        holder.tv_zone.setText(str_zone);

    }

    @Override
    public int getItemCount() {
        return areaList.size();
    }

    public class AreaViewHolder extends RecyclerView.ViewHolder{

        TextView tv_zone;
        public AreaViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_zone=itemView.findViewById(R.id.tv_zone);
        }
    }
}
