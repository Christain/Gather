package com.gather.android.manage;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

public class IntentManage {
	public static final int REQUEST_CODE_ALBUM = 0x1; // 系统相册
	public static final int REQUEST_CODE_TAKE_PHOTO = 0x2;// 拍照
	public static final int REQUEST_CODE_PREVIEW_IMAGE = 0x5;//图片预览
	public static final int REQUEST_CODE_CROP_IMAGE = 0x3;// 裁剪图片
	public static final int REQUEST_CODE_SHUIYIN_IMAGE = 0x4; // 水印相机
	

	/**
	 * 相册小图
	 * 
	 * @return
	 */
	public static Intent getSystemAlbumIntent() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		return intent;
	}
	
	/**
	 * 相册大图
	 * 
	 * @return
	 */
	public static Intent getSystemAlbumIntent(File imgFile) {
		Intent intent = new Intent(Intent.ACTION_PICK);
		Uri imgUri = Uri.fromFile(imgFile);
		intent.setDataAndType(imgUri,"image/*");
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imgUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("return-data", false);
		return intent;
	}

	/**
	 * 拍照
	 * 
	 * @param imgFile
	 *            照片文件
	 * @return
	 */
	public static Intent getSystemCameraIntent(File imgFile) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri mImageCaptureUri = Uri.fromFile(imgFile);
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
//		intent.putExtra("return-data", false);
		return intent;
	}

	public static Intent getSystemCropIntent(int aspectX, int aspectY, int outputX, int outputY, Intent data, File file) {
		return getSystemCropIntent(aspectX, aspectY, outputX, outputY, data.getData(), file);
	}

	public static Intent getSystemCropIntent(int aspectX, int aspectY, int outputX, int outputY, Uri uri, File file) {
		Intent cropIntent = new Intent("com.android.camera.action.CROP");
		// 图片来源
		cropIntent.setDataAndType(uri, "image/*");
		// 设置剪裁剪属性
		cropIntent.putExtra("crop", "true");
		cropIntent.putExtra("aspectX", 1);
		cropIntent.putExtra("aspectY", 1);
		// 输出的坐标
		cropIntent.putExtra("outputX", 160);
		cropIntent.putExtra("outputY", 160);
		// 返回剪裁的图片数据
		cropIntent.putExtra("return-data", false);
		cropIntent.putExtra("output", Uri.fromFile(file));
		return cropIntent;
	}
}
