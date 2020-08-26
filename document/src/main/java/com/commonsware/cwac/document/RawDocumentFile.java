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

import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

class RawDocumentFile extends DocumentFileCompat {
    private File mFile;

    RawDocumentFile(DocumentFileCompat parent, File file) {
        super(parent);
        mFile = file;
    }

    @Override
    public DocumentFileCompat createFile(String mimeType, String displayName) {
        // Tack on extension when valid MIME type provided
        final String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        if (extension != null) {
            displayName += "." + extension;
        }
        final File target = new File(mFile, displayName);
        try {
            target.createNewFile();
            return new RawDocumentFile(this, target);
        } catch (IOException e) {
            Log.w(TAG, "Failed to createFile: " + e);
            return null;
        }
    }

    @Override
    public DocumentFileCompat createDirectory(String displayName) {
        final File target = new File(mFile, displayName);
        if (target.isDirectory() || target.mkdir()) {
            return new RawDocumentFile(this, target);
        } else {
            return null;
        }
    }

    @Override
    public Uri getUri() {
        return Uri.fromFile(mFile);
    }

    @Override
    public String getName() {
        return mFile.getName();
    }

    @Override
    public String getType() {
        if (mFile.isDirectory()) {
            return null;
        } else {
            return getTypeForName(mFile.getName());
        }
    }

    @Override
    public boolean isDirectory() {
        return mFile.isDirectory();
    }

    @Override
    public boolean isFile() {
        return mFile.isFile();
    }

    @Override
    public boolean isVirtual() {
        return false;
    }

    @Override
    public long lastModified() {
        return mFile.lastModified();
    }

    @Override
    public long length() {
        return mFile.length();
    }

    @Override
    public boolean canRead() {
        return mFile.canRead();
    }

    @Override
    public boolean canWrite() {
        return mFile.canWrite();
    }

    @Override
    public boolean delete() {
        deleteContents(mFile);
        return mFile.delete();
    }

    @Override
    public boolean exists() {
        return mFile.exists();
    }

    @Override
    public DocumentFileCompat[] listFiles() {
        final ArrayList<DocumentFileCompat> results = new ArrayList<DocumentFileCompat>();
        final File[] files = mFile.listFiles();
        if (files != null) {
            for (File file : files) {
                results.add(new RawDocumentFile(this, file));
            }
        }
        return results.toArray(new DocumentFileCompat[results.size()]);
    }

    @Override
    public boolean renameTo(String displayName) {
        final File target = new File(mFile.getParentFile(), displayName);
        if (mFile.renameTo(target)) {
            mFile = target;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public InputStream openInputStream() throws FileNotFoundException {
        return new FileInputStream(mFile);
    }

    @Override
    public OutputStream openOutputStream() throws FileNotFoundException {
        return new FileOutputStream(mFile);
    }

    // inspired by http://stackoverflow.com/a/3571239/115145

    @Override
    public String getExtension() {
        String name=mFile.getName();
        String extension="";

        int i=name.lastIndexOf('.');

        if (i > 0) {
            extension=name.substring(i+1);
        }

        return extension;
    }

    private static String getTypeForName(String name) {
        final int lastDot = name.lastIndexOf('.');
        if (lastDot >= 0) {
            final String extension = name.substring(lastDot + 1).toLowerCase();
            final String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            if (mime != null) {
                return mime;
            }
        }

        return "application/octet-stream";
    }

    private static boolean deleteContents(File dir) {
        File[] files = dir.listFiles();
        boolean success = true;
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    success &= deleteContents(file);
                }
                if (!file.delete()) {
                    Log.w(TAG, "Failed to delete " + file);
                    success = false;
                }
            }
        }
        return success;
    }
}
