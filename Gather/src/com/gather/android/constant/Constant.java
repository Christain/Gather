package com.gather.android.constant;

import java.io.File;

import com.gather.android.manage.PhoneManage;

public final class Constant {
	
	public static final String DEFOULT_REQUEST_URL = "http://192.168.0.155:9999/gathersrv/index.php?r=";// 外网数据访问地址	
	public static final String DEFOULT_PIC_URL = "http://serverimage.yo-yo.so"; //外网图片	
	public static final boolean SHOW_LOG = true;// 显示log信息
	
	public static final String TENCENT_APPID = "1102364598"; //QQ登录ID
	public static final String SINA_APPID = "2247106580";	//新浪登录ID	
	public static final String SINA_CALLBACK_URL = "http://www.yo-yo.so"; //新浪回调地址
	public static final String SINA_SCOPE = "email,direct_messages_read,direct_messages_write"; //新浪授权权限
	
	public static final String IMAGE_CACHE_DIR_PATH = PhoneManage.getSdCardRootPath() + "/Gather/cache/images/";// 图片缓存地址
	public static final String APP_IMAGE_CACHE_DIR_PATH = PhoneManage.getSdCardRootPath() + "/Gather/cache/app/";// 系统图片缓存地址
	public static final String EXCEPTION_LOG_DIR_PATH = PhoneManage.getSdCardRootPath() + "/Gather/log/";// 报告文件存放地址
	public static final String UPLOAD_FILES_DIR_PATH = PhoneManage.getSdCardRootPath() + "/Gather/upload/";
	public static final String DOWNLOAD_IMAGE_DIR_PATH = PhoneManage.getSdCardRootPath() + "/Gather/imagedown/";
	
	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}
	
	public static void checkPath() {
		File dir = new File(IMAGE_CACHE_DIR_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		dir = new File(APP_IMAGE_CACHE_DIR_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		dir = new File(UPLOAD_FILES_DIR_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		dir = new File(DOWNLOAD_IMAGE_DIR_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		dir = new File(EXCEPTION_LOG_DIR_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

}
