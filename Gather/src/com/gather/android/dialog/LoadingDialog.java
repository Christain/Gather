package com.gather.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;

public class LoadingDialog extends Dialog {

	private static Context contexts;
	private static OnDismissListener listener;

	public LoadingDialog(Context context) {
		super(context);
	}

	public LoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	public static LoadingDialog createDialog(Context context, boolean cancel) {
		contexts = context;
		LoadingDialog loadingDialog = new LoadingDialog(context, R.style.LoadingDialogTheme);
		loadingDialog.setContentView(R.layout.dialog_loading);
		loadingDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		WindowManager.LayoutParams lp = loadingDialog.getWindow().getAttributes();
		loadingDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		loadingDialog.getWindow().setAttributes(lp);
		lp.alpha = 1.0f;// 透明度，1是不透明
		lp.dimAmount = 0.3f;// 黑暗度
		loadingDialog.setCanceledOnTouchOutside(false);
		
		loadingDialog.setOnDismissListener(new Dialog.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				if (listener != null) {
					listener.OnDismiss();
				}
			}
		});

		if (!cancel) {
			loadingDialog.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
						return true;
					} else {
						return false;
					}
				}
			});
		} 
		return loadingDialog;
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		ImageView imageView = (ImageView) findViewById(R.id.ivLoading);
		Animation animation = AnimationUtils.loadAnimation(contexts, R.anim.anim_loading_dialog);
		LinearInterpolator lir = new LinearInterpolator();
		animation.setInterpolator(lir);
		imageView.startAnimation(animation);
	}

	public void setMessage(String tips) {
		TextView tvTips = (TextView) findViewById(R.id.tvMessage);
		tvTips.setText(tips);
	}
	
	public interface OnDismissListener {
		public void OnDismiss();
	}
	
	public void setDismissListener(OnDismissListener listener) {
		this.listener = listener;
	}
	
}
