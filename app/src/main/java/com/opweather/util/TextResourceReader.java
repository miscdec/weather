package com.opweather.util;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TextResourceReader {
    public static String readTextFileFromResource(Context context, int resId) {
        StringBuilder body = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(resId)));
            while (true) {
                String nextLine = bufferedReader.readLine();
                if (nextLine == null) {
                    return body.toString();
                }
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(" could note open resource: " + resId, e);
        } catch (Resources.NotFoundException e2) {
            throw new RuntimeException(" could note found: " + resId, e2);
        }
    }
}
