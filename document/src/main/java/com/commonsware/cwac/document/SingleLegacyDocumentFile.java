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

import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class SingleLegacyDocumentFile extends DocumentFileCompat {
    private Context mContext;
    private Uri mUri;

    SingleLegacyDocumentFile(DocumentFileCompat parent, Context context, Uri uri) {
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
        return DocumentsContractApi1.getName(mContext, mUri);
    }

    @Override
    public String getType() {
        return DocumentsContractApi1.getType(mContext, mUri);
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public boolean isVirtual() {
        return false;
    }

    @Override
    public long lastModified() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long length() {
        return DocumentsContractApi1.length(mContext, mUri);
    }

    @Override
    public boolean canRead() {
      boolean result=false;

      try {
        InputStream in=openInputStream();
        in.close();
        result=true;
      }
      catch (IOException e) {
        // OK
      }

      return result;
    }

    @Override
    public boolean canWrite() {
      boolean result=false;

      try {
        OutputStream in=openOutputStream();
        in.close();
        result=true;
      }
      catch (IOException e) {
        // OK
      }

      return result;
    }

    @Override
    public boolean delete() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean exists() {
        return canRead();
    }

    @Override
    public DocumentFileCompat[] listFiles() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean renameTo(String displayName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream openInputStream()
      throws FileNotFoundException {
        return mContext.getContentResolver().openInputStream(getUri());
    }

    @Override
    public OutputStream openOutputStream()
      throws FileNotFoundException {
        return mContext.getContentResolver().openOutputStream(getUri());
    }

    @Override
    public String getExtension() {
        return(MimeTypeMap.getSingleton().getExtensionFromMimeType(getType()));
    }
}
