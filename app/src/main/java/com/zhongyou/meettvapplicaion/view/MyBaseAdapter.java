package com.zhongyou.meettvapplicaion.view;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class MyBaseAdapter extends BaseAdapter {
    protected Context context;
    protected List<Map<String, String>> list;

    public MyBaseAdapter(Context context){
        this.context = context;
        this.list = new ArrayList<>();
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Map<String, String> getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    public void addObject(Map<String, String> map){
        list.add(map);
        this.notifyDataSetChanged();
    }

    public void removeObject(Map<String, String> map) {
        list.remove(map);
        this.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public void clear(){
        list.clear();
        this.notifyDataSetChanged();
    }
}
