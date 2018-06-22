package rocks.lechick.android.bookinvent;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import rocks.lechick.android.bookinvent.data.BookContract;
import rocks.lechick.android.bookinvent.data.BookDbHelper;

public class EditorActivity extends AppCompatActivity {

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

    private BookDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mAuthor = (EditText) findViewById(R.id.author_text_view);
        mTitle = (EditText) findViewById(R.id.title_text_view);
        mBuyingPrice = (EditText) findViewById(R.id.buying_price_text_view);
        mSellingPrice = (EditText) findViewById(R.id.selling_price_text_view);
        mQuantity = (EditText) findViewById(R.id.quantity_text_view);
        mSupplierName = (EditText) findViewById(R.id.supplier_name_text_view);
        mSupplierContact = (EditText) findViewById(R.id.supplier_contact_text_view);
        mStatusSpinner = (Spinner) findViewById(R.id.spinner_delivery_status);
        mCategorySpinner = (Spinner) findViewById(R.id.spinner_category);

        mDbHelper = new BookDbHelper(this);
        setupSpinner();
        setupCategorySpinner();

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insertBook();
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

        if (newRowId == -1){
            Toast.makeText(this, "We can't be saving books like this!", Toast.LENGTH_LONG);
        }
        else {
            Toast.makeText(this, "We've assigned " + title + " ID " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

}
