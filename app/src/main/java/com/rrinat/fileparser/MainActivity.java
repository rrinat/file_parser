package com.rrinat.fileparser;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity implements ParserService.Listener {

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 101;

    private EditText filePathEditText;
    private EditText regExpEditText;

    private Button startButton;

    private Adapter adapter = new Adapter();

    private ParserService parserService;
    private boolean bound = false;

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

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        startButton.setOnClickListener(v -> onStartClick());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.copy) {
            onCopyClick();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startParseService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isFinishing()) {
            parserService.stopSelf();
        }
    }

    private void startParseService() {
        Intent intent = new Intent(this, ParserService.class);
        startService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void onStartClick() {
        checkPermissionAndStartParser();
    }

    private void checkPermissionAndStartParser() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else  {
            startParser();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE && grantResults.length == 1
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startParser();
        } else  {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startParser() {
        if (bound) {
            parserService.tryToStart(filePathEditText.getText().toString(), regExpEditText.getText().toString());
            hideStartButton();
        }
    }

    @Override
    public void onComplete() {
        showStartButton();
    }

    @Override
    public void onAddLine() {
        adapter.onAdd();
    }

    @Override
    public void onClearLines() {
        adapter.onClearLines();
    }

    private void showStartButton() {
        startButton.setVisibility(View.VISIBLE);
    }

    private void hideStartButton() {
        startButton.setVisibility(View.GONE);
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            ParserService.ParserBinder binder = (ParserService.ParserBinder) service;
            parserService = binder.getService();
            bound = true;
            onBindService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
            parserService.clearListener();
        }
    };

    private void onBindService() {
        parserService.setListener(MainActivity.this);
        adapter.setItems(parserService.getItems());

        if (parserService.isParsing()) {
            hideStartButton();
        } else {
            showStartButton();
        }
    }

    private void onCopyClick() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            String value = adapter.getSelectedItems();
            ClipData clip = ClipData.newPlainText(getString(R.string.clip_copy), value);
            clipboard.setPrimaryClip(clip);
        }
    }
}
