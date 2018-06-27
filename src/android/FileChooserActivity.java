package com.gx.filechooser;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.gx.filechooser.common.FileUtils;
import com.gx.filechooser.common.MediaStoreUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileChooserActivity extends Activity {

    private TextView titleView;

    private int EXPLORER_TYPE = 1;

    private String ROOT_LABEL_IMAGE = "图片";
    private String ROOT_LABEL_FILE = "Files";
    private String ROOT_PATH_IAMGE = "/images";
    private String ROOT_PATH_FILE = "/files";

    private String title = "图片";
    private String path = "/"; // Environment.getExternalStorageDirectory().getAbsolutePath();
    private String relativePath = "/images";

    private List<Map<String, Object>> listItems;
    private SimpleAdapter adapter;

    private List<String> selectedFiles = new ArrayList<String>();

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.getApplication().getResources().getIdentifier(
                "activity_filechooser", "layout", getApplication().getPackageName()));

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        // List<Map<String, String>> pices = com.gx.filechooser.common.ThumbnailUtils.getAllPictures(this);

        this.titleView = (TextView) this.findViewById("title");
        this.titleView.setText(this.ROOT_LABEL_IMAGE);

        final ListView listView = (ListView) this.findViewById("list_view");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onListViewItemClickHandler(view, i, l);
            }
        });

        String[] from = { "image", "title" };

        final int imageView = this.findIdentifier("imageView", "id");
        int textView = this.findIdentifier("textView2", "id");
        int itemLayout = this.findIdentifier("listview_item", "layout");

        int[] to = { imageView, textView };

        this.getVolumes();

        this.listItems = this.getImageCatalogs();
