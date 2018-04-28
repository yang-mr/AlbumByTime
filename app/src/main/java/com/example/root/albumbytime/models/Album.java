package com.example.root.albumbytime.models;

import java.util.ArrayList;

public class Album {
    private ArrayList<AlbumItem> albumItems;
    private String path;

    public Album(String path) {
        this.path = path;
    }

    public Album() {
        albumItems = new ArrayList<>();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public ArrayList<AlbumItem> getAlbumItems() {
        return albumItems;
    }
}
