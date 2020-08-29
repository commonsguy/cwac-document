/*
 * Copyright (C) 2014 The Android Open Source Project
 * Modifications (C) 2017 CommonsWare, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.commonsware.cwac.document;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

class DocumentsContractApi1 {
    private static final String TAG = "DocumentFileCompat";

    public static String getName(Context context, Uri self) {
        return queryForString(context, self, OpenableColumns.DISPLAY_NAME, null);
    }

    public static String getType(Context context, Uri self) {
        return context.getContentResolver().getType(self);
    }

    public static long length(Context context, Uri self) {
        return queryForLong(context, self, OpenableColumns.SIZE, 0);
    }

    private static String queryForString(Context context, Uri self, String column,
            String defaultValue) {
        final ContentResolver resolver = context.getContentResolver();

        Cursor c = null;
        try {
            c = resolver.query(self, new String[] { column }, null, null, null);
            if (c.moveToFirst() && !c.isNull(0)) {
                return c.getString(0);
            } else {
                return defaultValue;
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed query: " + e);
            return defaultValue;
        } finally {
            if (c != null) c.close();
        }
    }

    private static long queryForLong(Context context, Uri self, String column,
            long defaultValue) {
        final ContentResolver resolver = context.getContentResolver();

        Cursor c = null;
        try {
            c = resolver.query(self, new String[] { column }, null, null, null);
            if (c.moveToFirst() && !c.isNull(0)) {
                return c.getLong(0);
            } else {
                return defaultValue;
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed query: " + e);
            return defaultValue;
        } finally {
            if (c != null) c.close();
        }
    }
}
