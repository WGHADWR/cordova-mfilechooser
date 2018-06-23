package com.gx.filechooser.common;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileUtils {

    public static List<File> sort(List<File> files) {
        if (files == null || files.isEmpty()) {
            return files;
        }

        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                if (f1.isDirectory() && f2.isFile()) {
                    return -1;
                }
                if (f1.isFile() && f2.isDirectory()) {
                    return 1;
                }
                return f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase());
            }
        });
        return files;
    }

}
