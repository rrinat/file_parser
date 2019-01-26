package com.rrinat.fileparser;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUECT_WRITE_EXTERNAL_STORAGE = 101;

    private EditText filePathEditText;
    private EditText regExpEditText;

    private Button startButton;
    private Button stopButton;
    private final Adapter adapter = new Adapter();

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

        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        startButton.setOnClickListener(v -> onStartClick());
        stopButton.setOnClickListener(v -> onStopClick());
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
        Pattern pattern = new RegExpConverter().convertPattern(regExpEditText.getText().toString());

        FileParser fileParser = new FileParser(new FileParser.Listener() {
            @Override
            public void onError() {
                runOnUiThread(() -> {
                    showError();
                    showStartButton();
                });
            }

            @Override
            public void onNext(String value) {
                runOnUiThread(() -> adapter.add(value));
            }

            @Override
            public void onComplete() {
                runOnUiThread(() -> showStartButton());
            }
        }, filePathEditText.getText().toString(), pattern);

        adapter.clear();
        showStopButton();

        fileParser.start();
    }

    private void showStartButton() {
        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.GONE);
    }

    private void showStopButton() {
        startButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.VISIBLE);
    }

    private void showError() {
        Toast.makeText(this, R.string.error_parsing, Toast.LENGTH_LONG).show();
    }
}
