package com.sfzt.zlauncher_wf;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class ReversalLinearLayout extends LinearLayout implements AnimationListener {
	
	//private Animation anifont;
	private Animation aniback;
	
	private boolean isAnimation = false;
	
	public ReversalLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		aniback = AnimationUtils.loadAnimation(context, R.anim.back);
		aniback.setAnimationListener(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				if(!isAnimation){
					isAnimation = true;
					startAnimation(aniback);
				}
				break;
			case MotionEvent.ACTION_UP:
				isAnimation = false;
				break;
		}
		return true;
	}
	
	private Handler mHandler = new Handler();
	
	private Runnable mClickRunnable = new Runnable() {
		@Override
		public void run() {
			if(isAnimation){
				performLongClick();
			}else{
				performClick();
			}
			isAnimation = false;
		}
	};
	
	@Override
	public void onAnimationEnd(Animation animation) {
		mHandler.post(mClickRunnable);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}
	
}
