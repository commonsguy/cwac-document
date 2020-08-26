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

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.webkit.MimeTypeMap;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import androidx.annotation.RequiresApi;

@RequiresApi(19)
@TargetApi(19)
class SingleDocumentFile extends DocumentFileCompat {
    private Context mContext;
    private Uri mUri;

    SingleDocumentFile(DocumentFileCompat parent, Context context, Uri uri) {
        super(parent);
        mContext = context;
        mUri = uri;
    }

    @Override
    public DocumentFileCompat createFile(String mimeType, String displayName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DocumentFileCompat createDirectory(String displayName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uri getUri() {
        return mUri;
    }

    @Override
    public String getName() {
        return DocumentsContractApi19.getName(mContext, mUri);
    }

    @Override
    public String getType() {
        return DocumentsContractApi19.getType(mContext, mUri);
    }

    @Override
    public boolean isDirectory() {
        return DocumentsContractApi19.isDirectory(mContext, mUri);
    }

    @Override
    public boolean isFile() {
        return DocumentsContractApi19.isFile(mContext, mUri);
    }

    @Override
    public boolean isVirtual() {
        return DocumentsContractApi19.isVirtual(mContext, mUri);
    }

    @Override
    public long lastModified() {
        return DocumentsContractApi19.lastModified(mContext, mUri);
    }

    @Override
    public long length() {
        return DocumentsContractApi19.length(mContext, mUri);
    }

    @Override
    public boolean canRead() {
        return DocumentsContractApi19.canRead(mContext, mUri);
    }

    @Override
    public boolean canWrite() {
        return DocumentsContractApi19.canWrite(mContext, mUri);
    }

    @Override
    public boolean delete() throws FileNotFoundException {
        return DocumentsContractApi19.delete(mContext, mUri);
    }

    @Override
    public boolean exists() {
        return DocumentsContractApi19.exists(mContext, mUri);
    }

    @Override
    public DocumentFileCompat[] listFiles() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean renameTo(String displayName) throws FileNotFoundException {
        if (Build.VERSION.SDK_INT >= 21) {
            final Uri result =
              DocumentsContractApi21.renameTo(mContext, mUri, displayName);
            if (result != null) {
                mUri = result;
                return true;
            }
            else {
                return false;
            }
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public InputStream openInputStream() throws FileNotFoundException {
        return mContext.getContentResolver().openInputStream(getUri());
    }

    @Override
    public OutputStream openOutputStream() throws FileNotFoundException {
        return mContext.getContentResolver().openOutputStream(getUri());
    }

    @Override
    public String getExtension() {
        return(MimeTypeMap.getSingleton().getExtensionFromMimeType(getType()));
    }
}
