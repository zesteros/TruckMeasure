package com.mx.vise.cubicaciones.dao;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;


/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el miércoles 02 de mayo del 2018
 *
 * @author Angelo de Jesus Loza Martinez
 * @version CombustibleVISEandroidv1.0
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CUBAGE.db";
    public static final String DATABASE_K = "byc3andr0yt";
    private final Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        SQLiteDatabase.loadLibs(mContext);
        db.changePassword(DATABASE_K);
        db.execSQL(Contract.SCRIPT.SQL_CREATE_ENTRIES_BRANDS);
        db.execSQL(Contract.SCRIPT.SQL_CREATE_ENTRIES_SYNDICATES);
        db.execSQL(Contract.SCRIPT.SQL_CREATE_ENTRIES_TAGS);
        db.execSQL(Contract.SCRIPT.SQL_CREATE_ENTRIES_CUBAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        SQLiteDatabase.loadLibs(mContext);
        db.execSQL(Contract.SCRIPT.SQL_DELETE_ENTRIES_BRANDS);
        db.execSQL(Contract.SCRIPT.SQL_DELETE_ENTRIES_SYNDICATES);
        db.execSQL(Contract.SCRIPT.SQL_DELETE_ENTRIES_TAGS);
        onCreate(db);
    }

    public void onUpgradeAll(SQLiteDatabase db, int oldVersion, int newVersion) {
        SQLiteDatabase.loadLibs(mContext);
        db.execSQL(Contract.SCRIPT.SQL_DELETE_ENTRIES_BRANDS);
        db.execSQL(Contract.SCRIPT.SQL_DELETE_ENTRIES_SYNDICATES);
        db.execSQL(Contract.SCRIPT.SQL_DELETE_ENTRIES_TAGS);
        db.execSQL(Contract.SCRIPT.SQL_DELETE_ENTRIES_CUBAGE);
        onCreate(db);
    }



}
