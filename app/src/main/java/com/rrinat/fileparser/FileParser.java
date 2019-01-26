package com.rrinat.fileparser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class FileParser {

    interface Listener {
        void onError();
        void onNext(String value);
        void onComplete();
    }

    private final static String OUTPUT_FILE_NAME = "results.log";

    private final FileProvider fileProvider = FileProvider.getInstance();
    private final String inputFilePath;
    private final Pattern pattern;
    private final Listener listener;

    private BufferedReader reader = null;
    private BufferedWriter writer = null;
    private File inputFile;
    private File outputFile = null;

    public FileParser(final Listener listener, final String inputFilePath, final Pattern pattern) {
        this.listener = listener;
        this.inputFilePath = inputFilePath;
        this.pattern = pattern;
    }

    public boolean start() {
        if (checkFiles()) {
            parsing();
            return true;
        } else {
            return false;
        }
    }

    private boolean checkFiles() {
        inputFile = createInputFile();
        if (inputFile == null) {
            listener.onError();
            return false;
        }

        outputFile = createOutputFile();
        if (outputFile == null) {
            listener.onError();
            return false;
        }

        initReaderAndWriter();
        if (reader == null || writer == null) {
            listener.onError();
            return false;
        }

        return true;
    }

    private File createInputFile() {
        final File file = new File(inputFilePath);
        if (file.isFile() && file.exists() && file.canRead()) {
            return file;
        }
        return null;
    }

    private File createOutputFile() {
        final File directory = fileProvider.getPrivateExternalDirectory();
        if (directory != null) {
            final File file = new File(directory, OUTPUT_FILE_NAME);
            if (file.exists() && !file.delete()) {
                return null;
            }
            try {
                file.createNewFile();
                return file;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else  {
            return null;
        }
    }

    private void parsing() {
        new Thread(createParsingRunnable()).start();
    }

    private Runnable createParsingRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                String line;
                while (true) {
                    try {
                        if ((line = reader.readLine()) == null) break;
                        if (pattern.matcher(line).matches()) {
                            writer.write(line);
                            listener.onNext(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        closeReaderAndWriter();
                        listener.onError();
                        return;
                    }

                }
                closeReader();
                if (closeWriter()) {
                    listener.onComplete();
                } else {
                    listener.onError();
                }
            }
        };
    }

    private void initReaderAndWriter() {
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            writer = new BufferedWriter(new FileWriter(outputFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            closeReaderAndWriter();
        }
    }

    private void closeReaderAndWriter() {
        closeReader();
        closeWriter();
    }

    private boolean closeWriter() {
        boolean isSuccessful = true;
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                isSuccessful = false;
            } finally{
                writer = null;
            }
        }
        return isSuccessful;
    }

    private void closeReader() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                reader = null;
            }
        }
    }
}
