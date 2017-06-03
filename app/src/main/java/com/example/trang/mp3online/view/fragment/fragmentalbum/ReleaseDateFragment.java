package com.example.trang.mp3online.view.fragment.fragmentalbum;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trang.mp3online.R;
import com.example.trang.mp3online.activity.MainActivity;
import com.example.trang.mp3online.adapter.recycleradapter.albumdapter.ReleaseDateAdapter;

import com.example.trang.mp3online.entity.Key;
import com.example.trang.mp3online.api.Mp3API;
import com.example.trang.mp3online.entity.Album;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Trang on 5/20/2017.
 */

public class ReleaseDateFragment extends Fragment implements Mp3API.OnResponseJsonCallback {
    private ProgressDialog mProgress;
    private RecyclerView recyclerView;
    private View view;
    private ReleaseDateAdapter releaseDateAdapter;
    private Mp3API mp3API;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_release_date, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rc_release_date);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(gridLayoutManager);
        mProgress = new ProgressDialog(getContext());
        mProgress.setMessage("Loading...");
        mProgress.show();

        mp3API = new Mp3API();
        mp3API.setOnResponseJsonCallback(this);
        mp3API.getAlbumList(Mp3API.makeRequestData(0, mp3API.GET_PLAY_RELEASE_DATE));
        return view;
    }


    @Override
    public void onResponse(JSONObject jsonObject) {
        ArrayList<Album> albumArrayList = null;
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(Key.NAME_ARRAY);
            albumArrayList = new ArrayList<>();
            if (releaseDateAdapter != null && jsonArray.length() == 0) {
                releaseDateAdapter.setDataAvaliable(false);
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Album album = new Album();
                if (object.has(Key.PLAYLIST_ID)) {
                    album.setId(object.getInt(Key.PLAYLIST_ID));
                }
                if (object.has(Key.TITLE)) {
                    album.setTitel(object.getString(Key.TITLE));
                }
                if (object.has(Key.LINK)) {
                    album.setLink(object.getString(Key.LINK));
                }
                if (object.has(Key.ARTIST)) {
                    if (object.has(Key.COVER)) {
                        album.setUrlImage(object.getString(Key.COVER));
                    }
                    album.setArtist(object.getString(Key.ARTIST));
                }
                if (object.has(mp3API.GET_PLAY_RELEASE_DATE)) {
                    album.setSort(object.getString(mp3API.GET_PLAY_RELEASE_DATE));
                }
                albumArrayList.add(album);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showAlbum(albumArrayList);
    }

    private void showAlbum(final ArrayList<Album> albumArrayList) {
        if (releaseDateAdapter == null) {
            releaseDateAdapter = new ReleaseDateAdapter(albumArrayList, getContext());
            recyclerView.setAdapter(releaseDateAdapter);
            releaseDateAdapter.setLoadMoreRelesae(new ReleaseDateAdapter.OnLoadMoreRelesae() {
                @Override
                public void onLoadMore() {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            releaseDateAdapter.showLoading(true);
                            mp3API.getAlbumList(Mp3API.makeRequestData(releaseDateAdapter.getItemCount(), mp3API.GET_PLAY_RELEASE_DATE));
                            mProgress.dismiss();

                        }
                    });
                }
            });
            releaseDateAdapter.setClickItemSongListner(new ReleaseDateAdapter.OnClickItemSongListner() {
                @Override
                public void onClickItem(Album album, int position) {
                    ((MainActivity)getActivity()).showFragmentSongRelease(album.getId(),album.getUrlImage(),album.getTitel());
                }
            });
            mProgress.dismiss();
        } else {
            releaseDateAdapter.showLoading(false);
            releaseDateAdapter.addAblum(albumArrayList);
        }
    }
}

