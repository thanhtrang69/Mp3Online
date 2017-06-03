package com.example.trang.mp3online.adapter.recycleradapter.songdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.trang.mp3online.R;
import com.example.trang.mp3online.entity.Song;

import java.util.ArrayList;

/**
 * Created by Trang on 5/21/2017.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.HotAdapterHolder> {
    private ArrayList<Song> songArrayList;
    private Context mContext;
    private OnClickSongListener listnerListSongTotal;

    public void setListnerListSongTotal(OnClickSongListener listnerListSongTotal) {
        this.listnerListSongTotal = listnerListSongTotal;
    }

    public SongAdapter(ArrayList<Song> songArrayList, Context mContext) {
        this.songArrayList = songArrayList;
        this.mContext = mContext;

    }

    @Override
    public SongAdapter.HotAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HotAdapterHolder(LayoutInflater.from(mContext).inflate(R.layout.item_list_song, parent, false));
    }

    @Override
    public void onBindViewHolder(SongAdapter.HotAdapterHolder holder, int position) {

        holder.tvNameAlbumHot.setText(songArrayList.get(position).getTitel());
        holder.tvNameAuthorHot.setText(songArrayList.get(position).getArtist());

    }

    @Override
    public int getItemCount() {
        return (songArrayList != null) ? songArrayList.size() : 0;
    }

    public void addAlbum(ArrayList<Song> albumArrayList) {
        int posisson = albumArrayList.size() - 1;
        albumArrayList.addAll(posisson, albumArrayList);
        notifyItemRangeChanged(posisson, albumArrayList.size());
    }

    public class HotAdapterHolder extends RecyclerView.ViewHolder {

        private TextView tvNameAlbumHot;
        private TextView tvNameAuthorHot;
        private LinearLayout linearLayout;

        public HotAdapterHolder(View itemView) {
            super(itemView);
            tvNameAlbumHot = (TextView) itemView.findViewById(R.id.tv_name_song);
            tvNameAuthorHot = (TextView) itemView.findViewById(R.id.tv_name_author);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.ll_list_song_total);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listnerListSongTotal.onClickSong(songArrayList.get(getAdapterPosition()),getAdapterPosition());
                }
            });
        }
    }

    public interface OnClickSongListener {
        void onClickSong(Song song,int position);
    }
}
