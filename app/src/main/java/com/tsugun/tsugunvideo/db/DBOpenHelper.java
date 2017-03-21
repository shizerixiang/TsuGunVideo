package com.tsugun.tsugunvideo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite数据库
 * Created by shize on 2017/3/2.
 */

class DBOpenHelper extends SQLiteOpenHelper {

    DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建视频信息表
        db.execSQL("create table videoInfo(" +
                "id Integer primary key autoincrement," +
                "title varchar(100) not null," +
                "url varchar(400) not null unique, " +
                "type varchar(40) not null, " +
                "size Integer," +
                "duration Integer," +
                "record Integer," +
                "height Real," +
                "width Real," +
                "rotation Integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
