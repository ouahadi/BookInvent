package rocks.lechick.android.bookinvent;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import rocks.lechick.android.bookinvent.data.BookContract;
import rocks.lechick.android.bookinvent.data.BookDbHelper;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mAuthor;
    private EditText mTitle;
    private EditText mBuyingPrice;
    private EditText mSellingPrice;
    private EditText mQuantity;
    private EditText mSupplierName;
    private EditText mSupplierContact;
    private Spinner mStatusSpinner;
    private int mDeliveryStatus = 0;
    private Spinner mCategorySpinner;
    private int mCategory = 3;
    private Uri mCurrentBookUri;

    private BookDbHelper mDbHelper;
    private static final int EXISTING_BOOK_LOADER = 0;
    private boolean mBookHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri == null) {
            setTitle("Add a Book");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Book");
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }


        mAuthor = (EditText) findViewById(R.id.author_text_view);
        mTitle = (EditText) findViewById(R.id.title_text_view);
        mBuyingPrice = (EditText) findViewById(R.id.buying_price_text_view);
        mSellingPrice = (EditText) findViewById(R.id.selling_price_text_view);
        mQuantity = (EditText) findViewById(R.id.quantity_text_view);
        mSupplierName = (EditText) findViewById(R.id.supplier_name_text_view);
        mSupplierContact = (EditText) findViewById(R.id.supplier_contact_text_view);
        mStatusSpinner = (Spinner) findViewById(R.id.spinner_delivery_status);
        mCategorySpinner = (Spinner) findViewById(R.id.spinner_category);

        mAuthor.setOnTouchListener(mTouchListener);
        mTitle.setOnTouchListener(mTouchListener);
        mBuyingPrice.setOnTouchListener(mTouchListener);
        mSellingPrice.setOnTouchListener(mTouchListener);
        mQuantity.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mSupplierContact.setOnTouchListener(mTouchListener);
        mStatusSpinner.setOnTouchListener(mTouchListener);
        mCategorySpinner.setOnTouchListener(mTouchListener);


        mDbHelper = new BookDbHelper(this);
        setupSpinner();
        setupCategorySpinner();

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mCurrentBookUri == null) {
                    insertBook();
                } else {
                    saveBook();
                }

                Intent i = new Intent(EditorActivity.this, CatalogActivity.class);
                startActivity(i);
            }
        });
    }

    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter deliverySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.delivery_status_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        deliverySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mStatusSpinner.setAdapter(deliverySpinnerAdapter);

        // Set the integer mSelected to the constant values
        mStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.delivery_ordered))) {
                        mDeliveryStatus = BookContract.BookEntry.DELIVERY_STATUS_ORDERED;
                    } else if (selection.equals(getString(R.string.delivery_shipped))) {
                        mDeliveryStatus = BookContract.BookEntry.DELIVERY_STATUS_ON_ITS_WAY;
                    } else if (selection.equals(getString(R.string.delivery_delivered))) {
                        mDeliveryStatus = BookContract.BookEntry.DELIVERY_STATUS_DELIVERED;
                    } else {
                        mDeliveryStatus = BookContract.BookEntry.DELIVERY_STATUS_IN_STOCK;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mDeliveryStatus = 0;
            }
        });
    }

    private void setupCategorySpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter deliverySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.category_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        deliverySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mCategorySpinner.setAdapter(deliverySpinnerAdapter);

        // Set the integer mSelected to the constant values
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.fiction))) {
                        mCategory = BookContract.BookEntry.CATEGORY_FICTION;
                    } else if (selection.equals(getString(R.string.business))) {
                        mCategory = BookContract.BookEntry.CATEGORY_BUSINESS;
                    } else if (selection.equals(getString(R.string.pop_sci))) {
                        mCategory = BookContract.BookEntry.CATEGORY_POP_SCIENCE;
                    } else {
                        mCategory = BookContract.BookEntry.CATEGORY_OTHER;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCategory = 3;
            }
        });
    }

    private void insertBook() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        String author = mAuthor.getText().toString().trim();
        String title = mTitle.getText().toString().trim();
        double buyPrice = Double.parseDouble(mBuyingPrice.getText().toString().trim());
        double sellPrice = Double.parseDouble(mSellingPrice.getText().toString().trim());
        int quantity = Integer.parseInt(mQuantity.getText().toString().trim());
        String supplierName = mSupplierName.getText().toString().trim();
        String supplierContact = mSupplierContact.getText().toString().trim();
        int deliveryStatus = mDeliveryStatus;
        int category = mCategory;

        values.put(BookContract.BookEntry.COLUMN_NAME_AUTHOR, author);
        values.put(BookContract.BookEntry.COLUMN_NAME_TITLE, title);
        values.put(BookContract.BookEntry.COLUMN_NAME_BPRICE, buyPrice);
        values.put(BookContract.BookEntry.COLUMN_NAME_SPRICE, sellPrice);
        values.put(BookContract.BookEntry.COLUMN_NAME_QUANTITY, quantity);
        values.put(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_NAME, supplierName);
        values.put(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_CONTACT, supplierContact);
        values.put(BookContract.BookEntry.COLUMN_NAME_SHIPMENT_STATUS, deliveryStatus);
        values.put(BookContract.BookEntry.COLUMN_NAME_CATEGORY, category);

        long newRowId = db.insert(BookContract.BookEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Toast.makeText(this, "We can't be saving books like this!", Toast.LENGTH_LONG);
        } else {
            Toast.makeText(this, "We've assigned " + title + " ID " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String author = mAuthor.getText().toString().trim();
        String title = mTitle.getText().toString().trim();
        String buyPrice = mBuyingPrice.getText().toString().trim();
        String sellPrice = mSellingPrice.getText().toString().trim();
        String quantity = mQuantity.getText().toString().trim();
        String supplierName = mSupplierName.getText().toString().trim();
        String supplierContact = mSupplierContact.getText().toString().trim();
        int deliveryStatus = mDeliveryStatus;
        int category = mCategory;


        // Check if this is supposed to be a new pet
        // and check if all the fields in the editor are blank
        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(author) && TextUtils.isEmpty(title) &&
                TextUtils.isEmpty(buyPrice) && TextUtils.isEmpty(sellPrice) &&
                TextUtils.isEmpty(quantity) && TextUtils.isEmpty(supplierName) &&
                TextUtils.isEmpty(supplierContact) && deliveryStatus == BookContract.BookEntry.DELIVERY_STATUS_IN_STOCK &&
                category == BookContract.BookEntry.CATEGORY_OTHER) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }
        ContentValues values = new ContentValues();

        values.put(BookContract.BookEntry.COLUMN_NAME_AUTHOR, author);
        values.put(BookContract.BookEntry.COLUMN_NAME_TITLE, title);
        values.put(BookContract.BookEntry.COLUMN_NAME_BPRICE, buyPrice);
        values.put(BookContract.BookEntry.COLUMN_NAME_SPRICE, sellPrice);
        values.put(BookContract.BookEntry.COLUMN_NAME_QUANTITY, quantity);
        values.put(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_NAME, supplierName);
        values.put(BookContract.BookEntry.COLUMN_NAME_SUPPLIER_CONTACT, supplierContact);
        values.put(BookContract.BookEntry.COLUMN_NAME_SHIPMENT_STATUS, deliveryStatus);
        values.put(BookContract.BookEntry.COLUMN_NAME_CATEGORY, category);


        // Determine if this is a new or existing pet by checking if mCurrentPetUri is null or not
        if (mCurrentBookUri == null) {
            // This is a NEW pet, so insert a new pet into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_LONG).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_book_successful) + title,
                        Toast.LENGTH_LONG).show();
            }
        } else {
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_LONG).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_book_successful) + title,
                        Toast.LENGTH_LONG).show();
            }
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
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
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
            String supplierContact = cursor.getString(supplierConColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            int shipmentStatus = cursor.getInt(shipmentStatusColumnIndex);
            int category = cursor.getInt(categoryColumnIndex);

            // Update the views on the screen with the values from the database
            mTitle.setText(title);
            mAuthor.setText(author);
            mQuantity.setText(Integer.toString(quantity));
            mSellingPrice.setText(Double.toString(sprice));
            mBuyingPrice.setText(Double.toString(bprice));
            mSupplierContact.setText(supplierContact);
            mSupplierName.setText(supplierName);
            mCategorySpinner.setSelection(category);

            switch (shipmentStatus) {
                case BookContract.BookEntry.DELIVERY_STATUS_DELIVERED:
                    mStatusSpinner.setSelection(2);
                case BookContract.BookEntry.DELIVERY_STATUS_IN_STOCK:
                    mStatusSpinner.setSelection(3);
                case BookContract.BookEntry.DELIVERY_STATUS_ON_ITS_WAY:
                    mStatusSpinner.setSelection(1);
                case BookContract.BookEntry.DELIVERY_STATUS_ORDERED:
                    mStatusSpinner.setSelection(0);
            }
            //switch (category) {
              //  case BookContract.BookEntry.CATEGORY_FICTION:
              //      mCategorySpinner.setSelection(0);
              //  case BookContract.BookEntry.CATEGORY_BUSINESS:
              //      mCategorySpinner.setSelection(1);
              //  case BookContract.BookEntry.CATEGORY_POP_SCIENCE:
              //      mCategorySpinner.setSelection(2);
              //  case BookContract.BookEntry.CATEGORY_OTHER:
              //      mCategorySpinner.setSelection(3);
            //}
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTitle.setText("");
        mAuthor.setText("");
        mQuantity.setText("");
        mSellingPrice.setText("");
        mBuyingPrice.setText("");
        mSupplierContact.setText("");
        mSupplierName.setText("");
        mStatusSpinner.setSelection(0);
        mCategorySpinner.setSelection(3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveBook();
                //Goes back to CatalogActivity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }



        private void showUnsavedChangesDialog (
                DialogInterface.OnClickListener discardButtonClickListener){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.unsaved_changes_dialog_msg);
            builder.setPositiveButton(R.string.discard, discardButtonClickListener);
            builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }


    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        // TODO: Implement this method
        if (mCurrentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }

            finish();
        }
    }

}
