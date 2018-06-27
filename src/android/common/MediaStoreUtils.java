package com.gx.filechooser.common;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaStoreUtils {


    public List<Map<String, Object>> getMedias() {
        return null;
    }

    public static List<Map<String, Object>> getCatalogs(Context context) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] columns = new String[] {
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };
        String groupBy = " 1=1 ) group by (" + MediaStore.Images.Media.BUCKET_DISPLAY_NAME;

        Cursor cursor = context.getContentResolver().query(uri, columns, groupBy, null, null);

        List<Map<String, Object>> catalogs = new ArrayList<Map<String, Object>>();
        while (cursor.moveToNext()) {
            String path = new String(cursor.getBlob(0));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
            Map<String, Object> item = new HashMap<>();
            item.put("title", name);
            item.put("path", path.substring(0, path.lastIndexOf(File.separator)));

            catalogs.add(item);
        }

        return catalogs;
    }

    public List<Map<String, Object>> getThumbnails(Context context, String path, boolean isVideo) {
        List<Map<String, Object>> thumbnails = new ArrayList<>();

        String volumeName = "external";
        Uri uri = isVideo ? MediaStore.Video.Media.getContentUri(volumeName) : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                .getContentUri(volumeName);
        String selection = MediaStore.MediaColumns.DATA + "=?";
        String[] selectionArgs = new String[] {
                path
        };

        String[] columns = new String[] {
                MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA
        };

        Cursor c = context.getContentResolver()
                .query(uri, columns, selection, selectionArgs, null);
        if (c == null) {
            return thumbnails;
        }
        Map<String, Object> item = new HashMap<String, Object>();
        if (c.moveToNext()) {
            long id = c.getLong(0);
            byte[] data = c.getBlob(1);
            // Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            item.put("image_id", id);
            item.put("image_image", (new String(data)).trim());
            thumbnails.add(item);
        }
        c.close();

        return thumbnails;
    }

    public static Bitmap getThumbnailsFromId(ContentResolver cr, String id) {
        if (id == null || id.equals(""))
            return null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        long imageIdLong = Long.parseLong(id);
        //via imageid get the bimap type thumbnail in thumbnail table.
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, imageIdLong, MediaStore.Images.Thumbnails.MINI_KIND, options);

        return bitmap;
    }

}
