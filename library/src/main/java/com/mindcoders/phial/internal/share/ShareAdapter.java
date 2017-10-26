package com.mindcoders.phial.internal.share;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mindcoders.phial.R;
import com.mindcoders.phial.ShareDescription;

import java.util.List;

/**
 * Created by rost on 10/26/17.
 */

class ShareAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final List<ShareItem> items;

    ShareAdapter(Context context, List<ShareItem> items) {
        this.inflater = LayoutInflater.from(context);
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ShareItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.listitem_share, parent, false);
            viewHolder = ViewHolder.create(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder.bind(getItem(position));
        return convertView;
    }


    private static class ViewHolder {
        private final ImageView icon;
        private final TextView name;

        ViewHolder(ImageView icon, TextView name) {
            this.icon = icon;
            this.name = name;
        }

        static ViewHolder create(View convertView) {
            TextView name = convertView.findViewById(R.id.name);
            ImageView icon = convertView.findViewById(R.id.icon);
            return new ViewHolder(icon, name);
        }

        void bind(ShareItem shareItem) {
            final ShareDescription description = shareItem.getDescription();
            name.setText(description.getLabel());
            icon.setImageDrawable(description.getDrawable());
        }
    }
}
