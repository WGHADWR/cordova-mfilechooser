package com.gx.filechooser;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import java.util.Map;
import java.util.List;

public class FileChooserActivity extends AppCompatActivity {

    private TextView header;
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;

    private List<Map<String, Object>> listItems;
    private SimpleAdapter adapter;

    private List<String> selectedFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        System.out.println(this.path);
        this.header = findViewById(R.id.header);
        this.header.setText(this.path);

        final ListView listView = this.findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Map<String, Object> selectedItem = listItems.get(i);
            String selectFile = path + selectedItem.get("title").toString();
            if ((new File(selectFile)).isDirectory()) {
                clearSelectedItems();
                path = selectFile;
                loadItems(selectFile);
                return;
            }
            addOrRemoveSelectItem(view, selectFile);
            }
        });

        String[] from = { "image", "title" };
        int[] to = { R.id.imageView, R.id.textView2 };

        this.listItems = this.getListItems(this.path);
        this.adapter = new SimpleAdapter(
                this, this.listItems, R.layout.listview_item, from, to);
        listView.setAdapter(this.adapter);
    }

    private void addOrRemoveSelectItem(View view, String selectFile) {
        if (selectedFiles.contains(selectFile)) {
            selectedFiles.remove(selectFile);
            view.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } else {
            selectedFiles.add(selectFile);
            view.setBackgroundColor(Color.parseColor("#ECEEF0"));
        }

        Button button = findViewById(R.id.btnOk);
        if (selectedFiles.isEmpty()) {
            button.setText("确定");
            button.setEnabled(false);
        } else {
            button.setText("确定(" + this.selectedFiles.size() + ")");
            button.setEnabled(true);
        }
    }

    private void clearSelectedItems() {
        Button button = findViewById(R.id.btnOk);
        button.setText("确定");
        button.setEnabled(false);
    }

    private void loadItems(String path) {
        this.header.setText(path);
        if (!path.equals("/")) {
            path += "/";
        }
        this.path = path;
        List<Map<String, Object>> data = getListItems(this.path);
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
        File file = new File(this.path);
        this.loadItems(file.getParent());
    }

    public void onOkHandler(View source) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("selectedFiles", (ArrayList<String>) this.selectedFiles);
        intent.putExtras(bundle);
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

        for (File file : files) {
            Map<String, Object> item = new HashMap<String, Object>();
            if (file.isDirectory()) {
                item.put("image", R.drawable.folder_default);
            } else {
                item.put("image", R.drawable.file_x72);
            }
            item.put("title", file.getName());

            items.add(item);
        }
        return items;
    }

    private File[] getSubFiles(String parent) {
        return (new File(parent)).listFiles();
    }

}
