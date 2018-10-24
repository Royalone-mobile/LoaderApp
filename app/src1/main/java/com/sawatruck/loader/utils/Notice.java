package com.sawatruck.loader.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.R;


/**
 * Created by royalone on 2016-12-23.
 */

public class Notice {
    /**
     * Show Text
     * @param content
     */
    public static void show(String content) {
        try {
            View v = View.inflate(BaseApplication.getContext(), R.layout.layout_toast, null);
            ((TextView) v.findViewById(R.id.text)).setText(content);
            Toast t = new Toast(BaseApplication.getContext());
            t.setView(v);
            t.setDuration(Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP, 0, 0);
            t.show();
        }catch (Exception e ){
            e.printStackTrace();
        }
    }
    public static void show(int content) {
        try {
            show(BaseApplication.getContext().getString(content));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Long Display
     * @param context
     * @param content
     */
    public static void showL(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }

}
