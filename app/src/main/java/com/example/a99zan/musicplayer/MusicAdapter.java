package com.example.a99zan.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 99zan on 2018/1/11.
 */

public class MusicAdapter extends BaseAdapter {

    private Context context;
    private List<MusicBean> data;
    private LayoutInflater inflater;

    public MusicAdapter(Context context, List<MusicBean> data) {
        this.context = context;
        this.data = data;
        LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_list_item, null);
            holder.textView = convertView.findViewById(R.id.text1);
            holder.textView1 = convertView.findViewById(R.id.text2);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(data.get(position).getMusic());
        holder.textView1.setText(data.get(position).getName());

        return convertView;
    }

    private class ViewHolder {
        TextView textView;
        TextView textView1;
    }

}
