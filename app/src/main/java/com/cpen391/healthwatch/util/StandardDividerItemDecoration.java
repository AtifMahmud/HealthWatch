package com.cpen391.healthwatch.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;

/**
 * Created by william on 2018/3/27.
 * A divider that doesn't draw divider on header and footer view.
 */
public class StandardDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;

    /**
     * Use default dividers set by android.
     * @param context context.
     */
    public StandardDividerItemDecoration(Context context) {
        int[] attrs = {android.R.attr.listDivider};
        TypedArray ta = context.obtainStyledAttributes(attrs);
        Drawable divider = ta.getDrawable(0);
        ta.recycle();
        mDivider = divider;
    }

    public StandardDividerItemDecoration(Context context, int resId) {
        mDivider = ContextCompat.getDrawable(context, resId);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        ViewHolder topView = parent.findViewHolderForAdapterPosition(0);
        ViewHolder botView = parent.findViewHolderForAdapterPosition(parent.getAdapter().getItemCount() - 1);
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        // Skip header
        int i = topView == null ? 0 : 1;
        // Don't put divider after last item
        int end = botView == null ? childCount : childCount - 1;
        for (; i < end; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
