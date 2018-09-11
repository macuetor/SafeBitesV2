package com.udacity.safebites.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {
    /**
     *
     */
    private final LinearLayoutManager mLinearLayoutManager;

    protected PaginationScrollListener(LinearLayoutManager layoutManager) {
        this.mLinearLayoutManager = layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = mLinearLayoutManager.getChildCount();
        int totalItemCount = mLinearLayoutManager.getItemCount();
        int firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0) {
                loadMoreItems();
            }
        }
    }

    protected abstract boolean isLastPage();

    protected abstract boolean isLoading();

    protected abstract void loadMoreItems();
}