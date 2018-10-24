package com.sawatruck.loader.utils;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by royal on 8/19/2017.
 */


public class StringUtil {
    /**
     *  Escape Carriage Return
     * @return
     */
    public static String escapeString(String text){
        text = text.replace("\r", "");
        text = text.replace("\n", "");
        text = text.replaceAll("\\\\", "");
        return text;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getStringFromInputStream(InputStream inputStream){
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return textBuilder.toString();
    }
}

