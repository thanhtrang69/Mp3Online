package com.example.trang.mp3online.adapter.recycleradapter.albumdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.trang.mp3online.R;
import com.example.trang.mp3online.entity.Album;

import java.util.ArrayList;

/**
 * Created by Trang on 5/20/2017.
 */

public class PlayTotalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Album> albumArrayList;
    private Context mContext;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private boolean isLoading = false, isDataAvaliable = true;
    private onClickItemAlbumListner clickItemAlbumListner;
    private OnloadMoreListener onloadMoreListener;

    public void setClickItemAlbumListner(onClickItemAlbumListner clickAlbumListner) {
        this.clickItemAlbumListner = clickAlbumListner;
    }

    @Override
    public int getItemViewType(int position) {
        return albumArrayList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public PlayTotalAdapter(ArrayList<Album> albumArrayList, Context mContext) {
        this.albumArrayList = albumArrayList;
        this.mContext = mContext;
    }

    public void setDataAvaliable(boolean isDataAvaliable) {
        this.isDataAvaliable = isDataAvaliable;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == VIEW_TYPE_ITEM) {
            return new PlayTotalAdapter.PlayTotalHolder(inflater.inflate(R.layout.item_total_play, parent, false));

        } else {

            return new LoadHolder(inflater.inflate(R.layout.item_load, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (position >= (getItemCount() - 1) && !isLoading && isDataAvaliable && onloadMoreListener != null) {
            isLoading = true;
            onloadMoreListener. onloadMore();
        }

        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            PlayTotalHolder playTotalHolder = (PlayTotalHolder) holder;
            Glide.with(mContext)
                    .load("http://image.mp3.zdn.vn/" + albumArrayList.get(position).getUrlImage())
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .error(R.drawable.ic_terrain_black_24dp)
                    .into(playTotalHolder.imgTolal);
            playTotalHolder.tvNameAlbum.setText(albumArrayList.get(position).getTitel());
        }
    }

    @Override
    public int getItemCount() {
        return (albumArrayList != null) ? albumArrayList.size() : 0;
    }

    public void addAlbum(ArrayList<Album> albumArrayList) {
        int positionStart = this.albumArrayList.size() - 1;
        this.albumArrayList.addAll(positionStart, albumArrayList);
        positionStart = this.albumArrayList.size() - 1;
        notifyItemRangeInserted(positionStart, albumArrayList.size());
    }

    public void setOnloadMoreListener(OnloadMoreListener onloadMoreListener) {
        this.onloadMoreListener = onloadMoreListener;
    }

    public void showLoading(boolean showLoading) {
        isLoading = showLoading;
        int index = albumArrayList.size() - 1;
        if (showLoading) {
            albumArrayList.add(index, null);
            notifyItemInserted(index);
        } else {
            albumArrayList.remove(index);
            notifyItemRemoved(index);
        }
    }


    public class PlayTotalHolder extends RecyclerView.ViewHolder {
        private ImageView imgTolal;
        private RelativeLayout layoutTotal;
        private TextView tvNameAlbum;

        public PlayTotalHolder(View itemView) {
            super(itemView);
            imgTolal = (ImageView) itemView.findViewById(R.id.img_total_play);
            layoutTotal = (RelativeLayout) itemView.findViewById(R.id.rl_total);
            tvNameAlbum = (TextView) itemView.findViewById(R.id.tv_name_album_total);
            layoutTotal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickItemAlbumListner.onClickItem(albumArrayList.get(getAdapterPosition()));
                }
            });
        }
    }

    public class LoadHolder extends RecyclerView.ViewHolder {

        public LoadHolder(View itemView) {
            super(itemView);
        }
    }


    public interface onClickItemAlbumListner {
        void onClickItem(Album album);
    }

    public interface OnloadMoreListener {
        void onloadMore();
    }
}
