package com.opweather.util;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class MediaUtil {
    private static MediaUtil instance;

    public class SannerClient implements MediaScannerConnectionClient {
        private MediaScannerConnection mMediaScanConn;
        private List<ScanFile> mScanFiles;

        public SannerClient(Context context, List<ScanFile> scanFiles) {
            mScanFiles = null;
            mScanFiles = scanFiles;
            mMediaScanConn = new MediaScannerConnection(context, this);
        }

        public void connectAndScan() {
            if (mScanFiles != null) {
                mMediaScanConn.connect();
            }
        }

        private void scanNext() {
            if (mScanFiles == null || mScanFiles.isEmpty()) {
                mMediaScanConn.disconnect();
                return;
            }
            ScanFile sf = (ScanFile) mScanFiles.remove(mScanFiles.size() - 1);
            mMediaScanConn.scanFile(sf.filePaths, sf.mineType);
        }

        @Override
        public void onMediaScannerConnected() {
            scanNext();
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            scanNext();
        }
    }

    public static class ScanFile {
        public String filePaths;
        public String mineType;

        public ScanFile(String filePaths, String mineType) {
            filePaths = filePaths;
            mineType = mineType;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            ScanFile file = (ScanFile) obj;
            if (filePaths == null) {
                if (file.filePaths != null) {
                    return false;
                }
            } else if (!filePaths.equals(file.filePaths)) {
                return false;
            }
            return mineType == null ? file.mineType == null : mineType.equals(file.mineType);
        }
    }

    private MediaUtil() {
    }

    public static MediaUtil getInstace() {
        if (instance == null) {
            instance = new MediaUtil();
        }
        return instance;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public Uri getImageContentUri(Context r13_context, java.io.File r14_imageFile) {
        throw new UnsupportedOperationException("Method not decompiled: MediaUtil.getImageContentUri(android.content" +
                ".Context, java.io.File):android.net.Uri");
        /*
        this = this;
        r5 = 0;
        r4 = 1;
        r11 = 0;
        r8 = r14.getAbsolutePath();
        r0 = r13.getContentResolver();
        r1 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        r2 = new java.lang.String[r4];
        r3 = "_id";
        r2[r11] = r3;
        r3 = "_data=? ";
        r4 = new java.lang.String[r4];
        r4[r11] = r8;
        r7 = r0.query(r1, r2, r3, r4, r5);
        if (r7 == 0) goto L_0x0052;
    L_0x001f:
        r0 = r7.moveToFirst();	 Catch:{ all -> 0x0078 }
        if (r0 == 0) goto L_0x0052;
    L_0x0025:
        r0 = "_id";
        r0 = r7.getColumnIndex(r0);	 Catch:{ all -> 0x0078 }
        r9 = r7.getInt(r0);	 Catch:{ all -> 0x0078 }
        r0 = "content://media/external/images/media";
        r6 = android.net.Uri.parse(r0);	 Catch:{ all -> 0x0078 }
        r0 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0078 }
        r0.<init>();	 Catch:{ all -> 0x0078 }
        r1 = "";
        r0 = r0.append(r1);	 Catch:{ all -> 0x0078 }
        r0 = r0.append(r9);	 Catch:{ all -> 0x0078 }
        r0 = r0.toString();	 Catch:{ all -> 0x0078 }
        r5 = android.net.Uri.withAppendedPath(r6, r0);	 Catch:{ all -> 0x0078 }
        if (r7 == 0) goto L_0x0051;
    L_0x004e:
        r7.close();
    L_0x0051:
        return r5;
    L_0x0052:
        r0 = r14.exists();	 Catch:{ all -> 0x0078 }
        if (r0 == 0) goto L_0x0072;
    L_0x0058:
        r10 = new android.content.ContentValues;	 Catch:{ all -> 0x0078 }
        r10.<init>();	 Catch:{ all -> 0x0078 }
        r0 = "_data";
        r10.put(r0, r8);	 Catch:{ all -> 0x0078 }
        r0 = r13.getContentResolver();	 Catch:{ all -> 0x0078 }
        r1 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;	 Catch:{ all -> 0x0078 }
        r5 = r0.insert(r1, r10);	 Catch:{ all -> 0x0078 }
        if (r7 == 0) goto L_0x0051;
    L_0x006e:
        r7.close();
        goto L_0x0051;
    L_0x0072:
        if (r7 == 0) goto L_0x0051;
    L_0x0074:
        r7.close();
        goto L_0x0051;
    L_0x0078:
        r0 = move-exception;
        if (r7 == 0) goto L_0x007e;
    L_0x007b:
        r7.close();
    L_0x007e:
        throw r0;
        */
    }

    public void scanFile(Context context, String filePaths, String mineType) {
        ScanFile scanFile = new ScanFile(filePaths, mineType);
        List<ScanFile> list = new ArrayList<>();
        list.add(scanFile);
        scanFiles(context, list);
    }

    public void scanFiles(Context context, List<ScanFile> filePaths) {
        new SannerClient(context, filePaths).connectAndScan();
    }
}
