package com.gather.android.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.view.View;

import com.gather.android.constant.Constant;

public class BitmapUtils {
	/**
	 * 获取圆角bitmap
	 * 
	 * @param orgBmp
	 *            原始bitmap
	 * @param cornerRadius
	 *            圆角角度(50.0f比较合适)
	 * @return
	 */
	public static Bitmap getRoundCornerBitmap(Bitmap orgBmp, float cornerRadius) {
		if (orgBmp != null) {
			int w = orgBmp.getWidth();
			int h = orgBmp.getHeight();
			Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			Rect rect = new Rect(0, 0, w, h);
			RectF rectF = new RectF(rect);
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(orgBmp, null, rect, paint);
			return bitmap;
		}
		return null;
	}

	public static Bitmap getBitmapFromUri(Context context, Uri uri) {
		try {
			// 读取uri所在的图片
			// Bitmap bitmap =
			// MediaStore.Images.Media.getBitmap(context.getContentResolver(),
			// uri);
			Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
			return bitmap;
		} catch (Exception e) {
//			Log.e("[Android]", e.getMessage());
//			Log.e("[Android]", "目录为：" + uri);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取圆形bitmap
	 * 
	 * @param orgBmp原始bitmap
	 * @return
	 */
	public static Bitmap getRoundBitmap(Bitmap orgBmp) {
		if (orgBmp != null) {
			int w = orgBmp.getWidth();
			int h = orgBmp.getHeight();
			Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			Rect rect = new Rect(0, 0, w, h);
			RectF rectf = new RectF(rect);
			canvas.drawOval(rectf, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(orgBmp, null, rect, paint);
			return bitmap;
		}
		return null;
	}

	/**
	 * 获取assets中的图片biamap
	 * 
	 * @param c
	 * @param imageName
	 * @return
	 */
	public static Bitmap getAssetImageBitmap(Context c, String imageName) {
		try {
			AssetManager manager = c.getResources().getAssets();
			InputStream is = manager.open(imageName);
			Bitmap bmp = BitmapFactory.decodeStream(is);
			is.close();
			return bmp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将bitmap保存为本地图片
	 * 
	 * @param context
	 * @param format
	 * @param orgBmp
	 * @param file
	 * @param quality
	 * @return
	 */
	public static boolean saveBitmapToImageFile(Context context, Bitmap.CompressFormat format, Bitmap orgBmp, File file, int quality) {
		try {
			FileOutputStream outputStream = new FileOutputStream(file);
			orgBmp.compress(format, quality, outputStream);
			outputStream.flush();
			outputStream.close();
			file.setLastModified(System.currentTimeMillis());
			context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getPath())));
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 从view 得到图片
	 * 
	 * @param view
	 * @return
	 */
	public static Bitmap getBitmapFromView(View view) {
		view.destroyDrawingCache();
		view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = view.getDrawingCache(true);
		return bitmap;
	}

	/**
	 * 截屏（不包括状态栏）
	 * 
	 * @param activity
	 * @return
	 */
	public static Bitmap takeScreenShot(Activity activity, int delHigh) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();
		int statusBarHeight = getStatusBarHigh(activity);
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay().getHeight();
		// 去掉标题栏
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight + delHigh, width, height - statusBarHeight - delHigh);
		view.destroyDrawingCache();
		return b;
	}
	
	public static Bitmap takeScreenShot(Activity activity, int delHigh, int delBottom) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();
		int statusBarHeight = getStatusBarHigh(activity);
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay().getHeight();
		// 去掉标题栏
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight + delHigh, width, height - statusBarHeight - delHigh - delBottom);
		view.destroyDrawingCache();
		return b;
	}

	public static int getStatusBarHigh(Context context) { // 获取状态栏高度
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = context.getResources().getDimensionPixelSize(x);
			return sbar;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return 0;
	}

	/**
	 * 将图片文件转换成bitmap
	 * 
	 * @param imgFile
	 * @param maxNumOfPixels
	 * @return
	 */
	public static Bitmap getBitmapFromFile(File imgFile, int maxNumOfPixels) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(imgFile.getAbsolutePath(), opts);
			opts.inJustDecodeBounds = false;
			if (maxNumOfPixels <= 0) {
				opts.inSampleSize = computeSampleSize(opts, 1000 * 1000);
			} else {
				opts.inSampleSize = computeSampleSize(opts, maxNumOfPixels);
			}
			bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), opts);
		} catch (Exception e) {
			e.printStackTrace();
			bitmap = null;
		}
		return bitmap;
	}

	public static int computeSampleSize(BitmapFactory.Options options, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, -1, maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
}
