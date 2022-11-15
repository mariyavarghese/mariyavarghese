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

public class VenueAdapter extends RecyclerView.Adapter<VenueAdapter.VenueViewHolder> {

private Context context;
private List<String> venueList;

    public VenueAdapter(Context context, List<String> venueList) {
        this.context=context;
        this.venueList=venueList;
    }

    @NonNull
    @Override
    public VenueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_view_layout, null);
        return new VenueAdapter.VenueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VenueViewHolder holder, int position) {
//        if(venueList.size() ==0){
//            holder.tv_zone.setText("Null");
//        }
        String str_zone = venueList.get(position);
        holder.tv_zone.setText(str_zone);

    }

    @Override
    public int getItemCount() {
        return venueList.size();
    }

    public class VenueViewHolder extends RecyclerView.ViewHolder{

        TextView tv_zone;
        public VenueViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_zone=itemView.findViewById(R.id.tv_zone);
        }
    }
}
