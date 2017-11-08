package com.mindcoders.phial.internal.keyvalue;

import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.keyvalue.KVSaver.KVCategory;
import com.mindcoders.phial.internal.keyvalue.KVSaver.KVEntry;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

final class KeyValueAdapter extends BaseAdapter implements ExpandableListAdapter {

    private final LayoutInflater layoutInflater;

    private final List<KVCategory> items = new ArrayList<>();

    KeyValueAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public KVCategory getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getName().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return convertView;
    }

    public void swapData(List<KVCategory> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return getGroup(groupPosition).entries().size();
    }

    @Override
    public KVCategory getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    @Override
    public KVEntry getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).entries().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return items.get(groupPosition).getName().hashCode();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getChild(groupPosition, childPosition).getName().hashCode();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listitem_keyvalue_category, parent, false);
        }

        KVCategory category = getGroup(groupPosition);

        ((TextView) convertView).setText(category.getName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final KeyValueViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listitem_keyvalue_child, parent, false);
            holder = KeyValueViewHolder.fromRootView(convertView);
            convertView.setTag(holder);
        } else {
            holder = (KeyValueViewHolder) convertView.getTag();
        }

        KVEntry entry = getChild(groupPosition, childPosition);
        holder.key.setText(String.format("%s:", entry.getName()));
        holder.value.setText(entry.getValue());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return groupId ^ childId;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return groupId;
    }

    private static class KeyValueViewHolder {
        final TextView key;
        final TextView value;

        KeyValueViewHolder(TextView key, TextView value) {
            this.key = key;
            this.value = value;
        }

        static KeyValueViewHolder fromRootView(View view) {
            final TextView keyTV = view.findViewById(android.R.id.text1);
            final TextView valueTV = view.findViewById(android.R.id.text2);
            return new KeyValueViewHolder(keyTV, valueTV);
        }
    }
}
