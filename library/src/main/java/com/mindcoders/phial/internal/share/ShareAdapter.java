package com.mindcoders.phial.internal.share;

import com.mindcoders.phial.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ShareViewHolder> {
    interface OnItemClickedListener<T> {
        void onItemClicked(T item);
    }

    private final List<ShareItem> items;
    private OnItemClickedListener<ShareItem> clickedListener;

    ShareAdapter() {
        this(Collections.<ShareItem>emptyList());
    }

    ShareAdapter(List<ShareItem> items) {
        this.items = new ArrayList<>(items);
    }

    void swapData(List<ShareItem> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    void setClickedListener(OnItemClickedListener<ShareItem> clickedListener) {
        this.clickedListener = clickedListener;
    }

    @Override
    public ShareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ShareViewHolder(inflater.inflate(R.layout.item_share, parent, false));
    }

    @Override
    public void onBindViewHolder(ShareViewHolder holder, int position) {
        ShareItem shareItem = items.get(position);
        holder.bind(shareItem);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ShareViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView tvName;
        private final ImageView ivIcon;

        ShareViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.name);
            ivIcon = itemView.findViewById(R.id.icon);
            itemView.setOnClickListener(this);
        }

        void bind(final ShareItem shareItem) {
            tvName.setText(shareItem.getDescription().getLabel());
            ivIcon.setImageDrawable(shareItem.getDescription().getDrawable());
        }

        @Override
        public void onClick(View v) {
            if (clickedListener != null) {
                clickedListener.onItemClicked(items.get(getAdapterPosition()));
            }
        }
    }

}
