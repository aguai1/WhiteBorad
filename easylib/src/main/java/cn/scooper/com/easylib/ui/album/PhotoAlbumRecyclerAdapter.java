package cn.scooper.com.easylib.ui.album;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rey.material.widget.CheckBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.scooper.com.easylib.R;
import cn.scooper.com.easylib.utils.JsonUtil;

/**
 * Created by Aguai on 2016/11/19.
 * 自定义相册的adapter
 */
public class PhotoAlbumRecyclerAdapter extends RecyclerView.Adapter<PhotoAlbumRecyclerAdapter.ViewHolder> {

    public static final String FILE_PATH = "FILE_PATH";

    private boolean moreSel;
    private List<PicSelInfoPage> mPhotoList = new ArrayList<>();
    private Activity mContext;
    private OnclickPhotoListener mOnclickPhotoListener;


    public PhotoAlbumRecyclerAdapter(Activity context, List<String> photoList, boolean moreSel) {
        mContext = context;
        if (photoList != null) {
            Collections.reverse(photoList);
            List<PicSelInfoPage> fromString = PicSelInfoPage.createFromString(photoList);
            mPhotoList = fromString;
        }
        this.moreSel = moreSel;
    }

    public String getSelectedPath() {
        List<String> temp = new ArrayList<>();
        for (PicSelInfoPage page : mPhotoList) {
            if (page.selected) {
                temp.add(page.path);
            }
        }
        String s = JsonUtil.getParser().toJson(temp);
        return s;
    }

    public void setOnclickPhotoListener(OnclickPhotoListener listener) {
        this.mOnclickPhotoListener = listener;
    }

    public void setPhotoList(List<String> photoList) {
        if (photoList != null) {
            List<PicSelInfoPage> fromString = PicSelInfoPage.createFromString(photoList);
            mPhotoList = fromString;
        }
    }

    @Override
    public PhotoAlbumRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_album_image_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (moreSel) {
            Glide.with(mContext)
                    .load(mPhotoList.get(position).path)
                    .centerCrop()
                    .placeholder(R.drawable.loading_spinner)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(holder.mImageView);
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setCheckedImmediately(mPhotoList.get(position).selected);
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPhotoList.get(position).selected) {
                        mPhotoList.get(position).selected = false;
                    } else {
                        mPhotoList.get(position).selected = true;
                    }
                }
            });
            holder.mImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    holder.checkBox.setCheckedImmediately(!mPhotoList.get(position).selected);
                    mPhotoList.get(position).selected = !mPhotoList.get(position).selected;
                }
            });
        } else {
            if (position == 0) {
                holder.mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                holder.mImageView.setImageResource(R.drawable.ic_menu_camera);
                holder.checkBox.setVisibility(View.INVISIBLE);
                holder.mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnclickPhotoListener != null) {
                            mOnclickPhotoListener.onClick();
                        }
                    }
                });
            } else {
                Glide.with(mContext)
                        .load(mPhotoList.get(position - 1).path)
                        .centerCrop()
                        .placeholder(R.drawable.loading_spinner)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(holder.mImageView);
                holder.mImageView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String path = mPhotoList.get(position - 1).path;
                        Intent it = new Intent();
                        it.putExtra(FILE_PATH, path);
                        mContext.setResult(Activity.RESULT_OK, it);
                        mContext.finish();
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if (moreSel) {
            return mPhotoList.size();
        }
        return mPhotoList.size() + 1;
    }

    public interface OnclickPhotoListener {
        void onClick();
    }

    public static class PicSelInfoPage {
        String path;
        boolean selected;

        public static List<PicSelInfoPage> createFromString(List<String> strings) {
            List<PicSelInfoPage> list = new ArrayList<>();
            for (String str : strings) {
                PicSelInfoPage page = new PicSelInfoPage();
                page.path = str;
                page.selected = false;
                list.add(page);
            }
            return list;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        private CheckBox checkBox;

        public ViewHolder(View v) {
            super(v);
            checkBox = (CheckBox) v.findViewById(R.id.checkbox);
            mImageView = (ImageView) v.findViewById(R.id.image_view);
        }
    }
}

