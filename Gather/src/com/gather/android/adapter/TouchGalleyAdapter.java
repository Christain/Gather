package com.gather.android.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;

import com.gather.android.R;
import com.gather.android.widget.touchgallery.BasePagerAdapter;
import com.gather.android.widget.touchgallery.GalleryViewPager;
import com.gather.android.widget.touchgallery.UrlTouchImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;


public class TouchGalleyAdapter extends BasePagerAdapter {
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	public TouchGalleyAdapter(Context context, List<String> resources) {
		super(context, resources);
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.default_image)
		.showImageForEmptyUri(R.drawable.default_image)
		.showImageOnFail(R.drawable.default_image)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.displayer(new FadeInBitmapDisplayer(100))
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
		((GalleryViewPager) container).mCurrentView = ((UrlTouchImageView) object).getImageView();
	}

	@Override
	public Object instantiateItem(ViewGroup collection, int position) {
		final UrlTouchImageView iv = new UrlTouchImageView(mContext, imageLoader, options);
		iv.setUrl(mResources.get(position));
		iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		collection.addView(iv, 0);
		return iv;
	}
}
