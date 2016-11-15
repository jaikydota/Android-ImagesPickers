package com.jaiky.imagespickers.container;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter {

	protected Context mContext;
	protected List<T> mDatas;
	protected int layoutId;

	public CommonAdapter(Context context, List<T> data, int layoutId) {
		this.mContext = context;
		this.mDatas = data;
		this.layoutId = layoutId;
	}

	@Override
	public int getCount() {
		return mDatas == null ? 0 : mDatas.size();
	}

	@Override
	public T getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void onDataChange(List<T> data) {
		this.mDatas = data;
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = ViewHolder.get(mContext, convertView, parent,
				layoutId, position);
		convert(holder, mDatas.get(position));
		return holder.getConvertView();
	}

	public abstract void convert(ViewHolder holder, T data);

}
