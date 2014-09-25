package com.gather.android.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * @ClassName DbService
 * @Description 数据库服务
 * @author ouArea
 * @date 2013-11-8 上午11:00:46
 * 
 */
public abstract class DbService {
	protected DbHelper dbHelper;
	protected SQLiteDatabase sdb;
	protected Cursor cursor;

	public DbService(Context context) {
		dbHelper = new DbHelper(context);
	}

	public DbHelper getDbHelper() {
		return dbHelper;
	}

	/**
	 * 
	 * @Title writeBegin
	 * @Description 当方法有写操作，方法开始时调用
	 * @author ouArea
	 * @date 2013-11-8 上午11:06:45
	 */
	public void writeBegin() {
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
	}

	/**
	 * 
	 * @Title writeSuccess
	 * @Description 当方法有写操作，方法执行成功时调用
	 * @author ouArea
	 * @date 2013-11-8 上午11:37:30
	 */
	public void writeSuccess() {
		sdb.setTransactionSuccessful();
	}

	/**
	 * 
	 * @Title writeOver
	 * @Description 当方法有写操作，方法执行完时调用
	 * @author ouArea
	 * @date 2013-11-8 上午11:38:06
	 */
	public void writeOver() {
		sdb.endTransaction();
		sdb.close();
	}

	/**
	 * 
	 * @Title readBegin
	 * @Description 当方法只有读时，方法开始时调用
	 * @author ouArea
	 * @date 2013-11-8 上午11:38:25
	 */
	public void readBegin() {
		sdb = dbHelper.getReadableDatabase();
	}

	/**
	 * 
	 * @Title readOver
	 * @Description 当方法只有读时，方法执行完时调用
	 * @author ouArea
	 * @date 2013-11-8 上午11:37:27
	 */
	public void readOver() {
		sdb.close();
	}

}
