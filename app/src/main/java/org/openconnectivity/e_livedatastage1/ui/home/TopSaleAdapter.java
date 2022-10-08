package org.openconnectivity.e_livedatastage1.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openconnectivity.e_livedatastage1.R;

import java.util.List;

public class TopSaleAdapter extends RecyclerView.Adapter<TopSaleAdapter.ViewHolder> {

    private List<TopSale> mTopSaleList;
    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView topSaleImage;
        TextView topSaleName;

        public ViewHolder(@NonNull View view) {
            super(view);
            topSaleImage = (ImageView) view.findViewById(R.id.topSale_image);
            topSaleName = (TextView) view.findViewById(R.id.topSale_name);
        }
    }

    public TopSaleAdapter(List<TopSale> topSaleList){
        mTopSaleList = topSaleList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topsale_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TopSale topSale = mTopSaleList.get(position);
        holder.topSaleImage.setImageResource(topSale.getImageId());
        holder.topSaleName.setText(topSale.getName());
    }

    @Override
    public int getItemCount() {
        return mTopSaleList.size();
    }
}
