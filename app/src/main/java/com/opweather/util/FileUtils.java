package com.opweather.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {
    private static final int BUFFER = 8192;

    public static String readTextFile(File file) throws IOException {
        InputStream is = null;
        try {
            InputStream is2 = new FileInputStream(file);
            try {
                String text = readTextInputStream(is2);
                if (is2 != null) {
                    is2.close();
                }
                return text;
            } catch (Throwable th) {
                Throwable th2 = th;
                is = is2;
                if (is != null) {
                    is.close();
                }
                throw th2;
            }
        } catch (Throwable th3) {
            if (is != null) {
                is.close();
            }
            try {
                throw th3;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return "";
    }

    public static String readTextInputStream(InputStream is) throws IOException {
        StringBuilder strbuffer = new StringBuilder();
        BufferedReader reader = null;
        try {
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(is));
            while (true) {
                try {
                    String line = reader2.readLine();
                    if (line == null) {
                        break;
                    }
                    strbuffer.append(line).append("\r\n");
                } catch (Throwable th) {
                    Throwable th2 = th;
                    reader = reader2;
                }
            }
            if (reader2 != null) {
                reader2.close();
            }
            return strbuffer.toString();
        } catch (Throwable th3) {
            if (reader != null) {
                reader.close();
            }
            throw th3;
        }
    }

    public static void writeTextFile(File file, String str) throws Throwable {
        DataOutputStream out = null;
        try {
            DataOutputStream out2 = new DataOutputStream(new FileOutputStream(file));
            try {
                out2.write(str.getBytes());
                if (out2 != null) {
                    out2.close();
                }
            } catch (Throwable th) {
                Throwable th2 = th;
                out = out2;
                if (out != null) {
                    out.close();
                }
                throw th2;
            }
        } catch (Throwable th3) {
            if (out != null) {
                out.close();
            }
            throw th3;
        }
    }

    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            BufferedInputStream inBuff2 = new BufferedInputStream(new FileInputStream(sourceFile));
            BufferedOutputStream outBuff2 = new BufferedOutputStream(new FileOutputStream(targetFile));
            byte[] buffer = new byte[8192];
            while (true) {
                int length = inBuff2.read(buffer);
                if (length == -1) {
                    break;
                }
                outBuff2.write(buffer, 0, length);
            }
            outBuff2.flush();
            inBuff2.close();
            outBuff2.close();
        } catch (Throwable th) {
            inBuff.close();
            outBuff.close();
            throw th;
        }
    }
}
