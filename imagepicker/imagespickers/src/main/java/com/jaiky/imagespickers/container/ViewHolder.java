package com.jaiky.imagespickers.container;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ViewHolder {

	private SparseArray<View> mViews;
	private int mPostion;
	private View mConvertView;
	private Context mContext;

	public ViewHolder(Context context, ViewGroup parent, int layoutId,
					  int postion) {
		this.mPostion = postion;
		this.mViews = new SparseArray<View>();
		this.mConvertView = LayoutInflater.from(context)
				.inflate(layoutId, null);
		mConvertView.setTag(this);
		mContext = context;
	}

	public static ViewHolder get(Context context, View convertView,
								 ViewGroup parent, int layoutId, int postion) {
		if (convertView == null) {
			return new ViewHolder(context, parent, layoutId, postion);
		}
		else {
			ViewHolder holder = (ViewHolder) convertView.getTag();
			holder.mPostion = postion;
			return holder;
		}
	}

	public int getPostion(){
		return mPostion;
	}

	public View getConvertView() {
		return mConvertView;
	}


	public <T extends View> T getView(int viewId) {
		View view = mViews.get(viewId);
		if (view == null) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}



	public ViewHolder setText(int viewId, String text) {
		TextView tv = getView(viewId);
		tv.setText(text);
		return this;
	}


	public ViewHolder setVisible(int viewId, int visible){
		View view = getView(viewId);
		view.setVisibility(visible);
		return this;
	}

	public ViewHolder setProgressBar(int viewId, int now, int max) {
		ProgressBar pb = getView(viewId);
		pb.setMax(max);
		pb.setProgress(now);
		return this;
	}

	public ViewHolder setImageResource(int viewId, int resId) {
		ImageView iv = getView(viewId);
		iv.setImageResource(resId);
		return this;
	}

	public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
		ImageView iv = getView(viewId);
		iv.setImageBitmap(bm);
		return this;
	}
}
