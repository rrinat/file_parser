package com.rrinat.fileparser;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ParserService extends Service implements FileParser.Listener {

    interface Listener {
        void onComplete();
        void onAddLine();
        void onClearLines();
    }

    public class ParserBinder extends Binder {
        ParserService getService() {
            return ParserService.this;
        }
    }

    private final List<String> items = new ArrayList<>();
    private final IBinder binder = new ParserBinder();
    private final Handler mainHandler = new Handler();

    private boolean isParsing;
    private FileParser fileParser;
    private WeakReference<Listener> listenerWeakReference;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setListener(Listener listener) {
        this.listenerWeakReference = new WeakReference<>(listener);
    }

    public void clearListener() {
        listenerWeakReference.clear();
    }

    public boolean isParsing() {
        return isParsing;
    }

    public List<String> getItems() {
        return items;
    }

    public void tryToStart(final String inputFilePath, final String regExp) {
        if (isParsing) {
            return;
        }

        clearItems();

        fileParser = new FileParser(this, inputFilePath, new RegExpConverter().convertPattern(regExp));
        if (fileParser.start()) {
            isParsing = true;
        }
    }

    @Override
    public void onError() {
        mainHandler.post(() -> {
            Toast.makeText(this, R.string.error_parsing, Toast.LENGTH_LONG).show();
            callListenerComplete();
        });
    }

    @Override
    public void onNext(String value) {
        mainHandler.post(() -> {
            items.add(value);
            Listener listener = listenerWeakReference.get();
            if (listener != null) {
                listener.onAddLine();
            }
        });
    }

    @Override
    public void onComplete() {
        mainHandler.post(() -> {
            isParsing = false;
            callListenerComplete();
        });
    }

    private void callListenerComplete() {
        Listener listener = listenerWeakReference.get();
        if (listener != null) {
            listener.onComplete();
        }
    }

    private void clearItems() {
        items.clear();
        Listener listener = listenerWeakReference.get();
        if (listener != null) {
            listener.onClearLines();
        }
    }
}
