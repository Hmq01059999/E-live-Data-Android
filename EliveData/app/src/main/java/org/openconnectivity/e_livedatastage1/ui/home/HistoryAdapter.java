package org.openconnectivity.e_livedatastage1.ui.home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openconnectivity.e_livedatastage1.R;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<History> mHistoryList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView liveContent;
        TextView liveTime;
        TextView liveDuration;
        View historyView;


        public ViewHolder(@NonNull View view) {
            super(view);
            historyView = view;
            liveContent = (TextView) view.findViewById(R.id.history_liveContent);
            liveTime = (TextView) view.findViewById(R.id.history_liveTime);
            liveDuration = (TextView) view.findViewById(R.id.history_liveDuration);
        }
    }

    public HistoryAdapter(List<History> historyList){
        mHistoryList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.historyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                History history = mHistoryList.get(position);
                //跳转到直播数据分析页面
                Intent intent = new Intent(view.getContext(),HistoryInfoActivity.class);
                view.getContext().startActivity(intent);

            }
        });
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            History history = mHistoryList.get(position);
            holder.liveContent.setText(history.getLiveContent());
            holder.liveTime.setText(history.getLiveTime());
            holder.liveDuration.setText(history.getLiveDuration());
    }

    @Override
    public int getItemCount() {
        return mHistoryList.size();
    }
}
