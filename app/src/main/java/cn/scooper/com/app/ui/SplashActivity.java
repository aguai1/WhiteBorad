package cn.scooper.com.app.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import cn.scooper.com.easylib.ui.BaseActivity;
import cn.scooper.com.easylib.utils.SharedPreferencesHelper;
import cn.scooper.com.whiteboard.R;

public class SplashActivity extends BaseActivity {
	@Override
	public int bindLayout() {
		return R.layout.activity_welcome;
	}

	@Override
	public boolean translucentStatus() {
		return true;
	}


	@Override
	public void initView(View view) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent;
				if(SharedPreferencesHelper.getInstance().getIsFirstIn()){
					intent=new Intent(SplashActivity.this,LoginActivity.class);
				}else{
					intent = new Intent(SplashActivity.this, CreateMeetingActivity.class);
				}
				startActivity(intent);
				finish();
			}
		},2000);
	}

	@Override
	public void doBusiness(Context mContext) {

	}

	@Override
	public void onBackPressed() {

	}
}
