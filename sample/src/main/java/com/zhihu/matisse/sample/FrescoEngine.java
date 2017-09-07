package com.zhihu.matisse.sample;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.zhihu.matisse.engine.ImageEngine;

import io.github.lizhangqu.fresco.FrescoLoader;

/**
 * Created by bvin on 2017/9/7.
 */

public class FrescoEngine implements ImageEngine {
    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        FrescoLoader.with(context)
                .placeholder(placeholder)
                .resize(resize, resize)
                .progressiveRenderingEnabled(true)
                .load(uri)
                .fadeDuration(300)
                .actualScaleType(ImageView.ScaleType.CENTER_CROP)
                .into(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {

    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        FrescoLoader.with(context)
                .resize(resizeX, resizeY)
                .progressiveRenderingEnabled(true)
                .load(uri)
                .fadeDuration(300)
                .actualScaleType(ImageView.ScaleType.CENTER_CROP)
                .into(imageView);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {

    }

    @Override
    public boolean supportAnimatedGif() {
        return false;
    }
}
