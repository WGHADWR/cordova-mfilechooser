package com.gx.filechooser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class FileChooser extends CordovaPlugin {

    private static final String ACTION_OPEN = "open";
    private static final int FILECHOOSER_REQUEST_CODE = 0;

    private CallbackContext callback;

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        if (ACTION_OPEN.equals(action)) {
            this.open(callbackContext);
            return true;
        }

        return false;
    }

    private void open(CallbackContext callbackContext) {
        Intent intent = new Intent(null, FileChooserActivity.class);
        cordova.startActivityForResult(this, intent, 0);

        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        this.callback = callbackContext;
        callbackContext.sendPluginResult(pluginResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (FILECHOOSER_REQUEST_CODE != requestCode) {
            return;
        }
        if (Activity.RESULT_OK == resultCode) {
            Bundle bundle = intent.getExtras();
            List<String> selectedFiles = bundle.getStringArrayList("selectedFiles");

            JSONArray jsonArray = new JSONArray(selectedFiles);
            callback.success(jsonArray);
        } else {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
            callback.sendPluginResult(pluginResult);
        }
    }
}
