package com.example.bmi_calcurate

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context : Context) : SQLiteOpenHelper(context , "bmidb" , null , 1){
    override fun onCreate(db: SQLiteDatabase?) {
        // 테이블 구성
        val bmiSql = """
           create table bmidb_member(
            _id integer primary key autoincrement,
            bmi not null,
            time not null   
           )
        """

        // sql 실행
        db?.execSQL(bmiSql)
        // test data insert
//        db?.execSQL("insert into bmidb_member (bmi , time)" +
//                        "values ('-1')"
//        )

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table bmidb_member")
        onCreate(db)
    }
}