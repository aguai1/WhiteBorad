package cn.scooper.com.easylib.ui.album;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.scooper.com.easylib.R;
import cn.scooper.com.easylib.WidgetStartHelper;
import cn.scooper.com.easylib.ui.BaseActivity;
import cn.scooper.com.easylib.utils.PermissionsUtil;
import cn.scooper.com.easylib.utils.ToastUtils;

import static cn.scooper.com.easylib.utils.PermissionsUtil.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;

/**
 * Created by Aguai on 2016/11/19.
 * 自定义相册activity
 */
public class PhotoAlbumActivity extends BaseActivity {

    private final static int SCAN_OK = 1;
    //大图遍历字段
    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.ORIENTATION
    };
    private LinkedHashMap<String, List<String>> mGroupMap = new LinkedHashMap<>();
    private RecyclerView mPhotoRecyclerView;
    private RecyclerView mAlbumChoiceRecyclerView;
    private LinearLayout mChoicePhotoAlbumBtn;
    private TextView mPhotoAlbumName;
    private Animation mAlbumChoiceIn;
    private Animation mAlbumChoiceOut;
    private TextView ok;
    private AlbumChoiceRecyclerAdapter albumChoiceRecyclerAdapter;
    private PhotoAlbumRecyclerAdapter mPhotoAlbumRecyclerAdapter;
    private ImageView arrow;
    private boolean moreSel;
    private List<String> strings;
    private TextView cancelBtn;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN_OK:
                    initDate();
                    break;
            }
        }

    };

    @Override
    public int bindLayout() {
        return R.layout.activity_photo_album_layout;
    }

    @Override
    public boolean translucentStatus() {
        return false;
    }

    @Override
    public void initView(View view) {
        ok = (TextView) findViewById(R.id.btn_ok);
        mChoicePhotoAlbumBtn = (LinearLayout) findViewById(R.id.change_photo_album_btn);
        mPhotoAlbumName = (TextView) findViewById(R.id.photo_album_name);
        mPhotoRecyclerView = (RecyclerView) findViewById(R.id.photo_recyclerView);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAlbumChoiceRecyclerView = (RecyclerView) findViewById(R.id.album_choice_recyclerView);
        mAlbumChoiceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrow = (ImageView) findViewById(R.id.arrow);
        cancelBtn = (TextView) findViewById(R.id.cancel);
        assert cancelBtn != null;
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        if (checkPermissionREAD_EXTERNAL_STORAGE(PhotoAlbumActivity.this)) {
            getImages();
        }

        moreSel = getIntent().getBooleanExtra("moreSel", false);
        if (moreSel) {
            ok.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.INVISIBLE);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String path = mPhotoAlbumRecyclerAdapter.getSelectedPath();
                    Intent it = new Intent();
                    it.putExtra(PhotoAlbumRecyclerAdapter.FILE_PATH, path);
                    setResult(Activity.RESULT_OK, it);
                    finish();
                }
            });
        }
    }

    private void initDate() {
        initAnim();
        strings = mGroupMap.get(getString(R.string.all_photo));
        mPhotoAlbumRecyclerAdapter = new PhotoAlbumRecyclerAdapter(PhotoAlbumActivity.this, strings, moreSel);
        mPhotoAlbumRecyclerAdapter.setOnclickPhotoListener(new PhotoAlbumRecyclerAdapter.OnclickPhotoListener() {
            @Override
            public void onClick() {
                photoImage();
            }
        });
        mPhotoRecyclerView.setAdapter(mPhotoAlbumRecyclerAdapter);
        mPhotoRecyclerView.setItemAnimator(new DefaultItemAnimator());
        albumChoiceRecyclerAdapter = new AlbumChoiceRecyclerAdapter(PhotoAlbumActivity.this, subGroupOfImage(mGroupMap));
        albumChoiceRecyclerAdapter.setOnItemClickListener(new AlbumChoiceRecyclerAdapter.ItemOnclickListener() {
            @Override
            public void onItemClick(ImageBean imageBean) {
                String key = imageBean.getFolderName();
                mPhotoAlbumName.setText(key);
                List<String> pathList = mGroupMap.get(key);
                mPhotoAlbumRecyclerAdapter.setPhotoList(pathList);
                mPhotoAlbumRecyclerAdapter.notifyDataSetChanged();
                mAlbumChoiceRecyclerView.startAnimation(mAlbumChoiceOut);
            }
        });
        mAlbumChoiceRecyclerView.setAdapter(albumChoiceRecyclerAdapter);
        mChoicePhotoAlbumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAlbumChoiceRecyclerView.getVisibility() == View.INVISIBLE) {
                    mAlbumChoiceRecyclerView.startAnimation(mAlbumChoiceIn);
                } else {
                    mAlbumChoiceRecyclerView.startAnimation(mAlbumChoiceOut);
                }
            }
        });
    }

    private void initAnim() {
        mAlbumChoiceIn = AnimationUtils.loadAnimation(this, R.anim.anim_album_choice_in);
        mAlbumChoiceOut = AnimationUtils.loadAnimation(this, R.anim.anim_album_choice_out);
        mAlbumChoiceIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mAlbumChoiceRecyclerView.setVisibility(View.VISIBLE);
                arrow.setImageResource(R.drawable.arrow_above);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mAlbumChoiceOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                arrow.setImageResource(R.drawable.arrow_below);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mAlbumChoiceRecyclerView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    PermissionsUtil.showDialog("External storage", this,
                            Manifest.permission.READ_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                } else {
                    PermissionsUtil.getReadExternalStoragePermissions((Activity) context);
                }
                return false;
            } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        100);
            } else {
                return true;
            }
        }
        return true;
    }


    private void getImages() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                //获取大图的游标
                Cursor cursor = getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  // 大图URI
                        STORE_IMAGES,   // 字段
                        null,         // No where clause
                        null,         // No where clause
                        MediaStore.Images.Media.DATE_TAKEN + " DESC"); //根据时间升序

                if (cursor == null) {
                    return;
                }

                List<String> appPathList = new ArrayList<>();
                while (cursor.moveToNext()) {
                    String path = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

                    String parentName = new File(path).getParentFile().getName();

                    appPathList.add(path);
                    if (!mGroupMap.containsKey(parentName)) {
                        List<String> chileList = new ArrayList<>();
                        chileList.add(path);
                        mGroupMap.put(parentName, chileList);
                    } else {
                        mGroupMap.get(parentName).add(path);
                    }
                }
                mGroupMap.put(getString(R.string.all_photo), appPathList);

                mHandler.sendEmptyMessage(SCAN_OK);
                cursor.close();
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getImages();
                } else {
                    ToastUtils.showShort("GET_ACCOUNTS Denied");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    private List<ImageBean> subGroupOfImage(LinkedHashMap<String, List<String>> mGroupMap) {
        if (mGroupMap.size() == 0) {
            return new ArrayList<>();
        }
        List<ImageBean> list = new ArrayList<ImageBean>();

        Iterator<Map.Entry<String, List<String>>> it = mGroupMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<String>> entry = it.next();
            ImageBean mImageBean = new ImageBean();
            String key = entry.getKey();
            List<String> value = entry.getValue();
            if (value.size() > 0) {
                mImageBean.setFolderName(key);
                mImageBean.setImageCounts(value.size());
                mImageBean.setTopImagePath(value.get(0));

                list.add(mImageBean);
            }
        }

        Collections.reverse(list);

        return list;

    }

    private void photoImage() {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/temp_camera.jpg");
        WidgetStartHelper.takePhoto(this, Uri.fromFile(file), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 0) {
            Intent it = new Intent();
            it.putExtra("FILE_PATH", Environment.getExternalStorageDirectory().getPath() + "/temp_camera.jpg");
            setResult(Activity.RESULT_OK, it);
            finish();
        }
    }


}
