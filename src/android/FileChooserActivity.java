package com.gx.filechooser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.gx.filechooser.common.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileChooserActivity extends Activity {

    private TextView title;
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath();

    private List<Map<String, Object>> listItems;
    private SimpleAdapter adapter;

    private List<String> selectedFiles = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.getApplication().getResources().getIdentifier(
                "activity_filechooser", "layout", getApplication().getPackageName()));

        this.title = (TextView) this.findViewById("title");
        this.title.setText(this.path);

        final ListView listView = (ListView) this.findViewById("list_view");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, Object> selectedItem = listItems.get(i);
                String fileName = selectedItem.get("title").toString();
                String selectFile;
                if (path.equals("/")) {
                    selectFile = path + fileName;
                } else {
                    selectFile = path + File.separator + fileName;
                }
                if ((new File(selectFile)).isDirectory()) {
                    path = selectFile;
                    clearSelectedFiles();
                    loadItems(selectFile);
                    return;
                }
                addOrRemoveFile(view, selectFile);
            }
        });

        String[] from = { "image", "title" };

        int imageView = this.findIdentifier("imageView", "id");
        int textView = this.findIdentifier("textView2", "id");
        int itemLayout = this.findIdentifier("listview_item", "layout");

        int[] to = { imageView, textView };

        this.listItems = this.getListItems(this.path);
        this.adapter = new SimpleAdapter(
                this, this.listItems, itemLayout, from, to);
        listView.setAdapter(this.adapter);


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
        this.title.setText(path);
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
        if (this.path.equals("/")) {
            this.finish();
            return;
        }
        this.clearSelectedFiles();

        File file = new File(this.path);
        this.path = file.getParent();
        this.loadItems(file.getParent());
    }

    public void onOK(View source) {
        Intent intent = new Intent();
        intent.putExtra("selectedFiles", this.selectedFiles.toArray());
        this.setResult(RESULT_OK, intent);
        this.finish();
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
                item.put("image", defFile);
            }
            item.put("title", file.getName());

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
}
