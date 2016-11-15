package com.jaiky.imagespickers;


import android.view.ViewGroup;
import android.widget.GridView;

import com.jaiky.imagespickers.container.GridViewForScrollView;
import com.jaiky.imagespickers.container.SimpleImageAdapter;
import com.jaiky.imagespickers.utils.FileUtils;
import com.jaiky.imagespickers.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;

public class ImageConfig {

    private boolean mutiSelect;
    private int maxSize;

    private boolean showCamera;

    private boolean crop;
    private int aspectX;
    private int aspectY;
    private int outputX;
    private int outputY;

    private ImageLoader imageLoader;

    private int titleBgColor;
    private int titleTextColor;
    private int titleSubmitTextColor;

    private int steepToolBarColor;

    private String filePath;

    private ArrayList<String> pathList;

    private int requestCode;

    //是否开启预览
    private boolean isPreview;

    //适配器
    private SimpleImageAdapter containerAdapter;


    private ImageConfig(final Builder builder) {
        this.maxSize = builder.maxSize;
        this.showCamera = builder.showCamera;
        this.imageLoader = builder.imageLoader;
        this.mutiSelect = builder.mutiSelect;
        this.pathList = builder.pathList;
        this.filePath = builder.filePath;

        this.crop = builder.crop;
        this.aspectX = builder.aspectX;
        this.aspectY = builder.aspectY;
        this.outputX = builder.outputX;
        this.outputY = builder.outputY;

        this.requestCode = builder.requestCode;

        this.titleBgColor = builder.titleBgColor;
        this.titleTextColor = builder.titleTextColor;
        this.titleSubmitTextColor = builder.titleSubmitTextColor;
        this.steepToolBarColor = builder.steepToolBarColor;

        this.isPreview = builder.isPreview;
        this.containerAdapter = builder.containerAdapter;

        FileUtils.createFile(this.filePath);
    }

    public static class Builder implements Serializable {
        private boolean mutiSelect = true;
        private int maxSize = 9;
        private boolean showCamera = false;

        private boolean crop = false;
        private int aspectX = 1;
        private int aspectY = 1;
        private int outputX = 500;
        private int outputY = 500;
        private int requestCode = ImageSelector.IMAGE_REQUEST_CODE;

        private ImageLoader imageLoader;

        private String filePath = "/temp/pictures";

        private int titleBgColor = 0XFF000000;
        private int titleTextColor = 0XFFFFFFFF;
        private int titleSubmitTextColor = 0XFFFFFFFF;

        private int steepToolBarColor = 0XFF000000;

        private ArrayList<String> pathList = new ArrayList<String>();

        private boolean isPreview = true;
        private SimpleImageAdapter containerAdapter;


        public Builder(ImageLoader imageLoader) {
            this.imageLoader = imageLoader;
        }

        public Builder mutiSelect() {
            this.mutiSelect = true;
            return this;
        }

        public Builder crop() {
            this.crop = true;
            return this;
        }

        public Builder crop(int aspectX, int aspectY, int outputX, int outputY) {
            this.crop = true;
            this.aspectX = aspectX;
            this.aspectY = aspectY;
            this.outputX = outputX;
            this.outputY = outputY;
            return this;
        }

        public Builder requestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder pathList(ArrayList<String> pathList) {
            this.pathList = pathList;
            return this;
        }


        public Builder titleBgColor(int titleBgColor) {
            this.titleBgColor = titleBgColor;
            return this;
        }

        public Builder titleTextColor(int titleTextColor) {
            this.titleTextColor = titleTextColor;
            return this;
        }

        public Builder titleSubmitTextColor(int titleSubmitTextColor) {
            this.titleSubmitTextColor = titleSubmitTextColor;
            return this;
        }

        public Builder steepToolBarColor(int steepToolBarColor) {
            this.steepToolBarColor = steepToolBarColor;
            return this;
        }

        public Builder singleSelect() {
            this.mutiSelect = false;
            return this;
        }

        public Builder mutiSelectMaxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }


        public Builder showCamera() {
            this.showCamera = true;
            return this;
        }


        public Builder closePreview() {
            this.isPreview = false;
            return this;
        }

        public Builder setContainer(ViewGroup container){
            return setContainer(container, 4, false);
        }

        public Builder setContainer(ViewGroup container, int rowImageCount, boolean isDelete) {
            if (container.getChildCount() == 0) {
                //新建一个GridView
                GridViewForScrollView gvView = new GridViewForScrollView(container.getContext());
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                gvView.setLayoutParams(layoutParams);

                if (isDelete) {
                    //间距3dp
                    gvView.setHorizontalSpacing(Utils.dip2px(container.getContext(), 3));
                    gvView.setVerticalSpacing(Utils.dip2px(container.getContext(), 3));
                } else {
                    //间距10dp
                    gvView.setHorizontalSpacing(Utils.dip2px(container.getContext(), 10));
                    gvView.setVerticalSpacing(Utils.dip2px(container.getContext(), 10));
                }
                gvView.setNumColumns(rowImageCount);
                gvView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

                //设置适配器，暂时数据为控
                containerAdapter = new SimpleImageAdapter(container, isDelete, rowImageCount);
                gvView.setAdapter(containerAdapter);
                container.addView(gvView);
            }
            else {
                GridViewForScrollView gvView = (GridViewForScrollView) container.getChildAt(0);
                containerAdapter = (SimpleImageAdapter) gvView.getAdapter();
            }
            return this;
        }


        public ImageConfig build() {
            return new ImageConfig(this);
        }
    }

    //获取容器适配器
    public SimpleImageAdapter getContainerAdapter() {
        return containerAdapter;
    }

    public boolean isPreview() {
        return isPreview;
    }

    public boolean isCrop() {
        return crop;
    }

    public int getAspectX() {
        return aspectX;
    }

    public int getAspectY() {
        return aspectY;
    }

    public int getOutputX() {
        return outputX;
    }

    public int getOutputY() {
        return outputY;
    }

    public boolean isMutiSelect() {
        return mutiSelect;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public int getTitleBgColor() {
        return titleBgColor;
    }

    public int getTitleTextColor() {
        return titleTextColor;
    }

    public int getTitleSubmitTextColor() {
        return titleSubmitTextColor;
    }

    public int getSteepToolBarColor() {
        return steepToolBarColor;
    }

    public ArrayList<String> getPathList() {
        return pathList;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getRequestCode() {
        return requestCode;
    }
}

