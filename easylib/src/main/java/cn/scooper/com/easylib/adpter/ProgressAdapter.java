package cn.scooper.com.easylib.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.scooper.com.easylib.R;

/**
 * Created by Aguai on 2016/11/20.
 * RecyclerView分页加载适配器
 * setFixed(ture)   加载完成
 * setLoading(ture)  显示正在加载;false 则不显示。
 * 请不要重写getCount方法
 */
public abstract class ProgressAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_PROGRESS = 1;
    public static final int VIEW_TYPE_NORMAL = 2;
    private boolean mIsLoading = false;
    private boolean mFixed = false;
    private RecyclerView mTarget;
    private List<T> mItems;
    private List<T> mInnerItems;
    private Context mContext;
    /**
     * 当前Items个数等于hitCount时触发加载更多事件
     * 默认值为3
     */
    private int mHitCount = 3;
    private OnLoadMoreListener mListener;

    public ProgressAdapter(Context context, List<T> list, RecyclerView target) {
        mContext = context;
        mItems = list;
        mTarget = target;
        addItems(mItems);
    }

    private void addItems(List<T> items) {
        if (items != null) {
            if (mInnerItems == null) {
                mInnerItems = new ArrayList<>();
            }
            if (mInnerItems.size() > 0) {
                removeEmptyItem();
            }
            mInnerItems.addAll(items);
            if (mInnerItems.size() > 0) {
                addEmptyItem();
            }

        }
    }

    private void clearItems() {
        if (mInnerItems != null) {
            mInnerItems.clear();
        }
    }

    private void resetItems(List<T> items) {
        if (items != null) {
            clearItems();
            addItems(items);
        }
    }

    private void addEmptyItem() {
        if (mInnerItems != null) {
            mInnerItems.add(null);
        }
    }

    private void removeEmptyItem() {
        if (mInnerItems != null) {
            mInnerItems.remove(null);
        }
    }

    /**
     * 通知adapter's dataSet changed, do not call notifyDataSetChanged,call this method instead
     */
    public void notifyDataChanged() {
        mIsLoading = false;
        if (mItems != null && mInnerItems != null) {
            resetItems(mItems);
            notifyDataSetChanged();
        }
    }

    private void loading(boolean loading) {
        if (mIsLoading != loading) {
            mIsLoading = loading;
            dispatchLoadingEvent();
        }
    }

    private void dispatchLoadingEvent() {
        if (mIsLoading) {
            if (mListener != null) {
                mListener.onLoadMore();
            }
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mListener = listener;
    }

    public void setHitCount(int hitCount) {
        mHitCount = hitCount;
    }

    /**
     * 是否需要再调用加载更多事件
     *
     * @param fixed true if onLoadMore is no longer needed
     */
    public void setFixed(boolean fixed) {
        mFixed = fixed;
    }

    /**
     * 加载结束时应该调用setLoading(false),通知adapter状态改变
     *
     * @param loading 加载状态
     */
    public void setLoading(boolean loading) {
        loading(loading);
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public int getItemViewType(int position) {
        return position == mInnerItems.size() - 1 ? VIEW_TYPE_PROGRESS : VIEW_TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_NORMAL) {
            return onCreateItemViewHolder(parent, viewType);
        } else if (viewType == VIEW_TYPE_PROGRESS) {
            return onCreateFooterViewHolder(parent, viewType);
        } else {
            throw new IllegalStateException("viewType can only be VIEW_TYPE_NORMAL or VIEW_TYPE_PROGRESS");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position + mHitCount >= getItemCount()
                && mHitCount < mItems.size()
                && mListener != null) {
            if (!mIsLoading && !mFixed) {
                loading(true);
            }
        }
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_NORMAL) {
            onBindItemViewHolder(holder, position);
        } else if (viewType == VIEW_TYPE_PROGRESS) {
            onBindFooterViewHolder(holder, position);
        } else {
            throw new IllegalStateException("viewType can only be VIEW_TYPE_NORMAL or VIEW_TYPE_PROGRESS");
        }
    }

    @Override
    public int getItemCount() {
        return mInnerItems.size();
    }

    public abstract RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position);

    private ProgressViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        if (viewType != VIEW_TYPE_PROGRESS) {
            throw new IllegalStateException("can not create footer view holder when viewType is not VIEW_TYPE_PROGRESS");
        }
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.frame_progress, parent, false);
        return new ProgressViewHolder(view);
    }

    private void onBindFooterViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType != VIEW_TYPE_PROGRESS) {
            throw new IllegalStateException("can not bind footer view holder when viewType is not VIEW_TYPE_PROGRESS");
        }
        ProgressViewHolder pHolder = (ProgressViewHolder) holder;
        if (mIsLoading && !mFixed) {
            pHolder.doneHint.setVisibility(View.GONE);
            pHolder.progressBar.setVisibility(View.VISIBLE);
        } else {
            pHolder.doneHint.setVisibility(View.VISIBLE);
            pHolder.progressBar.setVisibility(View.GONE);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        TextView doneHint;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loading);
            doneHint = (TextView) itemView.findViewById(R.id.done_hint);

        }
    }
}
