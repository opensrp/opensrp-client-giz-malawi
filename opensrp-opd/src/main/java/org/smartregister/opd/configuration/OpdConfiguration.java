package org.smartregister.opd.configuration;

import android.support.annotation.NonNull;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdConfiguration {


    public static class Builder {

        private String tableName;
        private Builder builder;

        public Builder(@NonNull String tableName) {
            this.tableName = tableName;
        }

        public Builder setTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }
    }
}
