package com.example.appasi;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class CacheUtils {
    public static File saveUriToCache(Context context, Uri uri, String fileName) throws Exception {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            throw new Exception("No se pudo abrir el Uri");
        }
        File cacheFile = new File(context.getCacheDir(), fileName);
        try (OutputStream outputStream = new FileOutputStream(cacheFile)) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            inputStream.close();
        }
        return cacheFile;
    }
}
