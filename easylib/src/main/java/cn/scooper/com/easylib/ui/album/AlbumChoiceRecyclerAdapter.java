package cn.scooper.com.easylib.ui.album;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.scooper.com.easylib.R;

/**
 * Created by Aguai on 2016/11/19.
 * 自定义相册文件夹适配器
 */

class AlbumChoiceRecyclerAdapter extends RecyclerView.Adapter<AlbumChoiceRecyclerAdapter.ViewHolder> {
    private List<ImageBean> mPhotoList = new ArrayList<>();
    private Context mContext;
    private ItemOnclickListener mItemOnclickListener;

    public AlbumChoiceRecyclerAdapter(Context context, List<ImageBean> photoList) {
        mContext = context;
        if (photoList != null) {
            mPhotoList = photoList;
        }
    }

    public void setOnItemClickListener(ItemOnclickListener listener) {
        this.mItemOnclickListener = listener;
    }

    @Override
    public AlbumChoiceRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_choice_recycler_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ImageBean imageBean = mPhotoList.get(position);
        Glide.with(mContext)
                .load(imageBean.getTopImagePath())
                .centerCrop()
                .crossFade()
                .into(holder.mCoverImage);
        holder.mAlbumName.setText(imageBean.getFolderName());
        holder.mPhotoNum.setText(String.format(mContext.getResources().getString(R.string.photo_num), imageBean.getImageCounts()));
        holder.mClickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemOnclickListener.onItemClick(mPhotoList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }

    public interface ItemOnclickListener {
        void onItemClick(ImageBean imageBean);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mCoverImage;
        public TextView mAlbumName;
        public TextView mPhotoNum;
        public RelativeLayout mClickBtn;

        public ViewHolder(View v) {
            super(v);
            mCoverImage = (ImageView) v.findViewById(R.id.the_cover);
            mAlbumName = (TextView) v.findViewById(R.id.album_name);
            mPhotoNum = (TextView) v.findViewById(R.id.photo_num);
            mClickBtn = (RelativeLayout) v.findViewById(R.id.click_btn);
        }
    }
}
