package com.rrinat.fileparser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import java.io.File;

public class FileProvider {

    private static final String DIRECTORY_NAME = "results";

    private final Context context;

    public static FileProvider getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private FileProvider(Context context) {
        this.context = context;
    }

    private static class SingletonHelper {
        @SuppressLint("StaticFieldLeak")
        private static final FileProvider INSTANCE = new FileProvider(App.getInstance());
    }

    public File getPrivateExternalDirectory() {
        if (isExternalStorageWritable()) {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), DIRECTORY_NAME);
            if (file.exists() || file.mkdir()) {
                return file;
            }
        }
        return null;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

}
