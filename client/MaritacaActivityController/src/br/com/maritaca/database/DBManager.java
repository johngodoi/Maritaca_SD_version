package br.com.maritaca.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "answers";
	private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
			+ " (" + "formid" + " TEXT, " + "xml" + " TEXT);";
	public static final String DATABASE_NAME = "dados";

	public DBManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	// private final DatabaseOpenHelper dbHelper;
	//
	// public DBManager(Context context) {
	// dbHelper = new DatabaseOpenHelper(context, null, null, 0);
	// try {
	// dbHelper.createDataBase();
	// dbHelper.openDataBase();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// public SQLiteDatabase getReadableDataBase() {
	// return dbHelper.getReadableDatabase();
	// }
	//
	// public SQLiteDatabase getWritableDatabase() {
	// return dbHelper.getWritableDatabase();
	// }
	//
	// public void close() {
	//
	// dbHelper.close();
	// }
	//
	// private class DatabaseOpenHelper extends SQLiteOpenHelper {
	// private SQLiteDatabase database;
	// private final Context context;
	//
	// private final static String DATABASE_PATH =
	// "/data/data/br.com/databases/";
	// private final static String DATABASE_NAME = "dados.db";
	// private final static int DATABASE_VERSION = 1;
	//
	// public DatabaseOpenHelper(Context context, String name,
	// CursorFactory factory, int version) {
	//
	// super(context, DATABASE_NAME, null, DATABASE_VERSION);
	// this.context = context;
	// // TODO Auto-generated constructor stub
	// }
	//
	// public void createDataBase() throws IOException {
	// boolean dbExist = false;
	// if (dbExist) {
	// } else {
	// this.getWritableDatabase();
	// try {
	// copyDataBase();
	// } catch (IOException e) {
	// throw new Error("Error copying database");
	// }
	// }
	//
	// }
	//
	// private boolean checkDataBase() {
	// SQLiteDatabase checkDB = null;
	// try {
	// String myPath = DATABASE_PATH + DATABASE_NAME;
	// checkDB = SQLiteDatabase.openDatabase(myPath, null,
	// SQLiteDatabase.OPEN_READONLY);
	// } catch (SQLiteException e) {
	// }
	// if (checkDB != null) {
	// checkDB.close();
	// }
	// return checkDB != null ? true : false;
	// }
	//
	// private void copyDataBase() throws IOException {
	// InputStream myInput = context.getAssets().open(DATABASE_NAME);
	// String outFileName = DATABASE_PATH + DATABASE_NAME;
	// OutputStream myOutput = new FileOutputStream(outFileName);
	// byte[] buffer = new byte[1024];
	// int length;
	// while ((length = myInput.read(buffer)) > 0) {
	// myOutput.write(buffer, 0, length);
	// }
	// myOutput.flush();
	// myOutput.close();
	// myInput.close();
	// }
	//
	// public void openDataBase() throws SQLException {
	// String myPath = DATABASE_PATH + DATABASE_NAME;
	// database = SQLiteDatabase.openDatabase(myPath, null,
	// SQLiteDatabase.OPEN_READWRITE);
	//
	// }
	//
	// public synchronized void close() {
	// if (database != null)
	// database.close();
	// super.close();
	// }
	//
	// @Override
	// public void onCreate(SQLiteDatabase db) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	// {
	// // TODO Auto-generated method stub
	//
	// }
	// }

}
