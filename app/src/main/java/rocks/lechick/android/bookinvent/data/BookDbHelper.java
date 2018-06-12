package rocks.lechick.android.bookinvent.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BookDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "bookstock.db";
    private static final int DATABASE_VERSION = 1;


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + BookContract.BookEntry.TABLE_NAME;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_ENTRIES = "CREATE TABLE " + BookContract.BookEntry.TABLE_NAME + " (" +
                BookContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BookContract.BookEntry.COLUMN_NAME_AUTHOR + " TEXT NOT NULL, " +
                BookContract.BookEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL, " +
                BookContract.BookEntry.COLUMN_NAME_BPRICE + " INTEGER NOT NULL, " +
                BookContract.BookEntry.COLUMN_NAME_SPRICE + " INTEGER NOT NULL," +
                BookContract.BookEntry.COLUMN_NAME_QUANTITY + " INTEGER NOT NULL," +
                BookContract.BookEntry.COLUMN_NAME_SUPPLIER_NAME + " STRING, " +
                BookContract.BookEntry.COLUMN_NAME_SUPPLIER_CONTACT + " STRING, " +
                BookContract.BookEntry.COLUMN_NAME_SHIPMENT_STATUS + " INTEGER, " +
                BookContract.BookEntry.COLUMN_NAME_EXPECTED_DELIVERY + " INTEGER );";
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);

    }
}
