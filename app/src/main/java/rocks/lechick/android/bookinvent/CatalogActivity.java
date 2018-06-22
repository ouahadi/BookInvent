package rocks.lechick.android.bookinvent;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import rocks.lechick.android.bookinvent.adapters.SimpleFragmentPagerAdapter;
import rocks.lechick.android.bookinvent.data.BookContract;
import rocks.lechick.android.bookinvent.data.BookDbHelper;

public class CatalogActivity extends AppCompatActivity {

    private BookDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catallog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        SimpleFragmentPagerAdapter adapter;
        adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this);

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        mDbHelper = new BookDbHelper(this);
        displayDatabaseInfo();

    }

    @Override
    protected void onStart(){
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_NAME_AUTHOR,
                BookContract.BookEntry.COLUMN_NAME_TITLE,
                BookContract.BookEntry.COLUMN_NAME_QUANTITY,
                BookContract.BookEntry.COLUMN_NAME_SHIPMENT_STATUS,
                BookContract.BookEntry.COLUMN_NAME_EXPECTED_DELIVERY,
                BookContract.BookEntry.COLUMN_NAME_CATEGORY
        };

        // Filter results WHERE "title" = 'My Title'
        //String selection = BookContract.BookEntry.COLUMN_NAME_SHIPMENT_STATUS + " = ?";
        //String[] selectionArgs = { "DELIVERY_STATUS_IN_STOCK" };

        //String sortOrder = BookContract.BookEntry.COLUMN_NAME_AUTHOR + " DESC";

        Cursor cursor = db.query(BookContract.BookEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        TextView displayView = (TextView) findViewById(R.id.text_view_book);

        try {
            displayView.setText("The books table contains " + cursor.getCount() + " book entries.\n\n");
            displayView.append(BookContract.BookEntry._ID + " - " +
                    BookContract.BookEntry.COLUMN_NAME_AUTHOR + " - " +
                    BookContract.BookEntry.COLUMN_NAME_TITLE + " - " +
                    BookContract.BookEntry.COLUMN_NAME_QUANTITY + " - " +
                    BookContract.BookEntry.COLUMN_NAME_SHIPMENT_STATUS + " - " +
                    BookContract.BookEntry.COLUMN_NAME_EXPECTED_DELIVERY
            );

            int idColumnIndex = cursor.getColumnIndex(BookContract.BookEntry._ID);
            int authorColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_AUTHOR);
            int titleColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_TITLE);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_QUANTITY);
            int shipmentColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_SHIPMENT_STATUS);
            int deliveryColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_EXPECTED_DELIVERY);

            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentAuthor = cursor.getString(authorColumnIndex);
                String currentTitle = cursor.getString(titleColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                int currentShipmentStatus = cursor.getInt(shipmentColumnIndex);
                int currentExpDelivery = cursor.getInt(deliveryColumnIndex);
                displayView.append(("\n" + currentID + " - " +
                        currentAuthor + " - " +
                        currentTitle + " - " +
                        currentQuantity + " - " +
                        currentShipmentStatus + " - " +
                        currentExpDelivery));
            }
        }
        finally {
            cursor.close();
         }

    }

}


