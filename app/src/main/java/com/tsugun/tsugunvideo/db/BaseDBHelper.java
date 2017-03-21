package com.tsugun.tsugunvideo.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tsugun.tsugunvideo.dao.MediaFileFinder;
import com.tsugun.tsugunvideo.entity.MediaFileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作类
 * Created by shize on 2017/3/3.
 */

public class BaseDBHelper implements MediaFileFinder {

    private static final String DB_SELECT_ALL_FILE_INFO = "select * from videoInfo";
    private static final String DB_DELETE_ALL_FILE_INFO = "delete from videoInfo";
    private static final String DB_DELETE_WHERE_URL = "delete from videoInfo where url=?";
    private static final String DB_INSERT_FILE_INFO = "insert into videoInfo(title,url,type,size,duration,record,height,width) values(?,?,?,?,?,?,?,?)";
    private static final String DB_SELECT_WHERE_URL = "select * from videoInfo where url=?";
    private static final String DB_SELECT_WHERE_ID = "select * from videoInfo where id=?";
    private static final String DB_SELECT_WHERE_TYPE = "select * from videoInfo where type=?";
    private static final String DB_UPDATE_FILE_INFO_RECORD = "update videoInfo set record=? where id=?";
    private static final String DB_UPDATE_FILE_INFO = "update videoInfo set title=?,size=?,duration=?,record=?,height=?,width=? where url=?";

    private DBOpenHelper mDBOpenHelper;

    public BaseDBHelper(Context context, String dbName, int version) {
        mDBOpenHelper = new DBOpenHelper(context, dbName, null, version);
    }

    @Override
    public List<MediaFileInfo> getAllFileInfo() {
        return getMediaFileInfoList(DB_SELECT_ALL_FILE_INFO, null);
    }

    @Override
    public void deleteAllFileInfo() {
        SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        db.execSQL(DB_DELETE_ALL_FILE_INFO);
        db.close();
    }

    @Override
    public void deleteFileInfo(String url) {
        SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        db.execSQL(DB_DELETE_WHERE_URL, new Object[]{url});
        db.close();
    }

    @Override
    public void addFileInfo(MediaFileInfo fileInfo) {
        if (getFileInfoByUrl(fileInfo.getUrl()) == null) {
            SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
            db.execSQL(DB_INSERT_FILE_INFO, new Object[]{
                    fileInfo.getTitle(), fileInfo.getUrl(), fileInfo.getType(),
                    fileInfo.getSize(), fileInfo.getDuration(), fileInfo.getRecord(),
                    fileInfo.getHeight(), fileInfo.getWidth()
            });
            db.close();
        } else {
            upDateDatabaseFileInfo(fileInfo);
        }
    }

    @Override
    public void addFileInfo(List<MediaFileInfo> fileInfo) {
        for (int i = 0; i < fileInfo.size(); i++) {
            addFileInfo(fileInfo.get(i));
        }
    }

    @Override
    public MediaFileInfo getFileInfoByUrl(String url) {
        List<MediaFileInfo> infoList = getMediaFileInfoList(DB_SELECT_WHERE_URL, new String[]{url});
        if (infoList.size() == 0){
            return null;
        }
        return infoList.get(0);
    }

    @Override
    public List<MediaFileInfo> getFileInfoByType(String type) {
        return getMediaFileInfoList(DB_SELECT_WHERE_TYPE, new String[]{type});
    }

    @Override
    public List<MediaFileInfo> getFileInfoByFolderUrl(String folderUrl) {
        return getMediaFileInfoList(DB_SELECT_WHERE_URL, new String[]{folderUrl + "%"});
    }

    @Override
    public boolean updateFileInfo(MediaFileInfo mediaFileInfo) {
        if (getMediaFileInfoList(DB_SELECT_WHERE_ID,
                new String[]{mediaFileInfo.getId() + ""}).get(0) != null) {
            SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
            db.execSQL(DB_UPDATE_FILE_INFO_RECORD, new Object[]{mediaFileInfo.getRecord(),
                    mediaFileInfo.getId()});
            db.close();
        } else {
            return false;
        }
        return true;
    }

    /**
     * 获取视频文件信息
     *
     * @param cursor 游标
     * @return 视频文件信息
     */
    @NonNull
    private MediaFileInfo getMediaFileInfo(Cursor cursor) {
        return new MediaFileInfo(
                cursor.getInt(cursor.getColumnIndex("id")),
                cursor.getString(cursor.getColumnIndex("title")),
                cursor.getString(cursor.getColumnIndex("url")),
                cursor.getString(cursor.getColumnIndex("type")),
                cursor.getInt(cursor.getColumnIndex("size")),
                cursor.getInt(cursor.getColumnIndex("duration")),
                cursor.getInt(cursor.getColumnIndex("record")),
                cursor.getFloat(cursor.getColumnIndex("height")),
                cursor.getFloat(cursor.getColumnIndex("width")),
                cursor.getInt(cursor.getColumnIndex("rotation")));
    }

    /**
     * 通过查询语句获取文件信息集合
     *
     * @param sql    sql语句
     * @param filter 过滤条件
     * @return 媒体文件信息
     */
    @NonNull
    private List<MediaFileInfo> getMediaFileInfoList(String sql, String[] filter) {
        SQLiteDatabase db = mDBOpenHelper.getReadableDatabase();
        List<MediaFileInfo> infoList = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, filter);
        while (cursor.moveToNext()) {
            MediaFileInfo fileInfo = getMediaFileInfo(cursor);
            infoList.add(fileInfo);
        }
        cursor.close();
        db.close();
        return infoList;
    }

    /**
     * 添加数据时，对已存在数据进行更新
     *
     * @param fileInfo 文件信息
     */
    private void upDateDatabaseFileInfo(MediaFileInfo fileInfo) {
        SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        db.execSQL(DB_UPDATE_FILE_INFO, new Object[]{
                fileInfo.getTitle(), fileInfo.getSize(), fileInfo.getDuration(),
                fileInfo.getRecord(), fileInfo.getHeight(), fileInfo.getWidth(),
                fileInfo.getUrl()});
        db.close();
    }
}
