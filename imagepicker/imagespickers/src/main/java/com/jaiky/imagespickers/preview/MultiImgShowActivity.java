package com.jaiky.imagespickers.preview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaiky.imagespickers.ImageLoader;
import com.jaiky.imagespickers.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiImgShowActivity extends Activity implements ZoomImageView.OnIsSingleListener {
    private ViewPager viewPager;
    private List<String> imgList;
    private ZoomImageView[] mImageView;
    private TextView numText;
    /**
     * 记录当前页卡
     */
    private int current = 0;

    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_img);
        viewPager = (ViewPager) findViewById(R.id.show_img_viewPager);
        numText = (TextView) findViewById(R.id.showimg_text);

        // 图片地址
        imgList = getIntent().getStringArrayListExtra("photos");
        current = getIntent().getIntExtra("position", 0);



        mImageView = new ZoomImageView[imgList.size()];
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ZoomImageView imageView = new ZoomImageView(MultiImgShowActivity.this);

                String path = imgList.get(position);
                Bitmap bitmap = buffers.get(path);
                if (bitmap == null) {
                    try {
                        buffers.put(path, revitionImageSize(path));
                    }
                    catch (Exception ex) {
                         buffers.put(path, BitmapFactory.decodeFile(path));
                    }
                    bitmap = buffers.get(path);
                }

                imageView.setImageBitmap(bitmap);

                container.addView(imageView);
                setListener(imageView);
                mImageView[position] = imageView;

                return imageView;
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(mImageView[position]);
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return mImageView.length;
            }
        });
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                current = arg0;
                numText.setText(arg0 + 1 + "/" + imgList.size());
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });


        //设置当前选中项
        viewPager.setCurrentItem(current, true);
        numText.setText(current + 1 + "/" + imgList.size());
    }


    @Override
    public void onSingleClick() {
        this.finish();
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(0, R.anim.zoom_out);
    }

    public void setListener(ZoomImageView imageView) {
        imageView.setOnIsSingleListener(this);
    }


    //图片缓存
    private Map<String, Bitmap> buffers = new HashMap<>();

    public static Bitmap revitionImageSize(String path) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(
                new File(path)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        in.close();
        int i = 0;
        Bitmap bitmap = null;
        while (true) {
            if ((options.outWidth >> i <= 1000)
                    && (options.outHeight >> i <= 1000)) {
                in = new BufferedInputStream(
                        new FileInputStream(new File(path)));
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(in, null, options);
                break;
            }
            i += 1;
        }
        return bitmap;
    }

}
