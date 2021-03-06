/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhihu.matisse.internal.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.ListPopupWindow;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.Album;
import com.zhihu.matisse.internal.ui.adapter.AlbumsAdapter;
import com.zhihu.matisse.internal.utils.Platform;

public class AlbumsSpinner {

    private static final int MAX_SHOWN_COUNT = 5;
    private AlbumsAdapter mAdapter;
    private TextView mSelected;
    private ListPopupWindow mListPopupWindow;
    private AdapterView.OnItemSelectedListener mOnItemSelectedListener;
    private int mCheckedPosition;
    private Context mContext;

    public AlbumsSpinner(@NonNull Context context) {
        mContext = context;
        mListPopupWindow = new ListPopupWindow(context, null, R.attr.listPopupWindowStyle);
        mListPopupWindow.setModal(true);
        mListPopupWindow.setContentWidth(ListPopupWindow.MATCH_PARENT);
        mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlbumsSpinner.this.onItemClick(parent, view, position, id);
            }
        });
    }

    private void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mCheckedPosition = position;
        AlbumsSpinner.this.onItemSelected(parent.getContext(), position);
        if (mOnItemSelectedListener != null) {
            mOnItemSelectedListener.onItemSelected(parent, view, position, id);
        }
        if (mAdapter != null) {
            mAdapter.setCheckedPosition(position);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        mOnItemSelectedListener = listener;
    }

    public void setSelection(Context context, int position) {
        mListPopupWindow.setSelection(position);
        onItemSelected(context, position);
    }

    public void performItemClick(int position) {
        mSelected.performClick();
        mListPopupWindow.performItemClick(position);
    }

    private void onItemSelected(Context context, int position) {
        mListPopupWindow.dismiss();
        Cursor cursor = mAdapter.getCursor();
        cursor.moveToPosition(position);
        Album album = Album.valueOf(cursor);
        String displayName = album.getDisplayName(context);
        if (mSelected.getVisibility() == View.VISIBLE) {
            mSelected.setText(displayName);
        } else {
            if (Platform.hasICS()) {
                mSelected.setAlpha(0.0f);
                mSelected.setVisibility(View.VISIBLE);
                mSelected.setText(displayName);
                mSelected.animate().alpha(1.0f).setDuration(context.getResources().getInteger(
                        android.R.integer.config_longAnimTime)).start();
            } else {
                mSelected.setVisibility(View.VISIBLE);
                mSelected.setText(displayName);
            }

        }
    }

    public void setAdapter(final AlbumsAdapter adapter) {
        mListPopupWindow.setAdapter(adapter);
        mAdapter = adapter;
    }

    public void setSelectedTextView(TextView textView) {
        mSelected = textView;
        // tint dropdown arrow icon
        Drawable[] drawables = mSelected.getCompoundDrawables();
        Drawable right = drawables[2];
        TypedArray ta = mSelected.getContext().getTheme().obtainStyledAttributes(
                new int[]{R.attr.album_element_color});
        int color = ta.getColor(0, 0);
        ta.recycle();
        right.setColorFilter(color, PorterDuff.Mode.SRC_IN);

        mSelected.setVisibility(View.GONE);
        mSelected.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int itemHeight = v.getResources().getDimensionPixelSize(R.dimen.album_item_height);
                int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, v.getResources().getDisplayMetrics());
                mListPopupWindow.setHeight(
                        mAdapter.getCount() > MAX_SHOWN_COUNT ? itemHeight * MAX_SHOWN_COUNT + margin
                                : itemHeight * mAdapter.getCount() + margin);
                mListPopupWindow.show();
                if (mListPopupWindow.getListView() != null && mCheckedPosition> MAX_SHOWN_COUNT-1 ) {
                    mListPopupWindow.getListView().setSelection(mCheckedPosition);
                }
            }
        });
        mSelected.setOnTouchListener(mListPopupWindow.createDragToOpenListener(mSelected));
    }

    public void setPopupAnchorView(View view) {
        mListPopupWindow.setAnchorView(view);
    }

}
