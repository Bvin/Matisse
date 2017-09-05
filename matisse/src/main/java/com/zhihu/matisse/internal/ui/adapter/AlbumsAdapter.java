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
package com.zhihu.matisse.internal.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.Album;
import com.zhihu.matisse.internal.entity.SelectionSpec;

import java.io.File;

public class AlbumsAdapter extends CursorAdapter {

    private final Drawable mPlaceholder;
    private View mPreviousChecked;
    private View.OnClickListener mOnClickListener;
    private int mCheckedPosition = -1;

    public AlbumsAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);

        TypedArray ta = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.album_thumbnail_placeholder});
        mPlaceholder = ta.getDrawable(0);
        ta.recycle();
    }

    public AlbumsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        TypedArray ta = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.album_thumbnail_placeholder});
        mPlaceholder = ta.getDrawable(0);
        ta.recycle();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.album_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        Album album = Album.valueOf(cursor);
        ((TextView) view.findViewById(R.id.album_name)).setText(album.getDisplayName(context));
        ((TextView) view.findViewById(R.id.album_media_count)).setText(String.valueOf(album.getCount()));
        view.findViewById(R.id.iv_checked).setVisibility(mCheckedPosition == cursor.getPosition() ? View.VISIBLE : View.GONE);
        //view.findViewById(R.id.iv_checked).setVisibility(View.GONE);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPreviousChecked != null) {
                    // 虽然bindView中隐藏了，但是如果在同一个可见高度内并不会再次调用bindView，所以
                    // 要用记录上次选中的那行，点击后隐藏它
                    //mPreviousChecked.setVisibility(View.GONE);
                }
                mPreviousChecked = v;//.findViewById(R.id.iv_checked);
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(v);
                }
                mCheckedPosition = cursor.getPosition();
            }
        });
        // do not need to load animated Gif
        SelectionSpec.getInstance().imageEngine.loadThumbnail(context, context.getResources().getDimensionPixelSize(R
                        .dimen.media_grid_size), mPlaceholder,
                (ImageView) view.findViewById(R.id.album_cover), Uri.fromFile(new File(album.getCoverPath())));
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void showPreviourChecked(){
        if (mPreviousChecked != null) {
            mPreviousChecked.setVisibility(View.VISIBLE);
            mPreviousChecked.setBackgroundColor(Color.parseColor("#624"));
        }
    }


}
