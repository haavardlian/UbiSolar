package com.sintef_energy.ubisolar.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.model.NewsFeed;

import java.util.List;

/**
 * Created by baier on 5/2/14.
 */
public class NewsFeedAdapter extends ArrayAdapter<NewsFeed> {



        protected Context context;
        protected int resource;
        List<NewsFeed> newsFeedList = null;

        public NewsFeedAdapter(Context context, int resource, List<NewsFeed> newsFeedList) {
            super(context, resource);

            this.context = context;
            this.resource = resource;
            this.newsFeedList = newsFeedList;
        }

        @Override
        public void add(NewsFeed object) {
            newsFeedList.add(object);
            notifyDataSetChanged();
        }

        @Override
        public void clear() {
            newsFeedList.clear();
            notifyDataSetChanged();
        }

        @Override
        public NewsFeed getItem(int position) {
            return newsFeedList.get(position);
        }

        @Override
        public int getCount() {
            return newsFeedList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            NewsHolder holder = null;

            if(row == null) {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(resource, parent, false);

                holder = new NewsHolder();
                holder.content = (TextView)row.findViewById(R.id.news_feed);

                row.setTag(holder);
            } else {
                holder = (NewsHolder)row.getTag();
            }

            if(!newsFeedList.isEmpty()) {
                NewsFeed story = newsFeedList.get(position);
                holder.content.setText(story.getNewsContent());
            }
            return row;
        }

        static class NewsHolder {
            TextView content;
        }

    }


