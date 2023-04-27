package com.example.ucanhealth.sqlite;

import android.provider.BaseColumns;

public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_EXERCISE = "exercise";
        public static final String COLUMN_REPITITION = "repitition";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_DATE = "date";
    }

}
