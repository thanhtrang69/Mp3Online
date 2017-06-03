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
import com.example.trang.mp3online.adapter.recycleradapter.albumdapter.PlayTotalAdapter;
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

public class TotalPlayFragment extends Fragment implements Mp3API.OnResponseJsonCallback {

    private RecyclerView recyclerView;
    private View view;
    private PlayTotalAdapter totalAdapter;
    private GridLayoutManager gridLayoutManager;
    private Mp3API mp3API;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_total_play, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rc_total);
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mp3API = new Mp3API();
        mp3API.setOnResponseJsonCallback(this);
        mp3API.getAlbumList(Mp3API.makeRequestData(0, Mp3API.GET_PLAY_TOTAL_PLAY));
        return view;
    }


    @Override
    public void onResponse(JSONObject jsonObject) {
        ArrayList<Album> albumArrayList = null;
        try {

            JSONArray jsonArray = jsonObject.getJSONArray(Key.NAME_ARRAY);
            albumArrayList = new ArrayList<>();
            if (totalAdapter != null && jsonArray.length() == 0) {
                totalAdapter.setDataAvaliable(false);
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
        if (totalAdapter == null) {
            totalAdapter = new PlayTotalAdapter(albumArrayList, getActivity());
            recyclerView.setAdapter(totalAdapter);
            totalAdapter.setOnloadMoreListener(new PlayTotalAdapter.OnloadMoreListener() {
                @Override
                public void onloadMore() {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            totalAdapter.showLoading(true);
                            mp3API.getAlbumList(Mp3API.makeRequestData(totalAdapter.getItemCount(), mp3API.GET_PLAY_TOTAL_PLAY));
                            progressDialog.dismiss();

                        }
                    });
                }
            });
            totalAdapter.setClickItemAlbumListner(new PlayTotalAdapter.onClickItemAlbumListner() {
                @Override
                public void onClickItem(Album album) {

                    ((MainActivity) getActivity()).showFragmentSong(album.getId(), album.getUrlImage(), album.getTitel());
                }
            });
            progressDialog.dismiss();
        } else {
            totalAdapter.showLoading(false);
            totalAdapter.addAlbum(albumArrayList);
        }
    }

}

