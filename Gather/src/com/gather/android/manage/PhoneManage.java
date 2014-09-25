package com.gather.android.manage;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;

public class PhoneManage {

	/**
	 * sd卡是否插入
	 * 
	 * @return
	 */
	public static boolean isSdCardExit() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取SD卡根目录路径
	 * 
	 * @return
	 */
	public static String getSdCardRootPath() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	/**
	 * 获取app安装根目录
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppRootPath(Context context) {
		return "/data/data/" + context.getPackageName();
	}

	/**
	 * 将dip转换成px
	 * 
	 * @param context
	 * @param density
	 *            像素密度
	 * @return
	 */
	public static int dip2px(int dip, float density) {
		return (int) (density * dip + 0.5f);
	}

	/**
	 * 获取屏幕信息
	 * 
	 * @param activity
	 * @return
	 */
	public static DisplayMetrics getDisplayMetrics(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics;
	}

	public static int getAvailableSize(String rootPath) {
		StatFs inStatFs = new StatFs(rootPath);
		long blockSize = inStatFs.getBlockSize();
		long availaBlocks = inStatFs.getAvailableBlocks();
		int availaSize = (int) (availaBlocks * blockSize / 1024 / 1024);
		return availaSize;
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
}
