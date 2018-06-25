package rocks.lechick.android.bookinvent.adapters;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import rocks.lechick.android.bookinvent.CatalogActivity;
import rocks.lechick.android.bookinvent.R;
import rocks.lechick.android.bookinvent.data.BookContract;
import rocks.lechick.android.bookinvent.data.BookDbHelper;

import static android.net.Uri.withAppendedPath;

public class BookCursorAdapter extends CursorAdapter {

    private int quantityInt;

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView title = (TextView) view.findViewById(R.id.list_title_textview);
        TextView author = (TextView) view.findViewById(R.id.list_author_textview);
        TextView price = (TextView) view.findViewById(R.id.list_selling_price_text_view);
        final TextView quantity = (TextView) view.findViewById(R.id.list_quantity_text_view);
        ImageView bookImage = (ImageView) view.findViewById(R.id.list_book_imageview);
        Button sellButton = (Button) view.findViewById(R.id.button_sell);

        String titleString = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_TITLE));
        String authorString = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_AUTHOR));
        String priceString = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_SPRICE));
        final String[] quantityString = {cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_QUANTITY))};
        int category = cursor.getInt(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_CATEGORY));

        title.setText(titleString);
        author.setText(authorString);
        price.setText(priceString);
        quantity.setText(quantityString[0]);

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

        sellButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                quantityInt = Integer.parseInt(quantityString[0]);
                quantityInt = quantityInt - 1;
                quantityString[0] = Integer.toString(quantityInt);
                quantity.setText(quantityString[0]);
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(BookContract.BookEntry._ID));
                Uri currentBookUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, id);


                String titleString = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_TITLE));
                String authorString = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_AUTHOR));
                String sPriceString = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_SPRICE));
                String bPriceString = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_BPRICE));
                String supplierName = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_NAME));
                String supplierContact = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_CONTACT));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_CATEGORY));
                String quantityStr = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_QUANTITY));


                final ContentValues values = new ContentValues();
                values.put(BookContract.BookEntry.COLUMN_NAME_TITLE, titleString);
                values.put(BookContract.BookEntry.COLUMN_NAME_AUTHOR, authorString);
                values.put(BookContract.BookEntry.COLUMN_NAME_SPRICE, sPriceString);
                values.put(BookContract.BookEntry.COLUMN_NAME_BPRICE, bPriceString);
                values.put(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_NAME, supplierName);
                values.put(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_CONTACT, supplierContact);
                values.put(BookContract.BookEntry.COLUMN_NAME_CATEGORY, category);
                values.put(BookContract.BookEntry.COLUMN_NAME_QUANTITY, quantityStr);
                CatalogActivity catalogActivity = (CatalogActivity) context;
                int position = cursor.getPosition();
                catalogActivity.editQuantity(context, values, cursor, id);
            }
        });

    }
}

