package rocks.lechick.android.bookinvent;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import android.widget.Toast;

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

        SimpleFragmentPagerAdapter adapter;
        adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this);

        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void editQuantity(Context context, ContentValues values, Cursor cursor, long id, int position) {
        String whereThing = BookContract.BookEntry._ID + " = " + id;
        cursor.moveToPosition(position);
        int quantityInt = cursor.getInt(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_QUANTITY));
        if (quantityInt > 0) {
            quantityInt = quantityInt - 1;
            values.put(BookContract.BookEntry.COLUMN_NAME_QUANTITY, quantityInt);

            context.getContentResolver().update(BookContract.BookEntry.CONTENT_URI, values, whereThing, null);
        }
        if (0 >= quantityInt) {
            Toast.makeText(this, R.string.cant_sell_zero, Toast.LENGTH_LONG);
        }

    }
}


