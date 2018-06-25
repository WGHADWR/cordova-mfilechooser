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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FileChooser extends CordovaPlugin {

    private static final String ACTION_OPEN = "open";
    private static final int FILECHOOSER_REQUEST_CODE = 0;

    private CallbackContext callback;

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        this.callback = callbackContext;

        if (ACTION_OPEN.equals(action)) {
            this.open(callbackContext);
            return true;
        }

        return false;
    }

    private void open(CallbackContext callbackContext) {
        Intent intent = new Intent(this.cordova.getActivity(), com.gx.filechooser.FileChooserActivity.class);
        cordova.startActivityForResult(this, intent, FILECHOOSER_REQUEST_CODE);

        // PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        // pluginResult.setKeepCallback(true);
        // callbackContext.sendPluginResult(pluginResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (FILECHOOSER_REQUEST_CODE != requestCode || this.callback == null) {
            return;
        }
        if (Activity.RESULT_OK == resultCode) {
            Bundle bundle = intent.getExtras();
            List<String> selectedFiles = bundle.getStringArrayList("selectedFiles");

            List<JSONObject> array = new ArrayList<JSONObject>();
            try {
                for (int i = 0; i < selectedFiles.size(); i++) {
                    JSONObject item = new JSONObject();
                    item.put("path", selectedFiles.get(i));

                    array.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray jsonArray = new JSONArray(array);
            callback.success(jsonArray);
        } else {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
            callback.sendPluginResult(pluginResult);
        }
    }
}
