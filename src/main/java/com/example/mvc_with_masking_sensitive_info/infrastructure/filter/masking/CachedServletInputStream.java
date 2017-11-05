package com.example.mvc_with_masking_sensitive_info.infrastructure.filter.masking;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CachedServletInputStream extends ServletInputStream {

    private File temporaryFile;
    private InputStream inputStream;
    private ServletInputStream is;


    public CachedServletInputStream(ServletInputStream is, File temporaryFile) throws IOException {
        this.is = is;
        this.temporaryFile = temporaryFile;
        this.inputStream = null;
    }

    private InputStream acquireInputStream() throws IOException {
        if (inputStream == null) {
            inputStream = new FileInputStream(temporaryFile);
        }

        return inputStream;
    }

    public void close() throws IOException {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            throw e;
        } finally {
            inputStream = null;
        }
    }

    public int read() throws IOException {
        return acquireInputStream().read();
    }

    public boolean markSupported() {
        return false;
    }


    public synchronized void mark(int i) {
        throw new UnsupportedOperationException("mark not supported");
    }


    public synchronized void reset() throws IOException {
        throw new IOException(new UnsupportedOperationException("reset not supported"));
    }

    @Override
    public boolean isFinished() {
        return this.is.isFinished();
    }


    @Override
    public boolean isReady() {
        return this.is.isReady();
    }

    @Override
    public void setReadListener(ReadListener listener) {
        this.is.setReadListener(listener);
    }
}
