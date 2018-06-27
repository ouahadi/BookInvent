package rocks.lechick.android.bookinvent;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLEncoder;

import rocks.lechick.android.bookinvent.data.BookContract;
import rocks.lechick.android.bookinvent.data.BookDbHelper;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Cursor mCursor;
    private TextView mAuthor;
    private TextView mTitle;
    private TextView mBuyingPrice;
    private TextView mSellingPrice;
    private TextView mQuantity;
    private TextView mCategoryView;
    private int quantityInt;
    private Uri mCurrentBookUri;
    private String supplierContact;
    private static final int EXISTING_BOOK_LOADER = 0;
    private String titleString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setTitle("Book Details");

        final Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.details_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, EditorActivity.class);
                Uri currentBookUri = mCurrentBookUri;
                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });


        mAuthor = (TextView) findViewById(R.id.details_author_text_view);
        mTitle = (TextView) findViewById(R.id.details_title_text_view);
        mBuyingPrice = (TextView) findViewById(R.id.details_buying_price);
        mSellingPrice = (TextView) findViewById(R.id.details_selling_price);
        mQuantity = (TextView) findViewById(R.id.details_stock_count);
        mCategoryView = (TextView) findViewById(R.id.details_category_text_view);

        Button mPlusStock = (Button) findViewById(R.id.details_plus_stock);
        Button mMinusStock = (Button) findViewById(R.id.details_minus_stock);
        Button mOrder = (Button) findViewById(R.id.details_order_button);
        TextView mFindNewSupplier = (TextView) findViewById(R.id.details_find_new_supplier_textview);
        TextView mReportDefect = (TextView) findViewById(R.id.details_report_textview);

        mFindNewSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://www.google.com/search?q=" + titleString;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        mOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BookContract.BookEntry.isValidMail(supplierContact) == true) {
                    Intent i = new Intent(Intent.ACTION_SENDTO);
                    i.setData(Uri.parse("mailto:"));
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{supplierContact});
                    i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_order_subj) + " " + titleString);
                    i.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_order_text));
                    if (i.resolveActivity(getPackageManager()) != null) {
                        startActivity(i);
                    }
                    else {
                        Toast.makeText(DetailsActivity.this, R.string.need_email_app, Toast.LENGTH_LONG);
                    }
                }
                if (BookContract.BookEntry.isValidMobile(supplierContact) == true) {
                    Intent ii = new Intent(Intent.ACTION_DIAL);
                    ii.setData(Uri.parse("tel:" + supplierContact));
                    startActivity(ii);
                }
            }
        });

        mReportDefect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BookContract.BookEntry.isValidMail(supplierContact) == true) {
                    Intent i = new Intent(Intent.ACTION_SENDTO);
                    i.setData(Uri.parse("mailto:"));
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{supplierContact});
                    i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_defect_subj) + " " + titleString);
                    i.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_defect_text));
                    startActivity(i);
                }
                if (BookContract.BookEntry.isValidMobile(supplierContact) == true) {
                    Intent ii = new Intent(Intent.ACTION_DIAL);
                    ii.setData(Uri.parse("tel:" + supplierContact));
                    startActivity(ii);
                }
            }
        });

        mPlusStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCursor.moveToFirst();
                long id = mCursor.getLong(mCursor.getColumnIndexOrThrow(BookContract.BookEntry._ID));


                String titleString = mCursor.getString(mCursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_TITLE));
                String authorString = mCursor.getString(mCursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_AUTHOR));
                String sPriceString = mCursor.getString(mCursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_SPRICE));
                String bPriceString = mCursor.getString(mCursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_BPRICE));
                String supplierName = mCursor.getString(mCursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_NAME));
                String supplierContact = mCursor.getString(mCursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_CONTACT));
                String category = mCursor.getString(mCursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_CATEGORY));
                String quantityStr = mCursor.getString(mCursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_QUANTITY));


                final ContentValues values = new ContentValues();
                values.put(BookContract.BookEntry.COLUMN_NAME_TITLE, titleString);
                values.put(BookContract.BookEntry.COLUMN_NAME_AUTHOR, authorString);
                values.put(BookContract.BookEntry.COLUMN_NAME_SPRICE, sPriceString);
                values.put(BookContract.BookEntry.COLUMN_NAME_BPRICE, bPriceString);
                values.put(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_NAME, supplierName);
                values.put(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_CONTACT, supplierContact);
                values.put(BookContract.BookEntry.COLUMN_NAME_CATEGORY, category);
                values.put(BookContract.BookEntry.COLUMN_NAME_QUANTITY, quantityStr);
                editQuantityPlus(values, mCursor, id);
            }
        });

        mMinusStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCursor.moveToFirst();
                long id = mCursor.getLong(mCursor.getColumnIndexOrThrow(BookContract.BookEntry._ID));


                String titleString = mCursor.getString(mCursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_TITLE));
                String authorString = mCursor.getString(mCursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_AUTHOR));
                String sPriceString = mCursor.getString(mCursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_SPRICE));
                String bPriceString = mCursor.getString(mCursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_BPRICE));
                String supplierName = mCursor.getString(mCursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_NAME));
                String supplierContact = mCursor.getString(mCursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_CONTACT));
                String category = mCursor.getString(mCursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_CATEGORY));
                String quantityStr = mCursor.getString(mCursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_QUANTITY));


                final ContentValues values = new ContentValues();
                values.put(BookContract.BookEntry.COLUMN_NAME_TITLE, titleString);
                values.put(BookContract.BookEntry.COLUMN_NAME_AUTHOR, authorString);
                values.put(BookContract.BookEntry.COLUMN_NAME_SPRICE, sPriceString);
                values.put(BookContract.BookEntry.COLUMN_NAME_BPRICE, bPriceString);
                values.put(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_NAME, supplierName);
                values.put(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_CONTACT, supplierContact);
                values.put(BookContract.BookEntry.COLUMN_NAME_CATEGORY, category);
                values.put(BookContract.BookEntry.COLUMN_NAME_QUANTITY, quantityStr);
                editQuantityMinus(values, mCursor, id);
            }
        });

    }

    public void editQuantityPlus(ContentValues values, Cursor cursor, long id) {
        String whereThing = BookContract.BookEntry._ID + " = " + id;
        cursor.moveToFirst();
        quantityInt = quantityInt + 1;
        values.put(BookContract.BookEntry.COLUMN_NAME_QUANTITY, quantityInt);
        mQuantity.setText(Integer.toString(quantityInt));
        getContentResolver().update(BookContract.BookEntry.CONTENT_URI, values, whereThing, null);
    }

    public void editQuantityMinus(ContentValues values, Cursor cursor, long id) {
        String whereThing = BookContract.BookEntry._ID + " = " + id;
        cursor.moveToFirst();
        if (quantityInt > 0) {
            quantityInt = quantityInt - 1;
            values.put(BookContract.BookEntry.COLUMN_NAME_QUANTITY, quantityInt);
            mQuantity.setText(Integer.toString(quantityInt));
            getContentResolver().update(BookContract.BookEntry.CONTENT_URI, values, whereThing, null);
        }
        if (quantityInt <= 0) {
            Toast.makeText(this, R.string.cant_sell_zero, Toast.LENGTH_LONG);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_NAME_TITLE,
                BookContract.BookEntry.COLUMN_NAME_AUTHOR,
                BookContract.BookEntry.COLUMN_NAME_QUANTITY,
                BookContract.BookEntry.COLUMN_NAME_SPRICE,
                BookContract.BookEntry.COLUMN_NAME_BPRICE,
                BookContract.BookEntry.COLUMN_NAME_SUPPLIER_CONTACT,
                BookContract.BookEntry.COLUMN_NAME_SUPPLIER_NAME,
                BookContract.BookEntry.COLUMN_NAME_SHIPMENT_STATUS,
                BookContract.BookEntry.COLUMN_NAME_CATEGORY
        };
        return new CursorLoader(this,
                mCurrentBookUri, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null) {
            Log.d("Database", "Data empty");
        } else {
            cursor.moveToFirst();

            int titleColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_TITLE);
            int authorColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_AUTHOR);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_QUANTITY);
            int spriceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_SPRICE);
            int bpriceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_BPRICE);
            int supplierConColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_CONTACT);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_NAME);
            int shipmentStatusColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_SHIPMENT_STATUS);
            int categoryColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_CATEGORY);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(titleColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            double sprice = cursor.getDouble(spriceColumnIndex);
            double bprice = cursor.getDouble(bpriceColumnIndex);
            String mSupplierContact = cursor.getString(supplierConColumnIndex);
            int category = cursor.getInt(categoryColumnIndex);

            // Update the views on the screen with the values from the database
            mAuthor.setText(author);
            mTitle.setText(title);
            mBuyingPrice.setText(Double.toString(bprice));
            mSellingPrice.setText(Double.toString(sprice));
            mQuantity.setText(Integer.toString(quantity));

            supplierContact = mSupplierContact;
            titleString = title;
            quantityInt = quantity;
            mCursor = cursor;

            switch (category) {
                case BookContract.BookEntry.CATEGORY_FICTION:
                    mCategoryView.setText(R.string.fiction);
                case BookContract.BookEntry.CATEGORY_BUSINESS:
                    mCategoryView.setText(R.string.business);
                case BookContract.BookEntry.CATEGORY_POP_SCIENCE:
                    mCategoryView.setText(R.string.pop_sci);
                case BookContract.BookEntry.CATEGORY_OTHER:
                    mCategoryView.setText(R.string.other);

            }

            ImageView bookImage = (ImageView) findViewById(R.id.details_book_image);

            if (category == BookContract.BookEntry.CATEGORY_FICTION) {
                bookImage.setImageResource(R.drawable.orange_orange_book);
            }
            if (category == BookContract.BookEntry.CATEGORY_BUSINESS) {
                bookImage.setImageResource(R.drawable.book_thumbnail);
            }
            if (category == BookContract.BookEntry.CATEGORY_POP_SCIENCE) {
                bookImage.setImageResource(R.drawable.yellow_orange_book);
            }
            if (category == BookContract.BookEntry.CATEGORY_OTHER) {
                bookImage.setImageResource(R.drawable.turquoise_book);
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTitle.setText("");
        mAuthor.setText("");
        mQuantity.setText("");
        mSellingPrice.setText("");
        mBuyingPrice.setText("");
        mCategoryView.setText(R.string.other);
    }

}

