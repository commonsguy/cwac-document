/*
 * Copyright (c) 2017 CommonsWare, LLC
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
import android.os.Build;
import androidx.core.content.FileProvider;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.*;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class IOTests {
  private static final String TESTFILE="foo.pdf";
  private static final String TESTFILE2="foo2.pdf";
  private static final String AUTHORITY=
    com.commonsware.cwac.document.test.BuildConfig.APPLICATION_ID+".provider";
  private Context ctxt;

  @Before
  public void init() {
    ctxt= InstrumentationRegistry.getContext();
  }

  @After
  public void cleanup() {
    new File(ctxt.getFilesDir(), TESTFILE).delete();
    new File(ctxt.getFilesDir(), TESTFILE2).delete();
  }

  @Test
  public void testFiles() throws IOException {
    File f=new File(ctxt.getFilesDir(), TESTFILE);

    assertFalse(f.exists());

    DocumentFileCompat df=DocumentFileCompat.fromFile(f);

    assertFalse(df.exists());

    df.copyFromAsset(ctxt, "test.pdf");

    assertTrue(f.exists());
    assertTrue(df.exists());
    assertTrue(isEqual(df.openInputStream(), ctxt.getAssets().open("test.pdf")));
    assertEquals(df.getExtension(), "pdf");
  }

  @Test
  public void testFileUri() throws IOException {
    File f=new File(ctxt.getFilesDir(), TESTFILE);

    assertFalse(f.exists());

    DocumentFileCompat df=DocumentFileCompat.fromSingleUri(ctxt, Uri.fromFile(f));

    assertFalse(df.exists());

    df.copyFromAsset(ctxt, "test.pdf");

    assertTrue(f.exists());
    assertTrue(df.exists());
    assertTrue(isEqual(df.openInputStream(), ctxt.getAssets().open("test.pdf")));
    assertEquals(df.getExtension(), "pdf");
  }

  @Test
  public void testLegacy() throws IOException {
    testBuilder(new DocumentFileCompatBuilder() {
      @Override
      public DocumentFileCompat buildFrom(Context ctxt, Uri uri) {
        return(new SingleLegacyDocumentFile(null, ctxt, uri));
      }
    });
  }

  @Test
  public void testSingle() throws IOException {
    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
      testBuilder(new DocumentFileCompatBuilder() {
        @Override
        public DocumentFileCompat buildFrom(Context ctxt, Uri uri) {
          return(DocumentFileCompat.fromSingleUri(ctxt, uri));
        }
      });
    }
  }

  private void testBuilder(DocumentFileCompatBuilder builder) throws IOException {
    File f=new File(ctxt.getFilesDir(), TESTFILE);

    assertFalse(f.exists());

    Uri test= FileProvider.getUriForFile(ctxt, AUTHORITY, f);
    DocumentFileCompat df=builder.buildFrom(ctxt, test);

    assertFalse(df.exists());

    df.copyFromAsset(ctxt, "test.pdf");

    assertTrue(f.exists());
    assertTrue(df.exists());
    assertTrue(isEqual(df.openInputStream(), ctxt.getAssets().open("test.pdf")));

    assertTrue(df.canRead());
    assertTrue(df.canWrite());
    assertEquals(df.getName(), f.getName());
    assertEquals(df.length(), f.length());
    assertFalse(df.isDirectory());
    assertTrue(df.isFile());
    assertFalse(df.isVirtual());
    assertEquals(df.getType(), "application/pdf");
    assertEquals(df.getExtension(), "pdf");

    File f2=new File(ctxt.getFilesDir(), TESTFILE2);

    assertFalse(f2.exists());

    df.copyTo(f2);
    assertTrue(f2.exists());
    assertTrue(isEqual(df.openInputStream(), new FileInputStream(f2)));

    f2.delete();
    assertFalse(f2.exists());

    Uri test2=FileProvider.getUriForFile(ctxt, AUTHORITY, f2);
    DocumentFileCompat df2=builder.buildFrom(ctxt, test2);

    df.copyTo(df2);
    assertTrue(f2.exists());
    assertTrue(isEqual(df.openInputStream(), new FileInputStream(f2)));

    f2.delete();
    assertFalse(f2.exists());
    df2.copyFrom(df);
    assertTrue(f2.exists());
    assertTrue(isEqual(df.openInputStream(), new FileInputStream(f2)));
  }


  // from http://stackoverflow.com/a/4245881/115145

  private static boolean isEqual(InputStream i1, InputStream i2) throws IOException {
    byte[] buf1 = new byte[64 *1024];
    byte[] buf2 = new byte[64 *1024];
    try {
      DataInputStream d2 = new DataInputStream(i2);
      int len;
      while ((len = i1.read(buf1)) > 0) {
        d2.readFully(buf2,0,len);
        for(int i=0;i<len;i++)
          if(buf1[i] != buf2[i]) return false;
      }
      return d2.read() < 0; // is the end of the second file also.
    } catch(EOFException ioe) {
      return false;
    } finally {
      i1.close();
      i2.close();
    }
  }

  private interface DocumentFileCompatBuilder {
    DocumentFileCompat buildFrom(Context ctxt, Uri uri);
  }
}
