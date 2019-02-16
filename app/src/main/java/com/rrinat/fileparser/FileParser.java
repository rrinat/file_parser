package com.rrinat.fileparser;


import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileParser {

    interface Listener {
        void onError();
        void onNext(String value);
        void onComplete();
    }

    private final static String OUTPUT_FILE_NAME = "results.log";
    private final static int BUFFER_SIZE = 1024 * 1024;

    private final FileProvider fileProvider = FileProvider.getInstance();
    private final StringParser stringParser;
    private final String inputFilePath;
    private final Listener listener;

    private BufferedInputStream reader = null;
    private BufferedWriter writer = null;
    private File inputFile;
    private File outputFile = null;
    private Thread thread;

    public FileParser(final Listener listener, final String inputFilePath, final String pattern) {
        this.listener = listener;
        this.inputFilePath = inputFilePath;
        this.stringParser = new StringParser(pattern);
    }

    public boolean start() {
        if (checkFiles()) {
            parsing();
            return true;
        } else {
            return false;
        }
    }

    public void stop() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
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
        thread = new Thread(createParsingRunnable());
        thread.start();
    }

    private Runnable createParsingRunnable() {
        return () -> {
            try {
                dispatchFile();
            } catch (InterruptedException e) {
                closeReaderAndWriter();
                listener.onError();
                Thread.currentThread().interrupt();
            }
        };
    }

    private void dispatchFile() throws InterruptedException {
        byte[] data = new byte[BUFFER_SIZE];
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int bytesRead;
                if ((bytesRead = reader.read(data)) == -1) {
                    stringParser.addLastLine();
                    writeLines(stringParser.getLines());
                    break;
                }
                stringParser.parse(data, bytesRead);
                writeLines(stringParser.getLines());
                stringParser.clearLines();
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

    private void writeLines(List<String> lines) throws IOException {
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
            listener.onNext(line);
        }
    }

    private void initReaderAndWriter() {
        try {
            reader = new BufferedInputStream(new FileInputStream(inputFile));
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
