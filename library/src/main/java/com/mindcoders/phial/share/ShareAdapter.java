package com.mindcoders.phial.share;

import com.mindcoders.phial.R;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ShareViewHolder> {

    private final LayoutInflater inflater;

    private final List<ShareItem> items = new ArrayList<>();

    ShareAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public ShareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ShareViewHolder(inflater.inflate(R.layout.item_share, parent, false));
    }

    @Override
    public void onBindViewHolder(ShareViewHolder holder, int position) {
        ShareItem shareItem = items.get(position);
        holder.tvName.setText(shareItem.getDescription().getLabel());
        holder.ivIcon.setImageDrawable(shareItem.getDescription().getDrawable());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void swapData(List<ShareItem> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    static class ShareViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        ImageView ivIcon;

        public ShareViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.name);
            ivIcon = itemView.findViewById(R.id.icon);
        }

    }

}
