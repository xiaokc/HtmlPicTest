package com.exercise.xkc.test.todo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.exercise.xkc.test.R;

/**
 * Created by xkc on 12/10/15.
 */
public class HtmlPicActivity extends Activity implements View.OnClickListener {
    private EditText content;
    private String contentString = "";
    private Button insert;
    private Button finish;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_pic);

        initView();

        initEvent();


    }

    private void initEvent() {
        insert.setOnClickListener(this);
        finish.setOnClickListener(this);
    }

    private void initView() {
        content = (EditText) findViewById(R.id.content);
        insert = (Button) findViewById(R.id.insert);
        finish = (Button) findViewById(R.id.finish);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.insert:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
                break;
            case R.id.finish:
                Intent contentIntent = new Intent(this, ShowHtmlPicActivity.class);
                Bundle bundle = new Bundle();
                contentString = content.getText().toString();
                bundle.putString("contentString",contentString);
                Log.i("xkc","contentString="+contentString);
                contentIntent.putExtras(bundle);
                startActivity(contentIntent);
                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case 0:
                Uri uri = data.getData();
                String[] projection = {MediaStore.Images.ImageColumns.DATA};
                ContentResolver resolver = getContentResolver();
                Cursor cursor = resolver.query(uri, projection, null, null, MediaStore.Images.Media.DATE_MODIFIED);
                cursor.moveToFirst();

                int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
                String photoPath = cursor.getString(idx);

                String tagPath = "<img src=\""+photoPath+"\"/>";//为图片路径加上<img>标签

                Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
                if (bitmap != null) {
                    SpannableString ss = getBitmapMime(photoPath,tagPath);
                    insertPhotoToEditText(ss);
                }

                cursor.close();

                break;
        }
    }

    /**
     * 将图片插入到EditText中
     * @param ss
     */
    private void insertPhotoToEditText(SpannableString ss) {
        Editable et = content.getText();
        int start = content.getSelectionStart();
        et.insert(start,ss);
        content.setText(et);
        content.setSelection(start + ss.length());
        content.setFocusableInTouchMode(true);
        content.setFocusable(true);
    }

    private SpannableString getBitmapMime(String path,String tagPath) {
        SpannableString ss = new SpannableString(tagPath);//这里使用加了<img>标签的图片路径
        Bitmap bitmap = resizePhoto(path, 480, 800);
        ImageSpan imageSpan = new ImageSpan(this, bitmap);
        ss.setSpan(imageSpan, 0, tagPath.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    /**
     * 将指定路径的图片压缩为目标尺寸
     * @param path
     * @param width
     * @param height
     * @return
     */
    private Bitmap resizePhoto(String path, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = getInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;

        Bitmap bmp = BitmapFactory.decodeFile(path, options);


        return bmp;
    }

    /**
     * 根据目标大小获取压缩比例
     * @param options
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    private int getInSampleSize(BitmapFactory.Options options, int targetWidth, int targetHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;
        if (height > targetHeight || width > targetWidth) {
            final int heightRate = Math.round(height / targetHeight);
            final int widthRate = Math.round(width / targetWidth);

            inSampleSize = heightRate > widthRate ? heightRate : widthRate;
        }
        return inSampleSize;
    }


}
