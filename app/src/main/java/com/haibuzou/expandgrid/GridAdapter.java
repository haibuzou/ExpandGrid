package com.haibuzou.expandgrid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/8.
 */
public class GridAdapter extends BaseAdapter{

    private List<String> jobList = new ArrayList<>();
    private LayoutInflater inflater;

    public GridAdapter(List<String> jobList,Context context) {
        this.jobList = jobList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return jobList.size();
    }

    @Override
    public Object getItem(int position) {
        return jobList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if(convertView == null){
            convertView = inflater.inflate(R.layout.gridview_item,null);
            holder.itemtext = (TextView)convertView.findViewById(R.id.grid_item_text);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        holder.itemtext.setText(jobList.get(position));
        return convertView;
    }

    class ViewHolder{
        TextView itemtext;
    }
}
