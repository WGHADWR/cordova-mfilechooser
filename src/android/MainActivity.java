package com.gx.filechooser;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getApplication().getResources().getIdentifier(
                "activity_main", "layout", getApplication().getPackageName()));
    }
}
