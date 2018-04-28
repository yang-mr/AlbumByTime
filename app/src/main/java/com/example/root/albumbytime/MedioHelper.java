package com.example.root.albumbytime;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import com.example.root.albumbytime.models.Album;
import com.example.root.albumbytime.models.AlbumItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Created by jack
 * On 18-3-7:下午5:22
 * Desc:
 */

public class MedioHelper {
    public static ArrayList<Album> albumsByTime;

    private static final String filterPathByTime1 = "DCIM";
    private static final String filterPathByTime2 = "相机";
    private static final String[] projectionByTime = new String[]{
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            BaseColumns._ID};
    public static void loadAlbumsByDate(final Activity context, boolean isRefresh, final OnMediaLoadedCallback callback) {
        if (!isRefresh && albumsByTime != null && callback != null) {
            callback.onMediaLoaded(albumsByTime);
            return;
        }

        final long startTime = System.currentTimeMillis();

        final ArrayList<Album> albums = new ArrayList<>();

        // Return only image metadata.
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

        Uri queryUri = MediaStore.Files.getContentUri("external");

        final CursorLoader cursorLoader = new CursorLoader(
                context,
                queryUri,
                projectionByTime,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final Cursor cursor = cursorLoader.loadInBackground();

                if (cursor == null) {
                    return;
                }
                if (cursor.moveToFirst()) {
                    String path;
                    long dateTaken, id;
                    int pathColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                    int idColumn = cursor.getColumnIndex(BaseColumns._ID);

                    do {
                        path = cursor.getString(pathColumn);
                        if(!path.contains(filterPathByTime1) && !path.contains(filterPathByTime2)) {
                            continue;
                        }
                        AlbumItem albumItem = AlbumItem.getInstance(path);
                        if (albumItem != null) {
                            //set dateTaken
                            int dateTakenColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
                            dateTaken = cursor.getLong(dateTakenColumn);
                            albumItem.setDate(dateTaken);

                            id = cursor.getLong(idColumn);
                            Uri uri = ContentUris.withAppendedId(
                                    MediaStore.Files.getContentUri("external"), id);
                            albumItem.setUri(uri);

                            //search bucket
                            boolean foundBucket = false;
                            Iterator<Album> iterator = albums.iterator();
                            String itemDate = Util.timeStamp2Date(dateTaken);
                            while (iterator.hasNext()) {
                                Album album = iterator.next();
                                if (album.getPath().equals(itemDate)) {
                                    album.getAlbumItems().add(albumItem);
                                    foundBucket = true;
                                    break;
                                }
                            }

                            if (!foundBucket) {
                                //no bucket found
                                if (itemDate != null) {
                                    Album album = new Album(itemDate);
                                    album.getAlbumItems().add(albumItem);
                                    albums.add(album);
                                }
                            }
                        }

                    } while (cursor.moveToNext());
                }
                cursor.close();

                //done loading media with content resolver
                albumsByTime = albums;
                if (callback != null) {
                    callback.onMediaLoaded(albums);
                }

                Log.d("jack", String.valueOf(System.currentTimeMillis() - startTime) + " ms");
            }
        });
    }

   interface OnMediaLoadedCallback {
        void onMediaLoaded(ArrayList<Album> albums);
    }
}
