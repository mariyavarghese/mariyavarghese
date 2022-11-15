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

public class PrevilegeAdapter extends RecyclerView.Adapter<PrevilegeAdapter.PrevilegeViewHolder> {

private Context context;
private List<String> previlegeList;

    public PrevilegeAdapter(Context context, List<String> previlegeList) {
        this.context=context;
        this.previlegeList=previlegeList;
    }

    @NonNull
    @Override
    public PrevilegeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_view_layout, null);
        return new PrevilegeAdapter.PrevilegeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrevilegeViewHolder holder, int position) {
        String str_zone = previlegeList.get(position);
        holder.tv_zone.setText(str_zone);

    }

    @Override
    public int getItemCount() {
        return previlegeList.size();
    }

    public class PrevilegeViewHolder extends RecyclerView.ViewHolder{

        TextView tv_zone;
        public PrevilegeViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_zone=itemView.findViewById(R.id.tv_zone);
        }
    }
}
