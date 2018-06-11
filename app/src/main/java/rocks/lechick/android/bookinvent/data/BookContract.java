package rocks.lechick.android.bookinvent.data;

import android.provider.BaseColumns;

public final class BookContract {

    public static class BookEntry implements BaseColumns {

        public static final String TABLE_NAME = "books";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_BPRICE = "buying price";
        public static final String COLUMN_NAME_SPRICE = "selling price";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_SUPPLIER_NAME = "supplier name";
        public static final String COLUMN_NAME_SUPPLIER_CONTACT = "supplier contact";
        public static final String COLUMN_NAME_SHIPMENT_STATUS = "shipment status";
        public static final String COLUMN_NAME_EXPECTED_DELIVERY = "expected delivery";

        public static final int DELIVERY_STATUS_ORDERED = 1;
        public static final int DELIVERY_STATUS_ON_ITS_WAY = 2;
        public static final int DELIVERY_STATUS_DELIVERED = 3;
        public static final int DELIVERY_STATUS_IN_STOCK = 0;



    }
}

