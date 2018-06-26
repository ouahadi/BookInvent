package rocks.lechick.android.bookinvent.fragments;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import rocks.lechick.android.bookinvent.DetailsActivity;
import rocks.lechick.android.bookinvent.R;
import rocks.lechick.android.bookinvent.adapters.BookCursorAdapter;
import rocks.lechick.android.bookinvent.data.BookContract;
import rocks.lechick.android.bookinvent.data.BookDbHelper;

public class PopScienceFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;
    BookCursorAdapter mCursorAdapter;
    BookDbHelper mDbHelper;
    private String sortOrder = null;
    private String selection;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.listview_fragment,container,false);
        ListView listView = view.findViewById(R.id.listView);
        View emptyView = view.findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        mCursorAdapter = new BookCursorAdapter(getActivity(), null);
        listView.setAdapter(mCursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent i = new Intent(getActivity(), DetailsActivity.class);
                Uri currentBookUri =  ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, id);
                i.setData(currentBookUri);
                Log.v("Biz Frag", "This is the passed on URI" + currentBookUri);
                startActivity(i);
            }
        });

        mDbHelper = new BookDbHelper(getActivity());
        getLoaderManager().initLoader(BOOK_LOADER, null, this);

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
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

        selection = BookContract.BookEntry.COLUMN_NAME_CATEGORY + " = " + BookContract.BookEntry.CATEGORY_POP_SCIENCE;

        return new CursorLoader(getActivity(),
                BookContract.BookEntry.CONTENT_URI, projection, selection, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_category, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.by_stock:
                sortOrder = BookContract.BookEntry.COLUMN_NAME_QUANTITY + " DESC";
                mCursorAdapter.notifyDataSetChanged();
                getLoaderManager().restartLoader(BOOK_LOADER,null, this);
                return true;
            case R.id.by_author:
                sortOrder = BookContract.BookEntry.COLUMN_NAME_AUTHOR + " ASC";
                mCursorAdapter.notifyDataSetChanged();
                getLoaderManager().restartLoader(BOOK_LOADER,null, this);
                return true;
            case R.id.by_title:
                sortOrder = BookContract.BookEntry.COLUMN_NAME_TITLE + " ASC";
                mCursorAdapter.notifyDataSetChanged();
                getLoaderManager().restartLoader(BOOK_LOADER,null, this);
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllBooks() {
        int rowsDeleted = getActivity().getContentResolver().delete(BookContract.BookEntry.CONTENT_URI, selection, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
        getLoaderManager().restartLoader(BOOK_LOADER,null, this);
    }

}