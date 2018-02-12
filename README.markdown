# CWAC-Document: A Better DocumentFile

This library contains a fork of the Android Support Library's
`DocumentFile` and related classes. Whereas `DocumentFile`
only supports working with document `Uri` values on API Level 19+,
`DocumentFileCompat` supports other `Uri` values and older Android devices.

## Installation

The artifact for this library is distributed via the CWAC repository,
so you will need to configure that in your module's `build.gradle` file,
along with your `compile` statement:

```groovy
repositories {
    maven {
        url "https://s3.amazonaws.com/repo.commonsware.com"
    }
}

dependencies {
    compile 'com.commonsware.cwac:document:0.3.0'
}
```

## Basic Usage

The core API for `DocumentFileCompat` is
[the same as for `DocumentFile`](https://developer.android.com/reference/android/support/v4/provider/DocumentFile.html).
Just switch your `import` statement to pull in
`com.commonsware.cwac.document.DocumentFileCompat`.

There are three `static` methods on both `DocumentFile` and `DocumentFileCompat`
for creating your initial instance: `fromFile()`, `fromSingleUri()`,
and `fromTreeUri()`. `DocumentFileCompat` extends `fromSingleUri()`
support to older devices and non-document `Uri` values. Also, `fromSingleUri()` on
`DocumentFileCompat`
supports `Uri` values with `file` schemes, where those `Uri` values point
to a single file.

`DocumentFileCompat` also adds a number of convenience methods:

- `openInputStream()` and `openOutputStream()`, to return an `InputStream` and
`OutputStream` on the content, respectively
- `copyTo(OutputStream)`, `copyTo(File)`, and `copyTo(DocumentFileCompat)`,
to copy this object's content to some other location
- `copyFrom(InputStream)`, `copyFrom(File)`, `copyFrom(DocumentFileCompat)`,
and `copyFromAsset(Context, String)` to populate this object's content from
some other source
- `getExtension()` will try to return a usable file extension, either from the
filesystem path or based on the MIME type

[JavaDocs are available](http://javadocs.commonsware.com/cwac/document/index.html)
for your JavaDocing pleasure.

## What Is Supported

In effect, there are four types of `DocumentFileCompat`:

- One created via `fromFile()`
- One created via `fromTreeUri()`
- One created via `fromSingleUri()`, and where the app is running on API Level 19+
*and* the `Uri` is a document `Uri`
- One created via `fromSingleUri()`, where either the app is running on an older
device or the `Uri` is not a document `Uri` (but *is* one that supports
the `OpenableColumns`, such as one retrieved from an activity supporting
`CATEGORY_OPENABLE`, or has a `file` scheme)

The last bullet is the scenario added by `DocumentFileCompat`, over what is
supported by `DocumentFile`.

However, the `DocumentFile`/`DocumentFileCompat` API is not uniformly implemented.
Unsupported methods for a given `DocumentFileCompat` will thrown an
`UnsupportedOperationException`. The following table outlines what is and is
not supported for a given `DocumentFileCompat` type, where "yes" and "no"
indicate if the method is supported, and **bold** values indicated the method
is supported with a fixed return value:

|Method              |`fromFile()`|`fromTreeUri()`|`fromSingleUri()` (19+ document)|`fromSingleUri()` (other)|
|:------------------:|:----------:|:-------------:|:------------------------------:|:-----------------------:|
|`canRead()`         |yes         |yes            |yes                             |yes|
|`canWrite()`        |yes         |yes            |yes                             |yes|
|`createDirectory()` |yes         |yes            |no                              |no|
|`createFile()`      |yes         |yes            |no                              |no|
|`delete()`          |yes         |yes            |yes                             |no|
|`exists()`          |yes         |yes            |yes                             |yes (returns `canRead()`)|
|`getExtension()`    |yes         |yes            |yes                             |yes|
|`getName()`         |yes         |yes            |yes                             |yes|
|`getParentFile()`   |yes         |yes            |yes                             |yes|
|`getType()`         |yes         |yes            |yes                             |yes|
|`getUri()`          |yes         |yes            |yes                             |yes|
|`isDirectory()`     |yes         |yes            |yes                             |**false**|
|`isFile()`          |yes         |yes            |yes                             |**true**|
|`isVirtual()`       |**false**   |yes            |yes                             |**false**|
|`lastModified()`    |yes         |yes            |yes                             |no|
|`length()`          |yes         |yes            |yes                             |yes|
|`listFiles()`       |yes         |yes            |no                              |no|
|`openInputStream()` |yes         |no             |yes                             |yes|
|`openOutputStream()`|yes         |no             |yes                             |yes|
|`renameTo()`        |yes         |yes            |no                              |no|

`findFile()` is supported if `listFiles()` is supported.

## Dependencies

This library depends upon `com.android.support:support-annotations`
from the Android Support Library. Otherwise, it has no external
dependencies.

## Version

The current version is **0.3.0**. However, this uses a current version of
`com.android.support:support-annotations`, which (inexplicably) requires
a `minSdkVersion` of `14`.

If you want to use this library on older devices, either use version `0.2.0`
or override the library's `minSdkVersion`.

## Demo

See the instrumentation test cases for examples of how to use this.

## License

All of the code in this repository is licensed under the
Apache Software License 2.0. Look to the headers of the Java source
files to determine the actual copyright holder, as it is a mix of
the Android Open Source Project and CommonsWare, LLC.

## Questions

If you have questions regarding the use of this code, please post a question
on [Stack Overflow](http://stackoverflow.com/questions/ask) tagged with
`commonsware-cwac` and `android` after [searching to see if there already is an answer](https://stackoverflow.com/search?q=[commonsware-cwac]+camera). Be sure to indicate
what CWAC module you are having issues with, and be sure to include source code 
and stack traces if you are encountering crashes.

If you have encountered what is clearly a bug, or if you have a feature request,
please read [the contribution guidelines](.github/CONTRIBUTING.md), then
post an [issue](https://github.com/commonsguy/cwac-netsecurity/issues).
**Be certain to include complete steps for reproducing the issue.**
If you believe that the issue you have found represents a security bug,
please follow the instructions in
[the contribution guidelines](https://github.com/commonsguy/cwac-netsecurity/blob/master/.github/CONTRIBUTING.md#contributing-security-bug-reports).

You are also welcome to join
[the CommonsWare Community](https://community.commonsware.com/)
and post questions
and ideas to [the CWAC category](https://community.commonsware.com/c/cwac).

Do not ask for help via social media.

## Release Notes

- v0.3.0: updated to current dependencies, raised `minSdkVersion` back to 14
- v0.2.0
    - [dropped `minSdkVersion` to 10](https://github.com/commonsguy/cwac-document/issues/1)
    - [added disk synchronization when copying to files](https://github.com/commonsguy/cwac-document/issues/3)
    - [added `getExtension()`](https://github.com/commonsguy/cwac-document/issues/4)
    - [added `file` support in `fromSingleUri()`](https://github.com/commonsguy/cwac-document/issues/5)
- v0.1.1: initial release
