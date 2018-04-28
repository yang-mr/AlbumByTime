package com.example.root.albumbytime.models;

import android.net.Uri;
import java.io.File;

public class AlbumItem {
    private String name;
    private String path;
    private long dateTaken;
    public boolean error = false;
    private Uri uri;

    public static AlbumItem getInstance(String path) {
        AlbumItem albumItem = new AlbumItem();
        if (albumItem != null) {
            albumItem.setPath(path).setName(new File(path).getName());
        }
        return albumItem;
    }

    public AlbumItem() {
        name = "";
        path = "";
        dateTaken = -1;
    }

    public AlbumItem setName(String name) {
        this.name = name;
        return this;
    }

    public AlbumItem setPath(String path) {
        this.path = path;
        return this;
    }

    public String getPath() {
        return path;
    }

    public void setDate(long dateTaken) {
        this.dateTaken = dateTaken;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
