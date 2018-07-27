package com.example.eric.Land;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.eric.Personal.SelfInfo;

/**
 * 创建人：张宇
 * 用户信息
 * 创建时间：2018/7/17.
 */


public class UserDatabase extends SQLiteOpenHelper{

    private static final String name = "UserDataBase.db"; //数据库名
    private static final int version = 1;   //数据库版本
    public static final  String TAG = "UserDatabase";

    public UserDatabase(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //用户名(user)，密码(passWord),姓名(Name),身高(Height),体重(Weight),出生年月(birthDay),地区(district),性别(sex),证件类型(docuType),身份证号(idnumber)
        db.execSQL("CREATE TABLE IF NOT EXISTS UserInfo (dateid INTEGER PRIMARY KEY AUTOINCREMENT, user varchar(32), passWord varchar(10), Name varchar(32),Height varchar(32),Weight varchar(32),birthDay varchar(32),district varchar(32),sex varchar(32),docuType int,idnumber varchar(32))");
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
    public void modifyPass(String user,String password){
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("UPDATE UserInfo SET passWord = ? WHERE user= ?", new String[]{password,user});
    }
    public void saveInfo(String user,String password, String nickName,String sex,String birth,String region,String Height,String Weight){
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor1 = db.rawQuery("delete from UserInfo where user = ?",new String[]{user});
//        cursor1.close();
//        ContentValues cv = new ContentValues();
//        cv.put("user", user);
//        cv.put("passWord",password);
//        cv.put("Name", nickName);
//        cv.put("Height", Height);
//        cv.put("Weight", Weight);
//        cv.put("birthDay", birth);
//        cv.put("district", region);
//        cv.put("sex", sex);
//        db.insert("UserInfo", null, cv);
//        Cursor cursor = db.rawQuery("select * from UserInfo",new String[]{});
//        cursor.close();
//        db.close();
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("UPDATE UserInfo SET Name = ?, Height = ?,Weight = ?,birthday = ?,district = ?,sex = ? WHERE user = ?",new String[]{nickName,Height,Weight,birth,region,sex,user});
    }
    public String[] getInfo(String user){
        SQLiteDatabase db = getReadableDatabase();
        String info[] = new String[30];
        Cursor cursor = db.rawQuery("select * from UserInfo Where  user= ?", new String[]{user});
        if(cursor.moveToNext()){
            for(int i = 0;i<10;i++){
                info[i] = cursor.getString(i);
            }
        }
        return info;
    }
}
