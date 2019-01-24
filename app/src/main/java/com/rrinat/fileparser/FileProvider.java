package com.rrinat.fileparser;

import android.annotation.SuppressLint;
import android.content.Context;

public class FileProvider {

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
}
