package cn.aigestudio.downloader.bizs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

final class DLUtil {
    static String obtainFileName(String url, String contentDisposition, String contentLocation) {
        String fileName = null;
        if (null != contentDisposition) {
            fileName = parseContentDisposition(contentDisposition);
            if (null != fileName) {
                int index = fileName.lastIndexOf('/') + 1;
                if (index > 0) {
                    fileName = fileName.substring(index);
                }
            }
        }
        if (fileName == null && contentLocation != null) {
            String decodedContentLocation = Uri.decode(contentLocation);
            if (decodedContentLocation != null && !decodedContentLocation.endsWith("/") && decodedContentLocation.indexOf('?') < 0) {
                int index = decodedContentLocation.lastIndexOf('/') + 1;
                if (index > 0) {
                    fileName = decodedContentLocation.substring(index);
                } else {
                    fileName = decodedContentLocation;
                }
            }
        }
        if (fileName == null) {
            String decodedUrl = Uri.decode(url);
            if (decodedUrl != null && !decodedUrl.endsWith("/") && decodedUrl.indexOf('?') < 0) {
                int index = decodedUrl.lastIndexOf('/') + 1;
                if (index > 0) {
                    fileName = decodedUrl.substring(index);
                }
            }
        }
        if (fileName == null) {
            fileName = UUID.randomUUID().toString();
        }
        fileName = replaceInvalidVFATCharacters(fileName);
        return fileName;
    }

    private static String parseContentDisposition(String contentDisposition) {
        int index = contentDisposition.indexOf("=");
        if (index > 0) {
            return contentDisposition.substring(index + 1);
        }
        return null;
    }

    private static String replaceInvalidVFATCharacters(String fileName) {
        final char END_CTRLCODE = 0x1f;
        final char QUOTEDBL = 0x22;
        final char ASTERISK = 0x2A;
        final char SLASH = 0x2F;
        final char COLON = 0x3A;
        final char LESS = 0x3C;
        final char GREATER = 0x3E;
        final char QUESTION = 0x3F;
        final char BACKSLASH = 0x5C;
        final char BAR = 0x7C;
        final char DEL = 0x7F;
        final char UNDERSCORE = 0x5F;

        StringBuilder sb = new StringBuilder();
        char ch;
        boolean isRepetition = false;
        for (int i = 0; i < fileName.length(); i++) {
            ch = fileName.charAt(i);
            if (ch <= END_CTRLCODE || ch == QUOTEDBL || ch == ASTERISK || ch == SLASH ||
                    ch == COLON || ch == LESS || ch == GREATER || ch == QUESTION ||
                    ch == BACKSLASH || ch == BAR || ch == DEL) {
                if (!isRepetition) {
                    sb.append(UNDERSCORE);
                    isRepetition = true;
                }
            } else {
                sb.append(ch);
                isRepetition = false;
            }
        }
        return sb.toString();
    }

    static HttpURLConnection buildConnection(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(HttpConnPars.POST.content);
        connection.setConnectTimeout(Integer.parseInt(HttpConnPars.CONNECT_TIMEOUT.content));
        connection.setRequestProperty(HttpConnPars.ACCEPT.header, HttpConnPars.ACCEPT.content);
        connection.setRequestProperty(HttpConnPars.ACCEPT_RANGE.header, HttpConnPars.ACCEPT_RANGE.content);
        connection.setRequestProperty(HttpConnPars.ACCEPT_LANGUAGE.header, HttpConnPars.ACCEPT_LANGUAGE.content);
        connection.setRequestProperty(HttpConnPars.CHARSET.header, HttpConnPars.CHARSET.content);
        connection.setRequestProperty(HttpConnPars.KEEP_CONNECT.header, HttpConnPars.KEEP_CONNECT.content);
        return connection;
    }

    static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            return null != info && info.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    static synchronized boolean createFile(String path, String fileName) {
        boolean hasFile = false;
        try {
            File dir = new File(path);
            boolean hasDir = dir.exists() || dir.mkdirs();
            if (hasDir) {
                File file = new File(dir, fileName);
                hasFile = file.exists() || file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hasFile;
    }
}