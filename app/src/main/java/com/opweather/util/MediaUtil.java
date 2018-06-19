package com.opweather.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaUtil {
    private static MediaUtil instance;

    public class SannerClient implements MediaScannerConnectionClient {
        private MediaScannerConnection mMediaScanConn;
        private List<ScanFile> mScanFiles = null;

        public SannerClient(Context context, List<ScanFile> scanFiles) {
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
            ScanFile sf = mScanFiles.remove(mScanFiles.size() - 1);
            mMediaScanConn.scanFile(sf.filePaths, sf.mineType);
        }

        public void onMediaScannerConnected() {
            scanNext();
        }

        public void onScanCompleted(String path, Uri uri) {
            scanNext();
        }
    }

    public static class ScanFile {
        public String filePaths;
        public String mineType;

        public ScanFile(String filePaths, String mineType) {
            this.filePaths = filePaths;
            this.mineType = mineType;
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
            if (mineType == null) {
                if (file.mineType != null) {
                    return false;
                }
                return true;
            } else if (mineType.equals(file.mineType)) {
                return true;
            } else {
                return false;
            }
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

    public Uri getImageContentUri(Context context, File imageFile) {
        Uri uri = null;
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "_data=? " +
                "", new String[]{filePath}, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    uri = Uri.withAppendedPath(Uri.parse("content://media/external/images/media"), "" + cursor.getInt
                            (cursor.getColumnIndex("_id")));
                    return uri;
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (imageFile.exists()) {
            ContentValues values = new ContentValues();
            values.put("_data", filePath);
            uri = context.getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
            if (cursor != null) {
                cursor.close();
            }
        } else if (cursor != null) {
            cursor.close();
        }
        return uri;
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
