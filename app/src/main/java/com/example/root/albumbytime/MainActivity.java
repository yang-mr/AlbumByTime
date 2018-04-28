package com.example.root.albumbytime;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.root.albumbytime.models.Album;
import com.example.root.albumbytime.models.AlbumItem;
import com.example.root.albumbytime.models.PinnedHeaderEntity;
import com.example.root.albumbytime.models.TimeItem;
import com.oushangfeng.pinnedsectionitemdecoration.PinnedHeaderItemDecoration;
import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mMainRecyclerView;
    private Activity mContext;
    private BaseHeaderAdapter mMainRecyclerAdapter;
    private List<PinnedHeaderEntity> mData;

    public static final String HANDLECONTENTOBSERVER = "HANDLECONTENTOBSERVER";
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {;
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case HANDLECONTENTOBSERVER:
                    // notify data
                    MedioHelper.loadAlbumsByDate(mContext, true, new OnMediaLoadedByTimeCallback());
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        registerReceiver(broadcastReceiver, new IntentFilter(HANDLECONTENTOBSERVER));
        loadData();
    }

    private void loadData() {
        mMainRecyclerView = findViewById(R.id.main_recyclerview);
        mMainRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false));
        mMainRecyclerView.addItemDecoration(new PinnedHeaderItemDecoration.Builder(BaseHeaderAdapter.TYPE_HEADER).create());
        mMainRecyclerAdapter = new MainRecyclerAdapter(mData);
        mMainRecyclerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mData.get(position).getItemType() == BaseHeaderAdapter.TYPE_HEADER) {
                    return;
                }
                TimeItem item = (TimeItem) mData.get(position).getData();

            }
        });

        mMainRecyclerView.setAdapter(mMainRecyclerAdapter);
        mMainRecyclerAdapter.onAttachedToRecyclerView(mMainRecyclerView);

        setPermissionReq();
    }


    private void setPermissionReq() {
        String[] permission = new String[]
                {
                        Permission.READ_EXTERNAL_STORAGE
                };

        String hintStr = getString(R.string.persission_hint_read_image);
        PermissionUtils.ReqPermission(mContext, new PermissionUtils.IPersissionCallback() {
            @Override
            public void onGranted(List<String> granteds) {
                MedioHelper.loadAlbumsByDate(mContext, false, new OnMediaLoadedByTimeCallback());
            }

            @Override
            public void onDenied(List<String> denieds) {

            }
        }, hintStr, permission);
    }

    private List<PinnedHeaderEntity> adapterData(List<Album> albums) {
        if (albums.isEmpty()) {
            return null;
        }

        List<PinnedHeaderEntity> items = new ArrayList<>();

        Iterator<Album> iterator = albums.iterator();
        while (iterator.hasNext()) {
            Album album = iterator.next();
            String title = album.getPath();
            title = convertToNormal(title);
            items.add(new PinnedHeaderEntity(0, BaseHeaderAdapter.TYPE_HEADER, title));
            int i = 0;
            int size = album.getAlbumItems().size();
            for (int j = 0; j < size; j++) {
                TimeItem timeItem = new TimeItem(album, i++);
                items.add(new PinnedHeaderEntity(timeItem, BaseHeaderAdapter.TYPE_DATA, album.getPath()));
            }
        }
        return items;
    }

    long nowTime = new Date().getTime();
    long yesterdayTime = nowTime - 24 * 60 * 60 * 1000;
    private String convertToNormal(String title) {
        String nowStr = Util.timeStamp2Date(nowTime);
        if (title.equals(nowStr)) {
            return getString(R.string.hint_today) + " " + title;
        } else if (title.equals(Util.timeStamp2Date(yesterdayTime))) {
            return getString(R.string.hint_yesterday) + " " + title;
        } else {
            return title;
        }
    }

    private void sortForDate(List<Album> albums) {
        Collections.sort(albums, new Comparator<Album>() {
            @Override
            public int compare(Album o1, Album o2) {
                Date date1 = Util.str2Date(o1.getPath());
                Date date2 = Util.str2Date(o2.getPath());
                if (date1.after(date2)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }

    public class MainRecyclerAdapter extends BaseHeaderAdapter<PinnedHeaderEntity<TimeItem>> {

        public MainRecyclerAdapter(List data) {
            super(data);
        }

        private SparseIntArray mRandomHeights;

        @Override
        protected void addItemTypes() {
            addItemType(BaseHeaderAdapter.TYPE_HEADER, R.layout.item_header_recyclerview);
            addItemType(BaseHeaderAdapter.TYPE_DATA, R.layout.item_data_recyclerview);
        }

        @Override
        protected void convert(BaseViewHolder holder, PinnedHeaderEntity<TimeItem> item) {
            switch (holder.getItemViewType()) {
                case BaseHeaderAdapter.TYPE_HEADER:
                    holder.setText(R.id.tv_title, item.getPinnedHeaderName());
                    break;
                case BaseHeaderAdapter.TYPE_DATA:
           /*         int position = holder.getLayoutPosition();

                    if (mMainRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                        // 瀑布流布局记录随机高度，就不会导致Item由于高度变化乱跑，导致画分隔线出现问题
                        // 随机高度, 模拟瀑布效果.

                        if (mRandomHeights == null) {
                            mRandomHeights = new SparseIntArray(getItemCount());
                        }

                        if (mRandomHeights.get(position) == 0) {
                            mRandomHeights.put(position, DensityUtil.dip2px((int) (100 + Math.random() * 100)));
                        }
                        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
                        lp.height = mRandomHeights.get(position);
                        holder.itemView.setLayoutParams(lp);
                    }*/

                    TimeItem timeItem = item.getData();
                    AlbumItem albumItem = timeItem.getAlbum().getAlbumItems().get(timeItem.getPosition());
                    GlideUtil.loadLocalRes(mContext, albumItem.getPath(), (ImageView) holder.getView(R.id.iv_image));
                    break;
            }
        }
    }

    public class OnMediaLoadedByTimeCallback implements MedioHelper.OnMediaLoadedCallback {
        @Override
        public void onMediaLoaded(ArrayList<Album> albums) {
            sortForDate(albums);

            mData = adapterData(albums);
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // close progressBar
                    if(mData==null)
                        return;
                    mMainRecyclerAdapter.replaceData(mData);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        }
    }
}
