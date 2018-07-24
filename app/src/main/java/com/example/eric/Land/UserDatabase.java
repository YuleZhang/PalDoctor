package com.example.eric.Land;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建人：张宇
 * 用户信息
 * 创建时间：2018/7/17.
 */


public class UserDatabase extends SQLiteOpenHelper{

    private static final String name = "UserDataBase.db"; //数据库名
    private static final int version = 1;   //数据库版本

    public UserDatabase(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //用户名(user)，密码(passWord),姓名(Name),身高(Height),体重(Weight),出生年月(birthDay),地区(district),性别(sex),证件类型(docuType),身份证号(idnumber)
        db.execSQL("CREATE TABLE IF NOT EXISTS UserInfo (dateid INTEGER PRIMARY KEY AUTOINCREMENT, user varchar(32), passWord varchar(10), Name varchar(10),Height int,Weight float,birthDay varchar(10),district varchar(10),sex varchar(10),docuType int,idnumber varchar(32))");
        db.execSQL("CREATE TABLE IF NOT EXISTS SexInfo (Id INTEGER PRIMARY KEY AUTOINCREMENT,Sex varchar(10))");
        db.execSQL("CREATE TABLE IF NOT EXISTS DocuType (Id INTEGER PRIMARY KEY AUTOINCREMENT,Type varchar(10))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    void insert(String user, String passWord) {    //插入数据
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user", user);
        cv.put("passWord", passWord);
        db.insert("UserInfo", null, cv);
        db.close();
    }
    byte detection(String user, String passWord) {   //返回0：不存在；1：正确；2：错误
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * From UserInfo Where user=?", new String[]{user});
        if (cursor.moveToNext()) {
            if(null == passWord){
                return 3;
            }
            if (passWord.equals(cursor.getString(2))) {
                cursor.close();
                return 1;
            } else {
                return 2;
            }
        } else {
            cursor.close();
            return 0;
        }
    }
}
