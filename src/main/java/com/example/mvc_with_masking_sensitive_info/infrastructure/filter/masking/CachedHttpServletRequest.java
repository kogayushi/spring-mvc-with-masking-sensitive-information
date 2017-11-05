package com.example.mvc_with_masking_sensitive_info.infrastructure.filter.masking;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.file.Files;

public class CachedHttpServletRequest extends HttpServletRequestWrapper
{

    public static final String TEMPORARY_FILENAME_PREFIX = "servletRequest_";
    public static final String TEMPORARY_FILENAME_SUFFIX = ".cache";

    public static final int LEN_BUFFER = 32768; //32 KB

    private final ServletInputStream servletInputStream;

    private File temporaryFile;


    public CachedHttpServletRequest(HttpServletRequest httpServletRequest)
            throws ServletException {

        super(httpServletRequest);

        try {
            //Create a temporary file to hold the contents of the request's input stream
            this.temporaryFile = Files.createTempFile(TEMPORARY_FILENAME_PREFIX, TEMPORARY_FILENAME_SUFFIX).toFile();

            //Copy the request body to the temporary file
            this.servletInputStream = super.getInputStream();
            BufferedInputStream is = new BufferedInputStream(this.servletInputStream);
            FileOutputStream os = new FileOutputStream(temporaryFile);

            byte[] buffer = new byte[LEN_BUFFER];
            int bytesRead = is.read(buffer);
            while(bytesRead != -1) {
                os.write(buffer, 0, bytesRead);
                bytesRead = is.read(buffer);
            }
            is.close();
            os.close();
        }
        catch(Exception e) {
            throw new ServletException(e);
        }
    }


    public void cleanup() {
        this.temporaryFile.delete();
    }


    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CachedServletInputStream(this.servletInputStream, this.temporaryFile);
    }


    @Override
    public BufferedReader getReader() throws IOException {
        String enc = getCharacterEncoding();
        if(enc == null) enc = "UTF-8";
        return new BufferedReader(new InputStreamReader(getInputStream(), enc));
    }
}
