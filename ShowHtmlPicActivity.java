package com.exercise.xkc.test.todo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.EditText;

import com.exercise.xkc.test.R;

/**
 * Created by xkc on 12/10/15.
 */
public class ShowHtmlPicActivity extends Activity {
    private EditText content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_html_pic);

        initView();

        initEvent();

    }

    private void initEvent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String contentString = bundle.getString("contentString");

        Log.i("xkc","show contentString:"+contentString);

        Html.ImageGetter imageGetter = new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                Drawable drawable = null;
                drawable = Drawable.createFromPath(source);
                drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
                return drawable;
            }
        };

        content.setText(Html.fromHtml(contentString, imageGetter, null));

    }

    private void initView() {
        content = (EditText) findViewById(R.id.content);
    }
}
