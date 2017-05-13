package cn.scooper.com.easylib.ui.image;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import cn.scooper.com.easylib.R;
import cn.scooper.com.easylib.ui.BaseActivity;
import cn.scooper.com.easylib.utils.BitmapUtils;
import cn.scooper.com.easylib.utils.LogUtil;
import cn.scooper.com.easylib.utils.SpringInterpolator;
import cn.scooper.com.easylib.utils.StorageUtil;
import cn.scooper.com.easylib.views.image.TouchImageView;

/**
 * Created by Aguai on 2016/11/19.
 * 图片查看器，增加放大动画
 */
public class ImageDetailActivity extends BaseActivity {

    private static ImageView bgView;
    private TouchImageView mImageDetail;
    private FrameLayout mBaseLayout;
    private String mImageBigUrl;

    private int mItemHeight;
    private int mItemWidth;
    private int mItemLocationX;
    private int mItemLocationY;
    private Bitmap mBitmap;
    private View.OnTouchListener mInterceptTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    };

    public static void setStartView(ImageView view) {
        bgView = view;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_image_detail_layout;
    }

    @Override
    public boolean translucentStatus() {
        return false;
    }

    @Override
    public void initView(View view) {
        mBaseLayout = (FrameLayout) findViewById(R.id.bg_layout);
        mImageDetail = (TouchImageView) findViewById(R.id.image_detail);
    }

    @Override
    public void doBusiness(Context mContext) {
        Intent intent = getIntent();
        mImageBigUrl = intent.getStringExtra("image_url");
        mItemWidth = intent.getIntExtra("width", 0);
        mItemHeight = intent.getIntExtra("height", 0);
        mItemLocationY = intent.getIntExtra("y", 0);
        mItemLocationX = intent.getIntExtra("x", 0);
        Glide.with(this).load(mImageBigUrl)
                .asBitmap()
                .fitCenter()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        mImageDetail.setImageBitmap(resource);
                        mBitmap = resource;
                    }
                });
        Glide.with(this).load(mImageBigUrl).into(mImageDetail);
        beginScaleAnimation();
    }

    /**
     * 开启位移,放大动画
     */
    private void beginScaleAnimation() {
        if (mItemWidth != -1 && mItemHeight != -1) {
            mBaseLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mBaseLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                    try {
                        if (bgView != null) {

                            mImageDetail.setImageDrawable(bgView.getDrawable());
                            mBitmap = BitmapUtils.loadBitmapFromView(bgView);
                        }
                    } catch (Exception e) {
                        LogUtil.e("ImageDetail", e.fillInStackTrace().toString());
                    }
                    int parentWidth = mBaseLayout.getWidth();
                    int parentHeight = mBaseLayout.getHeight();

                    int[] parentLocations = new int[2];
                    mBaseLayout.getLocationOnScreen(parentLocations);
                    float scaleX = mItemWidth / (float) parentWidth;
                    float scaleY = mItemHeight / (float) parentHeight;
                    int translationX = parentLocations[0] - mItemLocationX + (parentWidth - mItemWidth) / 2;
                    int translationY = parentLocations[1] - mItemLocationY + (parentHeight - mItemHeight) / 2;
                    mBaseLayout.setScaleX(scaleX);
                    mBaseLayout.setScaleY(scaleY);
                    mBaseLayout.setTranslationX(-translationX);
                    mBaseLayout.setTranslationY(-translationY);


                    mBaseLayout.animate()
                            .scaleX(1)
                            .scaleY(1)
                            .translationX(0)
                            .translationY(0)
                            .setInterpolator(new SpringInterpolator(1.2f))
                            .setDuration(400)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    mImageDetail.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dismissSelf();
                                        }
                                    });
                                    mImageDetail.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                                        @Override
                                        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                                            menu.add("保存图片").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                                @Override
                                                public boolean onMenuItemClick(MenuItem item) {
                                                    StorageUtil.saveImage(ImageDetailActivity.this, mBitmap);
                                                    return false;
                                                }
                                            });
                                            menu.add("取消").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                                @Override
                                                public boolean onMenuItemClick(MenuItem item) {

                                                    return false;
                                                }
                                            });
                                        }
                                    });
                                    //清除触屏拦截监听器
                                    mBaseLayout.setOnTouchListener(null);
                                }
                            })
                            .start();

                    return true;
                }
            });
        }
    }

    /**
     * fragment 退出动画
     */
    public void dismissSelf() {

        if (bgView != null) {
            bgView.setVisibility(View.INVISIBLE);
            Bitmap bitmap = BitmapUtils.loadBitmapFromView(bgView);
            mBaseLayout.removeAllViews();
            mBaseLayout.setBackground(new BitmapDrawable(bitmap));
        }
        int parentWidth = mBaseLayout.getWidth();
        int parentHeight = mBaseLayout.getHeight();
        int[] parentLocations = new int[2];
        mBaseLayout.getLocationOnScreen(parentLocations);
        //计算scale动画参数
        float scaleX = mItemWidth / (float) parentWidth;
        float scaleY = mItemHeight / (float) parentHeight;
        //计算translation动画参数,以View的中心点作为参考点
        int translationX = parentLocations[0] - mItemLocationX + (parentWidth - mItemWidth) / 2;
        int translationY = parentLocations[1] - mItemLocationY + (parentHeight - mItemHeight) / 2;

        //设置触屏事件拦截
        mBaseLayout.setOnTouchListener(mInterceptTouchListener);
        //fragment主布局缩放,位移动画
        mBaseLayout.animate()
                .scaleX(scaleX)
                .scaleY(scaleY)
                .translationX(-translationX)
                .translationY(-translationY)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (bgView != null) {
                            bgView.setVisibility(View.VISIBLE);
                        }
                        finish();
                        overridePendingTransition(0, 0);

                    }
                })
                .setInterpolator(new SpringInterpolator(1.2f))
                .setDuration(400)
                .start();

    }

    @Override
    public void onBackPressed() {
        dismissSelf();
    }
}
