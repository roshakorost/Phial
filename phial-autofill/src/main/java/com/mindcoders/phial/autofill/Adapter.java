package com.mindcoders.phial.autofill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rost on 11/3/17.
 */

class Adapter extends BaseAdapter {
    interface OnItemClickedListener {
        void onItemClicked(FillOption option);
    }

    private List<FillOption> options = new ArrayList<>();
    private final LayoutInflater inflater;
    private OnItemClickedListener listener;

    Adapter(Context context) {
        this(context, null);
    }

    Adapter(Context context, OnItemClickedListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    public void setListener(OnItemClickedListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return options.size();
    }

    @Override
    public FillOption getItem(int position) {
        return options.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    void swapData(List<FillOption> options) {
        this.options = options;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Button button;
        if (convertView != null) {
            button = (Button) convertView;
        } else {
            button = (Button) inflater.inflate(R.layout.listview_button_item, parent, false);
        }
        final FillOption item = getItem(position);
        button.setText(item.getName());
        button.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClicked(item);
            }
        });

        return button;
    }
}
