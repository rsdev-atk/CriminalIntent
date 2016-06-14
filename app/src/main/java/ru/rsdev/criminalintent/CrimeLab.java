package ru.rsdev.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.rsdev.criminalintent.database.CrimeBaseHelper;
import ru.rsdev.criminalintent.database.CrimeCursorWrapper;
import ru.rsdev.criminalintent.database.CrimeDbSchema;


public class CrimeLab {
    private static CrimeLab sCrimeLab;
    //private List<Crime> mCrimes;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context){
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();

        //mCrimes = new ArrayList<>();
        /*
        for (int i=0;i<100;i++){
            Crime crime = new Crime();
            crime.setTitle("Преступление №" + i);
            crime.setSolved(i%2 == 0);
            mCrimes.add(crime);
        }
        */
    }

    public List<Crime> getCrime(){

        //return mCrimes;
        //return new ArrayList<>();
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null,null);

        try{
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }

        }
        finally {
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID id){
        ContentValues values = new ContentValues();
        mDatabase.insert(CrimeDbSchema.CrimeTable.NAME, null, values);

        CrimeCursorWrapper cursor = queryCrimes(CrimeDbSchema.Cols.UUID + " = ?",
                new String[]{id.toString()});

        try {
            if(cursor.getCount() == 0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        }
        finally {
            cursor.close();
        }

        /*for(Crime crime : mCrimes){
            if(crime.getId().equals(id)) {
                return crime;
            }
        }
        */
        //return null;
    }

    public void addCrime(Crime crime){

        //mCrimes.add(crime);
    }

    private static ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeDbSchema.Cols.UUID, crime.getId().toString());
        values.put(CrimeDbSchema.Cols.TITLE, crime.getTitle());
        values.put(CrimeDbSchema.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeDbSchema.Cols.SOLVED, crime.isSolved());

        return values;
    }

    public void updateCrime(Crime crime){
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeDbSchema.CrimeTable.NAME, values, CrimeDbSchema.Cols.UUID + " = ?", new String[]{ uuidString});
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                CrimeDbSchema.CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new CrimeCursorWrapper(cursor);
    }
}

