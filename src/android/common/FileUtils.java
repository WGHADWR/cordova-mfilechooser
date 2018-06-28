package com.gx.filechooser.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class FileUtils {

    private static final List<String> imageTypes = Arrays.asList(new String[] { "JPG", "JPEG", "PNG", "BMP", "GIF" });

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

    public static boolean isImage(String file) {
        String fileSuffix = file.substring(file.lastIndexOf(".") + 1);
        if (fileSuffix.trim().length() == 0) {
            return false;
        }
        return imageTypes.contains(fileSuffix.toUpperCase());
    }

    public static void copy(String source, String dest) throws Exception {
        FileInputStream fins = null;
        FileOutputStream fouts = null;
        try {
            fins = new FileInputStream(source);
            fouts = new FileOutputStream(dest);
            byte[] bytes = new byte[1024 * 10];
            int len = -1;
            while ((len = fins.read(bytes)) != -1) {
                fouts.write(bytes, 0, len);
            }
            fouts.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fouts != null) {
                fouts.close();
            }
            if (fins != null) {
                fins.close();
            }
        }
    }

}
