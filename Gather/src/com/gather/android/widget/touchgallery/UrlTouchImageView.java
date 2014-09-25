package com.gather.android.widget.touchgallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class UrlTouchImageView extends RelativeLayout {
	
	protected ProgressBar mProgressBar;
	protected TouchImageView mImageView;
	protected ImageLoader imageLoader;
	protected DisplayImageOptions options;

	protected Context mContext;

	public UrlTouchImageView(Context ctx, ImageLoader imageLoader, DisplayImageOptions options) {
		super(ctx);
		mContext = ctx;
		this.imageLoader = imageLoader;
		this.options = options;
		init();

	}

	public UrlTouchImageView(Context ctx, ImageLoader imageLoader, DisplayImageOptions options, AttributeSet attrs) {
		super(ctx, attrs);
		mContext = ctx;
		init();
	}

	public TouchImageView getImageView() {
		return mImageView;
	}

	protected void init() {
		mImageView = new TouchImageView(mContext);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mImageView.setLayoutParams(params);
		this.addView(mImageView);
		mImageView.setVisibility(GONE);

		mProgressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleHorizontal);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.setMargins(30, 0, 30, 0);
		mProgressBar.setLayoutParams(params);
		mProgressBar.setIndeterminate(false);
		mProgressBar.setMax(100);
		this.addView(mProgressBar);
	}

	public void setUrl(String imageUrl) {
		imageLoader.displayImage(imageUrl, mImageView, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				mProgressBar.setProgress(0);
				mProgressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				mProgressBar.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				mProgressBar.setVisibility(View.GONE);
				mImageView.setVisibility(VISIBLE);
			}
		}, new ImageLoadingProgressListener() {
			@Override
			public void onProgressUpdate(String imageUri, View view, int current, int total) {
				mProgressBar.setProgress((current / total)*100);
			}
		});
	}
}
