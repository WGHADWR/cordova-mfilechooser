package com.gx.filechooser;

import android.app.Activity;
import android.os.Bundle;

public class FileChooserActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.getApplication().getResources().getIdentifier(
                "activity_filechooser", "layout", getApplication().getPackageName()));
    }
}
