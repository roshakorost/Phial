package com.mindcoders.phial.keyvalue;

import com.mindcoders.phial.R;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

final class KeyValueAdapter extends BaseAdapter {

    private final LayoutInflater layoutInflater;

    private final List<Item> items = new ArrayList<>();

    KeyValueAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).value.hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            int layoutId = getLayoutId(getItemViewType(position));
            convertView = layoutInflater.inflate(layoutId, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Item item = getItem(position);

        holder.tvValue.setText(item.value);

        return convertView;
    }

    public void swapData(List<Item> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    private int getLayoutId(int itemViewType) {
        switch (itemViewType) {
            case Item.TYPE_CATEGORY:
                return R.layout.item_category;
            case Item.TYPE_ENTRY:
                return R.layout.item_entry;
            default:
                throw new IllegalArgumentException("invalid itemViewType: " + itemViewType);
        }
    }

    static final class ViewHolder {

        TextView tvValue;

        ViewHolder(View view) {
            tvValue = view.findViewById(R.id.tv_value);
        }

    }

}
