package com.example.root.albumbytime;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by root on 17-12-5.
 *
 * @DESCRIPTION --------------------
 */

public class GlideUtil {
    public interface OnImageLoaded {
        void imageLoaded(View markerView, String url);
    }

    public static void showImgByUrl(Context context, RequestOptions options, String url, ImageView view) {
        showImgByUrl(context, options, url, view, true);
    }

    public static void showImgByUrl(Context context, RequestOptions options, String url, ImageView view, boolean isCircleCrop) {
        showImgByUrl(context, options, url, view, isCircleCrop, R.drawable.default_avatar);
    }

    public static void showImgByUrl(Context context, RequestOptions options, String url, ImageView view, boolean isCircleCrop, int errorResId) {
        if (options == null) {
            options = new RequestOptions()
                    .dontAnimate()//防止设置placeholder导致第一次不显示网络图片,只显示默认图片的问题
                    .error(errorResId)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            if (isCircleCrop) {
                options.circleCrop();
            }
        }
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(view);
    }

    public static void loadLocalRes(Context context, String url, ImageView view) {
        Glide.with(context)
                .load(url)
                .apply(getReqOptions())
                .into(view);
    }

    private static RequestOptions getReqOptions() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.isMemoryCacheable();
        return requestOptions;
    }
}