//        String _path = this.listItems.get(0).get("path").toString();
//        this.relativePath += _path.substring(_path.lastIndexOf(File.separator) + 1);

        this.adapter = new SimpleAdapter(
                this, this.listItems, itemLayout, from, to);
        listView.setAdapter(this.adapter);
        this.adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object o, String s) {
            if (view instanceof ImageView && o instanceof Bitmap) {
                ImageView imageView1 = (ImageView) view;
                Bitmap bitmap = (Bitmap) o;
                //imageView1.setImageURI(Uri.fromFile(new File(o.toString())));
                imageView1.setImageBitmap(bitmap);
                return true;
            }
            return false;
            }
        });

    }

    private void onListViewItemClickHandler(View view, int i, long l) {
        Map<String, Object> selectedItem = this.listItems.get(i);
        String name = selectedItem.get("title").toString();
        String selectFile = selectedItem.get("path").toString();
        if ((new File(selectFile)).isDirectory()) {
            this.path = selectFile;
            this.title = name;
            this.relativePath += this.relativePath.endsWith(File.separator) ? name : File.separator + name;

            this.titleView.setText(name);
            clearSelectedFiles();
            loadItems(selectFile);
            return;
        }
        addOrRemoveFile(view, selectFile);
    }

    private void clearSelectedFiles() {
        this.selectedFiles.clear();
        Button okButton = (Button) findViewById("btnOK");
        okButton.setText("确定");
        okButton.setEnabled(false);
        okButton.setTextColor(Color.parseColor("#C3C3C3"));
    }

    private void addOrRemoveFile(View view, String selectFile) {
        if (selectedFiles.contains(selectFile)) {
            selectedFiles.remove(selectFile);
            view.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } else {
            selectedFiles.add(selectFile);
            view.setBackgroundColor(Color.parseColor("#ECEEF0"));
        }

        if (this.selectedFiles.isEmpty()) {
            this.clearSelectedFiles();
        } else {
            Button okButton = (Button) findViewById("btnOK");
            okButton.setText("确定(" + this.selectedFiles.size() + ")");
            okButton.setEnabled(true);
            okButton.setTextColor(Color.WHITE);
        }
    }

    private void loadItems(String path) {
        if (!path.equals("/")) {
            path += "/";
        }
        List<Map<String, Object>> data = getListItems(path);
        listItems.clear();
        if (data != null && data.size() > 0) {
            listItems.addAll(data);
        }
        adapter.notifyDataSetChanged();
    }

    public void onBack(View view) {
        if (this.relativePath.equals(this.ROOT_PATH_IAMGE) || this.relativePath.equals(this.ROOT_PATH_FILE)) {
            this.finish();
            return;
        }

        if (this.EXPLORER_TYPE == 1) {
            this.titleView.setText(this.ROOT_LABEL_IMAGE);
            this.relativePath = "/images";

            this.listItems.clear();
            this.listItems.addAll(this.getImageCatalogs());
            adapter.notifyDataSetChanged();
        } else {
            this.clearSelectedFiles();

            this.relativePath = this.relativePath.substring(0, this.relativePath.lastIndexOf(File.separator));
            if (this.relativePath.equals(this.ROOT_PATH_FILE)) {
                this.titleView.setText(this.ROOT_LABEL_FILE);
                this.loadRootPath();
            } else {
                String dir = this.relativePath.substring(this.relativePath.lastIndexOf(File.separator) + 1);
                this.titleView.setText(dir);

                File file = new File(this.path);
                this.path = file.getParent();
                this.loadItems(file.getParent());
            }

        }
    }

    public void onOK(View source) {
        Intent intent = new Intent();
        intent.putExtra("selectedFiles", (ArrayList<String>) this.selectedFiles);
        this.setResult(RESULT_OK, intent);
        this.finish();
    }

    public void toggleMenu(View view) {
        final View menu = this.findViewById("menu");

        // LinearLayout menuBar = (LinearLayout) this.findViewById("menuBar");
        // menuBar.clearAnimation();

        // TranslateAnimation animation;
        if (menu.getVisibility() == View.VISIBLE) {
//            animation = new TranslateAnimation(menuBar.getWidth(), 0, 0, 0);
//            animation.setAnimationListener(new Animation.AnimationListener() {
//                @Override
//                public void onAnimationStart(Animation animation) {
//                }
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    menu.setVisibility(View.INVISIBLE);
//                }
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//                }
//            });
            menu.setVisibility(View.INVISIBLE);
        } else {
            menu.setVisibility(View.VISIBLE);

//            animation = new TranslateAnimation(0, menuBar.getWidth(), 0, 0);
        }

//        animation.setDuration(500);
//        animation.setFillAfter(true);

        // menuBar.setAnimation(animation);
    }

    public void openImageView(View source) {
        this.EXPLORER_TYPE = 1;
        this.title = "图片";
        this.relativePath = "/images";
        this.titleView.setText(this.ROOT_LABEL_IMAGE);

        this.listItems.clear();
        this.listItems.addAll(this.getImageCatalogs());
        adapter.notifyDataSetChanged();

        this.toggleMenu(source);
    }

    public void openFileView(View source) {
        this.EXPLORER_TYPE = 0;
        this.title = "Files";
        this.relativePath = "/files";
        this.titleView.setText(this.ROOT_LABEL_FILE);

        // String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        this.loadRootPath();

        this.toggleMenu(source);
    }

    private void loadRootPath() {
        this.listItems.clear();
        this.listItems.addAll(this.getVolumes());
        adapter.notifyDataSetChanged();
    }

    public List<Map<String, Object>> getImageCatalogs() {
        List<Map<String, Object>> catalogs = MediaStoreUtils.getCatalogs(this.getApplicationContext());
        int defFolder = this.findIdentifier("folder_default", "drawable");
        for (Map<String, Object> item : catalogs) {
            item.put("image", defFolder);
        }
        return catalogs;
    }
    public List<Map<String, Object>> getVolumes() {
        List<Map<String, Object>> volumes = new ArrayList<Map<String, Object>>();
        String[] paths = this.getVolumesPaths();

        int defFolder = this.findIdentifier("folder_default", "drawable");
        for (String path : paths) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("image", defFolder);
            item.put("title", path.substring(path.lastIndexOf(File.separator) + 1));
            item.put("path", path);

            volumes.add(item);
        }
        return volumes;
    }

    public String[] getVolumesPaths() {
        StorageManager sm = (StorageManager) this.getSystemService(Context.STORAGE_SERVICE);
        // 获取sdcard的路径：外置和内置
        try {
            String[] paths = (String[]) sm.getClass().getMethod("getVolumePaths", new Class[0]).invoke(sm, new Object[]{});
            return paths;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return new String[] {};
    }

    private List<Map<String, Object>> getListItems(String parent) {
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        File[] subFiles = getSubFiles(parent);
        if (subFiles == null || subFiles.length == 0) {
            return items;
        }
        List<File> files = FileUtils.sort(Arrays.asList(subFiles));
        files = FileUtils.sort(files);

        int defFolder = this.findIdentifier("folder_default", "drawable");
        int defFile = this.findIdentifier("file_x72", "drawable");

        for (File file : files) {
            Map<String, Object> item = new HashMap<String, Object>();
            if (file.isDirectory()) {
                item.put("image", defFolder);
            } else {
                if (file.getName().endsWith(".jpg")) {
                    List<Map<String, Object>> thumbnails = this.getThumbnails(file.getAbsolutePath(), false);
                    if (!thumbnails.isEmpty()) {
                        Map<String, Object> thumb = thumbnails.get(0);
                        Bitmap bitmap = MediaStoreUtils.getThumbnailsFromId(this.getContentResolver(), thumb.get("image_id").toString());
                        item.put("image", bitmap);
                    } else {
                        item.put("image", defFile);
                    }
                } else {
                    item.put("image", defFile);
                }
            }
            item.put("title", file.getName());
            item.put("path", file.getAbsolutePath());

            items.add(item);
        }
        return items;
    }

    private File[] getSubFiles(String parent) {
        return (new File(parent)).listFiles();
    }

    private View findViewById(String id) {
        return this.findViewById(findIdentifier(id, "id"));
    }

    private int findIdentifier(String id, String defType) {
        return getApplication().getResources().getIdentifier(id, defType, getApplication().getPackageName());
    }

    public List<Map<String, Object>> getThumbnails(String path, boolean isVideo) {
        List<Map<String, Object>> thumbnails = new ArrayList<Map<String, Object>>();

        String volumeName = "external";
        Uri uri = isVideo ? MediaStore.Video.Media.getContentUri(volumeName) : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                .getContentUri(volumeName);
        String selection = MediaStore.MediaColumns.DATA + "=?";
        String[] selectionArgs = new String[] {
                path
        };

        String[] columns = new String[] {
                // MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA
        };

        Cursor c = this.getContentResolver()
                .query(uri, columns, selection, selectionArgs, null);
        if (c == null) {
            return thumbnails;
        }

        String[] columns0 = c.getColumnNames();

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
}
