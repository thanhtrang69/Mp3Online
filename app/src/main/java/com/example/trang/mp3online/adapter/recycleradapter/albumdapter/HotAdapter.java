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
 * Created by Trang on 5/21/2017.
 */

public class HotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Album> albumArrayList;
    private Context mContext;
    private boolean isloading;
    private boolean isDataAvaliable = true;
    private OnLoadMoreListner loadMoreListner;
    private OnClickItemAlbumListner clickItemAlbumListner;

    public void setClickItemAlbumListner(OnClickItemAlbumListner clickItemAlbumListner) {
        this.clickItemAlbumListner = clickItemAlbumListner;
    }

    public void setLoadMoreListner(OnLoadMoreListner loadMoreListner) {
        this.loadMoreListner = loadMoreListner;
    }

    public HotAdapter(ArrayList<Album> albumArrayList, Context mContext) {
        this.albumArrayList = albumArrayList;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        if (viewType == Key.VIEW_TYPE_ITEM) {
            return new HotAdapterHolder(layoutInflater.inflate(R.layout.item_hot, parent, false));
        } else {
            return new LoadMoreHot(layoutInflater.inflate(R.layout.item_load, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (albumArrayList.get(position) == null) ? Key.VIEW_TYPE_LOADING : Key.VIEW_TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= (getItemCount() - 1) && !isloading && isDataAvaliable && loadMoreListner != null) {
            isloading = true;
            loadMoreListner.onLoadMore();
        }

        if (getItemViewType(position) == Key.VIEW_TYPE_ITEM) {
            HotAdapterHolder hotAdapter = (HotAdapterHolder) holder;
            Glide
                    .with(mContext)
                    .load("http://image.mp3.zdn.vn/" + albumArrayList.get(position).getUrlImage())
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .error(R.drawable.ic_terrain_black_24dp)
                    .into(hotAdapter.imgAlbumHot);
            hotAdapter.tvNameAlbumHot.setText(albumArrayList.get(position).getTitel());

        }
    }

    public void showLoading(boolean showLoading) {
        isloading = showLoading;
        int indext = albumArrayList.size() - 1;
        if (showLoading) {
            albumArrayList.add(indext, null);
            notifyItemInserted(indext);
        } else {
            albumArrayList.remove(indext);
            notifyItemRemoved(indext);
        }
    }

    @Override
    public int getItemCount() {
        return (albumArrayList != null) ? albumArrayList.size() : 0;
    }

    public void addAlbum(ArrayList<Album> albumArrayList) {
        int posisson = this.albumArrayList.size() - 1;
        this.albumArrayList.addAll(posisson, albumArrayList);
        notifyItemRangeChanged(posisson, albumArrayList.size());
    }

    public class HotAdapterHolder extends RecyclerView.ViewHolder {
        private ImageView imgAlbumHot;
        private TextView tvNameAlbumHot;
        private LinearLayout linearLayout;

        public HotAdapterHolder(View itemView) {
            super(itemView);
            imgAlbumHot = (ImageView) itemView.findViewById(R.id.img_hot);
            tvNameAlbumHot = (TextView) itemView.findViewById(R.id.tv_name_album_hot);
            linearLayout= (LinearLayout) itemView.findViewById(R.id.ll_item_hot);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickItemAlbumListner.onClickItem(albumArrayList.get(getAdapterPosition()),getAdapterPosition());
                }
            });
        }
    }

    public class LoadMoreHot extends RecyclerView.ViewHolder {
        public LoadMoreHot(View itemView) {
            super(itemView);

        }
    }

    public interface OnLoadMoreListner {
        void onLoadMore();

    }

    public interface OnClickItemAlbumListner {
        void onClickItem(Album album,int position);
    }
}
