package com.example.trang.mp3online.util;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.trang.mp3online.App;


import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Trang on 5/26/2017.
 */

public class LoadingPhoto implements LoadPhotoAlbum {
    @Override
    public void onLoading(String urlAlbum, ImageView imgAlbum) {
        Glide.with(App.getmContext())
                .load("http://image.mp3.zdn.vn/" + urlAlbum)
                .into(imgAlbum);
    }

    @Override
    public void onLoadingStartAnimation(String urlAlbum, final CircleImageView circleImageView, final Animation animation) {
        Glide.with(App.getmContext())
                .load("http://image.mp3.zdn.vn/" + urlAlbum)
                .asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                circleImageView.setImageBitmap(resource);
                circleImageView.startAnimation(animation);

            }
        });
    }

    @Override
    public void onLoadinged(String urlAlbum, final CircleImageView circleImageView) {
        Glide.with(App.getmContext())
                .load("http://image.mp3.zdn.vn/" + urlAlbum)
                .asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                circleImageView.clearAnimation();
            }
        });
    }


}
