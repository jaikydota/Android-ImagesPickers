package com.jaiky.imagespickers.container;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jaiky.imagespickers.ImageLoader;
import com.jaiky.imagespickers.R;
import com.jaiky.imagespickers.preview.MultiImgShowActivity;
import com.jaiky.imagespickers.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class SimpleImageAdapter extends CommonAdapter<String> {

	private boolean isDelete;

	private FrameLayout.LayoutParams fl = null;

	private int containerWidth = 1080;

    private int rowCount = 4;

    private ImageLoader imageLoader;


	private void initImgSize() {
		//带删除
		if (isDelete) {
            // 如果行数为4，中间间隔3个3dp共9dp + 距离右边4个8dp共32dp
            int size = containerWidth - Utils.dip2px(mContext, 3 * (rowCount -1) + 8 * rowCount);
            size = Math.round(size / (float)rowCount);
            fl = new FrameLayout.LayoutParams(size, size);
            //填充上右，为删除按钮让出空间
			fl.setMargins(0, Utils.dip2px(mContext, 8), Utils.dip2px(mContext, 8), 0);
		}
		//不带删除
		else {
			// 如果行数为4，中间间隔3个10dp共30dp
			int size = containerWidth - Utils.dip2px(mContext, 10 * (rowCount -1));
			size = Math.round(size / (float)rowCount);
			fl = new FrameLayout.LayoutParams(size, size);
		}
	}


	public SimpleImageAdapter(ViewGroup container, boolean isDelete, int rowCount) {
		super(container.getContext(), null, R.layout.activity_gradview_item);
        this.containerWidth = container.getMeasuredWidth();
		this.isDelete = isDelete;
        this.rowCount = rowCount;
        initImgSize();
	}

    //更新数据
    public void refreshData(List<String> datas, ImageLoader imageLoader){
        this.imageLoader = imageLoader;
        onDataChange(datas);
    }


	@Override
	public void convert(final ViewHolder holder, final String data) {
		ImageView ivImage = holder.getView(R.id.activity_item_ivImage);
		ivImage.setLayoutParams(fl);

        imageLoader.displayImage(mContext, data, ivImage);

		ivImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, MultiImgShowActivity.class);
				intent.putStringArrayListExtra("photos", (ArrayList<String>)mDatas);
				intent.putExtra("position", holder.getPostion());
				Activity ac = (Activity) mContext;
				ac.startActivity(intent);
				ac.overridePendingTransition(R.anim.zoom_in, 0);
			}
		});

        //是否带删除
		if (isDelete) {
            ImageView ivDelete = holder.getView(R.id.activity_item_ivDelete);
            ivDelete.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    //移除图片
                    mDatas.remove(data);
                    notifyDataSetChanged();
                }
            });
		}
		else {
            holder.setVisible(R.id.activity_item_ivDelete, View.GONE);
		}
	}


}
