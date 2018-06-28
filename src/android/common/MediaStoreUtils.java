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
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("title", name);
            item.put("path", path.substring(0, path.lastIndexOf(File.separator)));

            catalogs.add(item);
        }

        return catalogs;
    }

    public List<Map<String, Object>> getThumbnails(Context context, String path, boolean isVideo) {
        List<Map<String, Object>> thumbnails = new ArrayList<Map<String, Object>>();

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
        return MediaStore.Images.Thumbnails.getThumbnail(cr, imageIdLong, MediaStore.Images.Thumbnails.MINI_KIND, options);
    }

    public static Map<String, Object> getMedia(Context context, String file) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.MediaColumns.DATA + "=?";
        String[] selectionArgs = new String[] {
                file
        };

        String[] columns = new String[] {
                //MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.SIZE
        };

        Cursor c = context.getContentResolver()
                .query(uri, columns, selection, selectionArgs, null);
        if (c == null) {
            return null;
        }
        Map<String, Object> item = new HashMap<String, Object>();
        if (c.moveToNext()) {
            byte[] data = c.getBlob(c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            item.put("id", c.getLong(c.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)));
            item.put("data", (new String(data)).trim());
            item.put("size", c.getLong(c.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)));
            //item.put("title", c.getString(c.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE)));
            item.put("displayName", c.getString(c.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)));
        }
        c.close();

        return item;
    }

    public static Map<String, Object> getThumbnail(Context context, String id) {
        Uri uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Images.Thumbnails.IMAGE_ID + "=?";
        String[] selectionArgs = new String[] {
                id
        };

        String[] columns = new String[] {
                //MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.SIZE
        };

        Cursor c = context.getContentResolver()
                .query(uri, columns, selection, selectionArgs, null);
        if (c == null) {
            return null;
        }
        Map<String, Object> item = new HashMap<String, Object>();
        if (c.moveToNext()) {
            byte[] data = c.getBlob(c.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
            item.put("imageId", c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.IMAGE_ID)));
            item.put("data", (new String(data)).trim());
            item.put("kind", c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.KIND)));
        }
        c.close();

        return item;
    }

}
