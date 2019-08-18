package au.edu.swin.sdmd.birthdayDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME="birthdays";
    static final String PERSON_TBL = "person";
    static final String NAME="name";
    static final String DOB="dob";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    /** A simple function to get started */
    public void onCreate(SQLiteDatabase db)
    {
        String createStmt = "CREATE TABLE person (_id INTEGER PRIMARY KEY AUTOINCREMENT, ";
        createStmt += " name TEXT, dob TEXT);";
        db.execSQL(createStmt);

        // insert content into the table directly
        ContentValues cv = new ContentValues();
        cv.put(NAME, "Darth Vader");
        cv.put(DOB, "23/11/2045");
        db.insert(PERSON_TBL, NAME, cv);

        cv.put(NAME, "George Swinburne");
        cv.put(DOB, "03/02/1861");
        db.insert(PERSON_TBL, NAME, cv);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        android.util.Log.w("BIRTHDAY_DB_HELPER", "Evil method to upgrade db, will destroy old data");
        db.execSQL("DROP TABLE IF EXISTS "+PERSON_TBL);
        onCreate(db);
    }
}
