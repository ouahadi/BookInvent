package rocks.lechick.android.bookinvent.adapters;

import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import rocks.lechick.android.bookinvent.R;
import rocks.lechick.android.bookinvent.data.BookContract;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title = (TextView) view.findViewById(R.id.list_title_textview);
        TextView author = (TextView) view.findViewById(R.id.list_author_textview);
        TextView price = (TextView) view.findViewById(R.id.list_selling_price_text_view);
        TextView quantity = (TextView) view.findViewById(R.id.list_quantity_text_view);
        ImageView bookImage = (ImageView) view.findViewById(R.id.list_book_imageview);

        String titleString = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_TITLE));
        String authorString = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_AUTHOR));
        String priceString = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_SPRICE));
        String quantityString = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_QUANTITY));
        int category = cursor.getInt(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_NAME_CATEGORY));

        title.setText(titleString);
        author.setText(authorString);
        price.setText(priceString);
        quantity.setText(quantityString);

        if (category == BookContract.BookEntry.CATEGORY_FICTION){
            bookImage.setImageResource(R.drawable.orange_orange_book);
        }
        if (category == BookContract.BookEntry.CATEGORY_BUSINESS){
            bookImage.setImageResource(R.drawable.book_thumbnail);
        }
        if (category == BookContract.BookEntry.CATEGORY_POP_SCIENCE){
            bookImage.setImageResource(R.drawable.yellow_orange_book);
        }
        if (category == BookContract.BookEntry.CATEGORY_OTHER) {
            bookImage.setImageResource(R.drawable.turquoise_book);
        }
    }
}
