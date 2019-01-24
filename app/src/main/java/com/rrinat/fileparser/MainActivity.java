package com.rrinat.fileparser;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUECT_WRITE_EXTERNAL_STORAGE = 101;

    private EditText filePathEditText;
    private EditText regExpEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupListeners();
    }

    private void initViews() {
        filePathEditText = findViewById(R.id.file_path_edit_text);
        regExpEditText = findViewById(R.id.rexexp_edit_text);
    }

    private void setupListeners() {
        findViewById(R.id.start_button).setOnClickListener(v -> onStartClick());
        findViewById(R.id.stop_button).setOnClickListener(v -> onStopClick());
    }

    private void onStartClick() {
        checkPermissionAndStartParser();
    }

    private void onStopClick() {

    }

    private void checkPermissionAndStartParser() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUECT_WRITE_EXTERNAL_STORAGE);
        } else  {
            startParser();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUECT_WRITE_EXTERNAL_STORAGE && grantResults.length == 1
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startParser();
        } else  {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startParser() {

    }
}
