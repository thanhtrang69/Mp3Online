package com.example.trang.mp3online.util;

import android.view.animation.Animation;
import android.widget.ImageView;


import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Trang on 5/26/2017.
 */

public interface LoadPhotoAlbum {
  void onLoading( String urlAlbum,ImageView imgAlbum);
  void onLoadingStartAnimation( String urlAlbum,CircleImageView circleImageView,Animation animation);
  void onLoadinged( String urlAlbum,CircleImageView circleImageView);
}
