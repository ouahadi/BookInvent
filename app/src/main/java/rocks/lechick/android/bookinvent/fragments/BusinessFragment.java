package rocks.lechick.android.bookinvent.fragments;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

import rocks.lechick.android.bookinvent.EditorActivity;
import rocks.lechick.android.bookinvent.R;
import rocks.lechick.android.bookinvent.adapters.BookCursorAdapter;
import rocks.lechick.android.bookinvent.data.BookContract;
import rocks.lechick.android.bookinvent.data.BookDbHelper;

public class BusinessFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;
    BookCursorAdapter mCursorAdapter;
    BookDbHelper mDbHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.listview_fragment,container,false);
        ListView listView = view.findViewById(R.id.listView);
        View emptyView = view.findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        mCursorAdapter = new BookCursorAdapter(getActivity(), null);
        listView.setAdapter(mCursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent i = new Intent(getActivity(), EditorActivity.class);
                Uri currentPetUri =  ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, id);
                i.setData(currentPetUri);
                startActivity(i);
            }
        });

        mDbHelper = new BookDbHelper(getActivity());
        getLoaderManager().initLoader(BOOK_LOADER, null, this);

        return view;
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
                BookContract.BookEntry.COLUMN_NAME_CATEGORY
        };

        String selection = BookContract.BookEntry.COLUMN_NAME_CATEGORY + " = " + BookContract.BookEntry.CATEGORY_BUSINESS;

        return new CursorLoader(getActivity(),
                BookContract.BookEntry.CONTENT_URI, projection, selection, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

}
