package rocks.lechick.android.bookinvent.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BookContract {

    private BookContract() {}

    public static final String CONTENT_AUTHORITY = "rocks.lechick.android.bookinvent";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "books";

    public static class BookEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        public static final String TABLE_NAME = "books";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_BPRICE = "buying_price";
        public static final String COLUMN_NAME_SPRICE = "selling_price";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_NAME_SUPPLIER_CONTACT = "supplier_contact";
        public static final String COLUMN_NAME_SHIPMENT_STATUS = "shipment_status";
        public static final String COLUMN_NAME_EXPECTED_DELIVERY = "expected_delivery";
        public static final String COLUMN_NAME_CATEGORY = "category";

        public static final int DELIVERY_STATUS_ORDERED = 1;
        public static final int DELIVERY_STATUS_ON_ITS_WAY = 2;
        public static final int DELIVERY_STATUS_DELIVERED = 3;
        public static final int DELIVERY_STATUS_IN_STOCK = 0;

        public static final int CATEGORY_FICTION = 0;
        public static final int CATEGORY_BUSINESS = 1;
        public static final int CATEGORY_POP_SCIENCE = 2;
        public static final int CATEGORY_OTHER = 3;

        public static boolean isValidCategory(int category){
            if (category == CATEGORY_FICTION || category == CATEGORY_BUSINESS ||
                    category == CATEGORY_POP_SCIENCE || category == CATEGORY_OTHER) {
                return true;
            }
            return false;
        }


        public static boolean isValidMail(String email) {
            boolean check;
            Pattern p;
            Matcher m;

            String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

            p = Pattern.compile(EMAIL_STRING);

            m = p.matcher(email);
            check = m.matches();

            return check;
        }

        public static boolean isValidMobile(String phone) {
            boolean check=false;
            if(!Pattern.matches("[a-zA-Z]+", phone)) {
                if(phone.length() < 6 || phone.length() > 13) {
                    // if(phone.length() != 10) {
                    check = false;
                } else {
                    check = true;
                }
            } else {
                check=false;
            }
            return check;
        }

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

    }
}

