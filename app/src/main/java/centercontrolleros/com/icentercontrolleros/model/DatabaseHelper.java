package centercontrolleros.com.icentercontrolleros.model;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import centercontrolleros.com.icentercontrolleros.ui.service.MainService;
import centercontrolleros.com.icentercontrolleros.ui.untils.Logger;

/**
 * Created by Duy on 8/31/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private final static String FAVOURITE_APP = "NAME_APP";
    private final static String TABLE_NAME = "FAVOURITE_APP";
    private final static String ID = "ID";
    private final static String APP_NAME = "APP_NAME";
    private final static String APP_ICON = "APP_ICON";
    private final static String APP_PACKAGENAME = "APP_PACKAGENAME";
    private final static String SORT_POSITION = "SORT_POSITION";

    public static int VERSION_CODE = 1;
    public String TAG = DatabaseHelper.class.getSimpleName();
    SQLiteDatabase mSQLiteDatabase;
    PackageManager mPackageManager;

    public DatabaseHelper(Context context) {
        super(context, FAVOURITE_APP, null, VERSION_CODE);
        mSQLiteDatabase = this.getWritableDatabase();
        mPackageManager = context.getPackageManager();
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String tblFavourite = "CREATE TABLE " + TABLE_NAME + " ( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + APP_NAME + " TEXT, " + APP_ICON + " TEXT, " + APP_PACKAGENAME + " TEXT ,"
                + SORT_POSITION + " INT "
                + " )";
        sqLiteDatabase.execSQL(tblFavourite);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean insertFavouriteApp(Context mContext, AppInfor mApplication, int count) {
        return insertFavouriteAppHanlder(mContext, mApplication, count);
    }


    public boolean insertFavouriteApp(Context mContext, ApplicationInfo mApplication, int count) {

        return insertFavouriteAppHanlder(mContext, mApplication, count);

    }

    public boolean insertFavouriteAppHanlder(Context mContext, ApplicationInfo mApplication, int count) {

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(APP_NAME, (String) mApplication.loadLabel(mPackageManager));
        mContentValues.put(APP_ICON, String.valueOf(mApplication.loadIcon(mPackageManager)));
        Logger.d(TAG, String.valueOf(mApplication.loadIcon(mPackageManager)));
        mContentValues.put(APP_PACKAGENAME, mApplication.packageName);

        int positionSort = count;

//        if(mApplication instanceof  AppInfor)
//             positionSort = ((AppInfor) mApplication).positionSort;
        Logger.d("mApplication", mApplication.packageName);


        mContentValues.put(SORT_POSITION, positionSort);
        Logger.d("positionSort", "AAAAAAAAAAAA" + positionSort);
        long insert = mSQLiteDatabase.insert(TABLE_NAME, null, mContentValues);
        if (insert != 0) {
            MainService.saveSharepreferences(mContext, true);
            return true;

        } else {
            return false;
        }


    }


    public void updateFavouriteApp(Context mContext, AppInfor mApplication) {

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(SORT_POSITION, mApplication.positionSort);
        mSQLiteDatabase.update(TABLE_NAME, mContentValues, APP_PACKAGENAME + "=?", new String[]{mApplication.packageName});
        Logger.d(TAG, mApplication.packageName + mApplication.name + mApplication.positionSort);
        MainService.saveSharepreferences(mContext, true);
    }


    public List<AppInfor> getListFavouriteApp(Context mContext) {

        List<AppInfor> mListFavourite = new ArrayList<>();
        String mQuery = "SELECT " + APP_NAME + ", " + APP_ICON + ", " + SORT_POSITION + ", " + APP_PACKAGENAME + " FROM " + TABLE_NAME
                + " ORDER BY " + SORT_POSITION + " ASC";

        Cursor cursor = mSQLiteDatabase.rawQuery(mQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AppInfor mApplication = new AppInfor();
            mApplication.name = cursor.getString(cursor.getColumnIndex(APP_NAME));
            String name = cursor.getString(cursor.getColumnIndex(APP_ICON));
            int sortposition = cursor.getInt(cursor.getColumnIndex(SORT_POSITION));
            int id = mContext.getResources().getIdentifier(name, "drawable", mContext.getPackageName());

            mApplication.icon = id;
            mApplication.positionSort = sortposition;
            mApplication.packageName = cursor.getString(cursor.getColumnIndex(APP_PACKAGENAME));
            Logger.d(TAG, mApplication.packageName);
            mListFavourite.add(mApplication);
            cursor.moveToNext();
        }
        if (mListFavourite.size() > 0)
            return mListFavourite;
        else
            return null;
    }

    public boolean checkInsert(String appname) {
        String tblQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + APP_NAME + " = \"" + appname + "\"";
        Cursor cursor = mSQLiteDatabase.rawQuery(tblQuery, null);
        if (cursor.getCount() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public int getSizeListFavourite() {
        String tblQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = mSQLiteDatabase.rawQuery(tblQuery, null);
        return cursor.getCount();

    }

    public boolean deleteFavourite(Context mContext, String mPackageNam) {
        long delete = mSQLiteDatabase.delete(TABLE_NAME, APP_PACKAGENAME + " = '" + mPackageNam + "'", null);
        if (delete != 0) {
            MainService.saveSharepreferences(mContext, true);
            return true;
        } else {
            return false;
        }
    }


//    public void deleteAllFavourite() {
//        mSQLiteDatabase.delete(TABLE_NAME, null, null);
//    }
}
