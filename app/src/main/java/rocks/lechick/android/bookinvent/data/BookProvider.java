package rocks.lechick.android.bookinvent.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import rocks.lechick.android.bookinvent.R;

public class BookProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int BOOKS = 100;
    private static final int BOOK_ID = 101;

    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    private BookDbHelper mDbHelper;

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BookProvider.class.getSimpleName();


    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case BOOKS:
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query silly URIs" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookContract.BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookContract.BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues contentValues) {
        String title = contentValues.getAsString(BookContract.BookEntry.COLUMN_NAME_TITLE);
        if (title == null) {
            Toast.makeText(getContext(), R.string.book_requires_name, Toast.LENGTH_LONG);
        }
        String author = contentValues.getAsString(BookContract.BookEntry.COLUMN_NAME_AUTHOR);
        if (author == null) {
            Toast.makeText(getContext(), R.string.book_requires_author, Toast.LENGTH_LONG);
        }
        String buyingPrice = contentValues.getAsString(BookContract.BookEntry.COLUMN_NAME_BPRICE);
        if (buyingPrice == null && buyingPrice.length() == 0) {
            Toast.makeText(getContext(), R.string.book_requires_buying_price, Toast.LENGTH_LONG);
        }
        String sellingPrice = contentValues.getAsString(BookContract.BookEntry.COLUMN_NAME_SPRICE);
        if (sellingPrice == null && sellingPrice.length() == 0) {
            Toast.makeText(getContext(), R.string.book_requires_selling_price, Toast.LENGTH_LONG);
        }
        String supplierName = contentValues.getAsString(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_NAME);
        if (supplierName == null) {
            Toast.makeText(getContext(), R.string.book_requires_supplier_data, Toast.LENGTH_LONG);        }
        String supplierContact = contentValues.getAsString(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_CONTACT);
        if (!BookContract.BookEntry.isValidMail(supplierContact) && !BookContract.BookEntry.isValidMobile(supplierContact)) {
            Toast.makeText(getContext(), R.string.book_requires_supplier_contact, Toast.LENGTH_LONG);
        }
        Integer quantity = contentValues.getAsInteger(BookContract.BookEntry.COLUMN_NAME_QUANTITY);
        if (quantity == null) {
            Toast.makeText(getContext(), R.string.book_requires_quantity, Toast.LENGTH_LONG);
        }
        Integer category = contentValues.getAsInteger(BookContract.BookEntry.COLUMN_NAME_CATEGORY);
        if (category == null && !BookContract.BookEntry.isValidCategory(category)) {
            Toast.makeText(getContext(), R.string.book_requires_category, Toast.LENGTH_LONG);
        }
        Integer shipment = contentValues.getAsInteger(BookContract.BookEntry.COLUMN_NAME_SHIPMENT_STATUS);


        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(BookContract.BookEntry.TABLE_NAME, null, contentValues);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);

    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        String title = contentValues.getAsString(BookContract.BookEntry.COLUMN_NAME_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Book requires a name");
        }
        String author = contentValues.getAsString(BookContract.BookEntry.COLUMN_NAME_AUTHOR);
        if (author == null) {
            throw new IllegalArgumentException("Please add an author");
        }
        String buyingPrice = contentValues.getAsString(BookContract.BookEntry.COLUMN_NAME_BPRICE);
        if (buyingPrice == null) {
            throw new IllegalArgumentException("Book requires a price");
        }
        String sellingPrice = contentValues.getAsString(BookContract.BookEntry.COLUMN_NAME_SPRICE);
        if (sellingPrice == null) {
            throw new IllegalArgumentException("Please add selling price");
        }
        String supplierName = contentValues.getAsString(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Book requires a price");
        }
        String supplierContact = contentValues.getAsString(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_CONTACT);
        //if (!BookContract.BookEntry.isValidMail(supplierContact) && !BookContract.BookEntry.isValidMobile(supplierContact)) {
        //    throw new IllegalArgumentException("Please add a valid number or email address");
        //}
        Integer quantity = contentValues.getAsInteger(BookContract.BookEntry.COLUMN_NAME_QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("Please add quantity");
        }

        Integer category = contentValues.getAsInteger(BookContract.BookEntry.COLUMN_NAME_CATEGORY);
        if (category == null && !BookContract.BookEntry.isValidCategory(category)) {
            throw new IllegalArgumentException("Please select category");
        }
        Integer shipment = contentValues.getAsInteger(BookContract.BookEntry.COLUMN_NAME_SHIPMENT_STATUS);

        if (contentValues.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(BookContract.BookEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


}
