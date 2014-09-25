package com.gather.android.manage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.gather.android.constant.Constant;

public class ExceptionManage implements UncaughtExceptionHandler {
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private static ExceptionManage instance;
	private Application application;

	public static void startInstance(Application app) {
		if (instance == null) {
			synchronized (ExceptionManage.class) {
				if (instance == null) {
					instance = new ExceptionManage();
				}
				instance.init(app);
			}
		}
	}

	public void init(Application app) {
		application = app;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else { // 如果自己处理了异常，则不会弹出错误对话框，则需要手动退出app
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			AppManage.getInstance().clearEverything(application.getApplicationContext());
//			Intent intent = new Intent();
//			intent.setClass(application.getApplicationContext(), StartActivity.class);
//			Intent intent = application.getApplicationContext().getPackageManager().getLaunchIntentForPackage(application.getApplicationContext().getPackageName());
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			application.getApplicationContext().startActivity(intent);
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
	 * 
	 * @return true代表处理该异常，不再向上抛异常，
	 *         false代表不处理该异常(可以将该log信息存储起来)然后交给上层(这里就到了系统的异常处理)去处理，
	 *         简单来说就是true不会弹出那个错误提示框，false就会弹出
	 */

	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return false;
		}

		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Context context = application.getApplicationContext();
				saveExceptionLog(context, ex);
				Looper.prepare();
				Toast.makeText(context, "运行异常", Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}.start();
		return true;
	}

	private void saveExceptionLog(Context context, Throwable ex) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String logInfo = writer.toString();
		if (!Constant.SHOW_LOG) {
			return;
		} else {
			Log.e("ERROR_MSG", logInfo);
		}
		// if (PhoneManage.isSdCardExit()) {
		// File dir = new File(Constant.EXCEPTION_LOG_DIR_PATH);
		// if (!dir.exists()) {
		// dir.mkdirs();
		// }
		// StringBuilder sb = new StringBuilder();
		// String date = TimeUtil.getFormatedTime("yyyy-MM-dd^HH-mm-ss",
		// System.currentTimeMillis());
		// sb.append("crash_");
		// PackageInfo info = VersionManage.getPackageInfo(context);
		// if (info != null) {
		// sb.append("V");
		// sb.append(info.versionName);
		// sb.append("_");
		// }
		// sb.append(date);
		// sb.append(".txt");
		// File logFile = new File(dir, sb.toString());
		// try {
		// FileOutputStream fos = new FileOutputStream(logFile);
		// fos.write(logInfo.getBytes());
		// fos.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
	}
}
