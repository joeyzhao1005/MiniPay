package com.canking.minipay;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

/**
 * Created by changxing on 2017/9/8.
 */

class WeZhi {
    /*package*/
    static void startWeZhi(final Context c, final View view) {
        File dir = c.getExternalFilesDir("pay");
        if (dir != null &&
                !dir.exists() && !dir.mkdirs()) {
            return;
        } else {
            File[] f = new File[0];
            if (dir != null) {
                f = dir.listFiles();
            }
            if (f != null) {
                for (File file : f) {
                    file.delete();
                }
            }
        }

        String fileName = "wechat_pay_qrcode_" + System.currentTimeMillis() + ".png";
        final File file = new File(dir, fileName);
//        if (!file.exists()) {
//            file.delete();
//        }

        new AsyncTask<Context, String, String>() {
            WeakReference<Context> context;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Toast.makeText(c, R.string.wei_loading, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Context... c) {
                context = new WeakReference<>(c[0]);
                snapShot(context.get(), file, view);
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                startWechat(context.get());
            }
        }.execute(c);
    }

    private static void snapShot(Context context, @NonNull File file, @NonNull View view) {
        if (context == null) {
            return;
        }
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(bitmap);
        view.draw(canvas);

        FileOutputStream fos = null;
        boolean isSuccess = false;
        try {
            fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            isSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos);
            fos.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            MiniPayUtils.closeIO(fos);
        }
        if (isSuccess) {
            ContentResolver contentResolver = context.getContentResolver();
            ContentValues values = new ContentValues(4);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            }
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.ORIENTATION, 0);
            }
            values.put(MediaStore.Images.Media.TITLE, context.getString(R.string.donate));
            values.put(MediaStore.Images.Media.DESCRIPTION, context.getString(R.string.donate_qa));
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            values.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000);
            Uri url = null;

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    context.grantUriPermission(context.getPackageName(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                url = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values); //其实质是返回 Image.Meida.DATA中图片路径path的转变而成的uri
                OutputStream imageOut = null;
                if (url != null) {
                    imageOut = contentResolver.openOutputStream(url);
                }
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, imageOut);
                } finally {
                    MiniPayUtils.closeIO(imageOut);
                }

                long id = 0;
                if (url != null) {
                    id = ContentUris.parseId(url);
                }
                MediaStore.Images.Thumbnails.getThumbnail(contentResolver, id, MediaStore.Images.Thumbnails.MINI_KIND, null);//获取缩略图

            } catch (Exception e) {
                if (url != null) {
                    contentResolver.delete(url, null, null);
                }
            }
        }
    }

    /*package*/
    @SuppressLint("WrongConstant")
    private static void startWechat(Context c) {
        if (c == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
        intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
        intent.setFlags(335544320);
        intent.setAction("android.intent.action.VIEW");
        if (MiniPayUtils.isActivityAvailable(c, intent)) {
            c.startActivity(intent);
        } else {
            Toast.makeText(c, "未安装微信～", Toast.LENGTH_SHORT).show();
        }
    }
}
