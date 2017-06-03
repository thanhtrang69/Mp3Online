package com.example.trang.mp3online.adapter.recycleradapter.albumdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.trang.mp3online.R;

import com.example.trang.mp3online.entity.Key;
import com.example.trang.mp3online.entity.Album;

import java.util.ArrayList;

/**
 * Created by Trang on 5/20/2017.
 */

public class ReleaseDateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Album> albumArrayList;
    private Context mContext;
    private boolean isLoading = false;
    private boolean isDataAvaliable = true;
    private OnLoadMoreRelesae loadMoreRelesae;
    private OnClickItemSongListner clickItemSongListner;

    public void setClickItemSongListner(OnClickItemSongListner clickSongListner) {
        this.clickItemSongListner = clickSongListner;
    }

    public void setLoadMoreRelesae(OnLoadMoreRelesae loadMoreRelesae) {
        this.loadMoreRelesae = loadMoreRelesae;
    }

    @Override
    public int getItemViewType(int position) {
        return (albumArrayList.get(position) == null) ? Key.VIEW_TYPE_LOADING : Key.VIEW_TYPE_ITEM;
    }

    public ReleaseDateAdapter(ArrayList<Album> albumArrayList, Context mContext) {
        this.albumArrayList = albumArrayList;
        this.mContext = mContext;
    }


    public void setDataAvaliable(boolean dataAvaliable) {
        isDataAvaliable = dataAvaliable;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        if (viewType == Key.VIEW_TYPE_ITEM) {
            return new ReleaseDateAdapter.ReleaseDateHolder(layoutInflater.inflate(R.layout.item_release, parent, false));
        } else {
            return new LoadMoreRelease(layoutInflater.inflate(R.layout.item_load, parent, false));
        }


    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (position >= (getItemCount() - 1) && !isLoading && isDataAvaliable && loadMoreRelesae != null) {
            isLoading = true;
            loadMoreRelesae.onLoadMore();
        }
        if (getItemViewType(position) == Key.VIEW_TYPE_ITEM) {
            ReleaseDateHolder releaseDateHolder = (ReleaseDateHolder) holder;
            Glide.with(mContext)
                    .load("http://image.mp3.zdn.vn/" + albumArrayList.get(position).getUrlImage())
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .error(R.drawable.ic_terrain_black_24dp)
                    .into(releaseDateHolder.imgRelease);
            releaseDateHolder.tvNameAlbumRelease.setText(albumArrayList.get(position).getTitel());
        }
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

    @Override
    public int getItemCount() {
        return (albumArrayList != null) ? albumArrayList.size() : 0;
    }

    public void addAblum(ArrayList<Album> albumArrayList) {
        int posissonStrat = this.albumArrayList.size() - 1;
        this.albumArrayList.addAll(posissonStrat, albumArrayList);
        posissonStrat = this.albumArrayList.size() - 1;
        notifyItemRangeChanged(posissonStrat, albumArrayList.size());
    }


    public class ReleaseDateHolder extends RecyclerView.ViewHolder {
        private ImageView imgRelease;
        private TextView tvNameAlbumRelease;
        private LinearLayout linearLayout;
        public ReleaseDateHolder(View itemView) {
            super(itemView);
            imgRelease = (ImageView) itemView.findViewById(R.id.img_release);
            tvNameAlbumRelease = (TextView) itemView.findViewById(R.id.tv_name_album_release);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.ll_release);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickItemSongListner.onClickItem(albumArrayList.get(getAdapterPosition()),getAdapterPosition());
                }
            });

        }
    }

    public class LoadMoreRelease extends RecyclerView.ViewHolder {
        public LoadMoreRelease(View itemView) {
            super(itemView);
        }
    }

    public interface OnLoadMoreRelesae {
        void onLoadMore();
    }

    public interface OnClickItemSongListner{
        void onClickItem(Album album,int position);
    }

}
