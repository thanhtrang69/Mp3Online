package com.example.trang.mp3online.view.fragment.fragmentalbum;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trang.mp3online.R;
import com.example.trang.mp3online.activity.MainActivity;
import com.example.trang.mp3online.adapter.recycleradapter.albumdapter.HotAdapter;
import com.example.trang.mp3online.entity.Key;
import com.example.trang.mp3online.api.Mp3API;
import com.example.trang.mp3online.entity.Album;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Trang on 5/21/2017.
 */

public class HotFragment extends Fragment implements Mp3API.OnResponseJsonCallback {
    private RecyclerView recyclerView;
    private View view;
    private HotAdapter hotAdapter;
    private GridLayoutManager gridLayoutManager;
    private Mp3API mp3API;
    private ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_hot, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rc_hot);
        gridLayoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        mp3API = new Mp3API();
        mp3API.setOnResponseJsonCallback(this);
        mp3API.getAlbumList(Mp3API.makeRequestData(0, mp3API.GET_PLAY_HOT));
        return view;
    }


    @Override
    public void onResponse(JSONObject jsonObject) {
        ArrayList<Album> albumArrayList = null;
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(Key.NAME_ARRAY);
            albumArrayList = new ArrayList<>();
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
                if (object.has(mp3API.GET_PLAY_HOT)) {
                    album.setSort(object.getString(mp3API.GET_PLAY_HOT));
                }
                albumArrayList.add(album);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showAlbum(albumArrayList);

    }

    private void showAlbum(final ArrayList<Album> albumArrayList) {
        if (hotAdapter == null) {
            hotAdapter = new HotAdapter(albumArrayList, getContext());
            recyclerView.setAdapter(hotAdapter);
            hotAdapter.setLoadMoreListner(new HotAdapter.OnLoadMoreListner() {
                @Override
                public void onLoadMore() {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            hotAdapter.showLoading(true);
                            mp3API.getAlbumList(Mp3API.makeRequestData(hotAdapter.getItemCount(), mp3API.GET_PLAY_HOT));
                            progressDialog.dismiss();

                        }
                    });
                }
            });
          hotAdapter.setClickItemAlbumListner(new HotAdapter.OnClickItemAlbumListner() {
              @Override
              public void onClickItem(Album album, int position) {
                  ((MainActivity)getActivity()).showFragmentSongHot(album.getId(),album.getUrlImage(),album.getTitel());
              }
          });
            progressDialog.dismiss();

        } else {
            hotAdapter.showLoading(false);
            hotAdapter.addAlbum(albumArrayList);
        }
    }
}
