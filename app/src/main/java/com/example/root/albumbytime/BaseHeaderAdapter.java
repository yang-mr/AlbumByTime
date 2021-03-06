package com.example.root.albumbytime;

import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

/**
 * 继承BaseMultiItemQuickAdapter的一个适配器基类
 */
public abstract class BaseHeaderAdapter<T extends MultiItemEntity> extends BaseMultiItemQuickAdapter<T, BaseViewHolder> {

    public static final int TYPE_HEADER = 1;
    public static final int TYPE_DATA = 2;

    public BaseHeaderAdapter(List<T> data) {
        super(data);
        addItemTypes();
    }

    protected abstract void addItemTypes();

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Util.onAttachedToRecyclerView(recyclerView, this, TYPE_HEADER);
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        Util.onViewAttachedToWindow(holder, this, TYPE_HEADER);
    }
}
