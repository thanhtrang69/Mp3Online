package com.example.trang.mp3online.entity;

/**
 * Created by Trang on 5/20/2017.
 */

public class Album {
    private int id;
    private String titel;
    private String artist;
    private String link;
    private String urlImage;
    private String sort;

    public Album(int id, String titel, String link, String urlImage, String artist, String sort) {
        this.id = id;
        this.titel = titel;
        this.link = link;
        this.urlImage = urlImage;
        this.artist = artist;
        this.sort = sort;
    }

    public Album(String titel, String link, String urlImage, String artist, String sort) {
        this.titel = titel;
        this.link = link;
        this.urlImage = urlImage;
        this.artist = artist;
        this.sort = sort;
    }


    public Album() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
