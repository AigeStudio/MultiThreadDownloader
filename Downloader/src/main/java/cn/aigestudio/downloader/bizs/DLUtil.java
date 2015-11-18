package cn.aigestudio.downloader.bizs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

final class DLUtil {
    public static final String DEFAULT_USER_AGENT;

    static {
        final StringBuilder builder = new StringBuilder();

        final boolean validRelease = !TextUtils.isEmpty(Build.VERSION.RELEASE);
        final boolean validId = !TextUtils.isEmpty(Build.ID);
        final boolean includeModel = "REL".equals(Build.VERSION.CODENAME)
                && !TextUtils.isEmpty(Build.MODEL);

        builder.append("MultiThreadDownloader");
        if (validRelease) {
            builder.append("/").append(Build.VERSION.RELEASE);
        }
        builder.append(" (Linux; U; Android");
        if (validRelease) {
            builder.append(" ").append(Build.VERSION.RELEASE);
        }
        if (includeModel || validId) {
            builder.append(";");
            if (includeModel) {
                builder.append(" ").append(Build.MODEL);
            }
            if (validId) {
                builder.append(" Build/").append(Build.ID);
            }
        }
        builder.append(")");

        DEFAULT_USER_AGENT = builder.toString();
    }

    private DLUtil() {
    }

    static String normalizeMimeType(String type) {
        if (type == null) {
            return null;
        }
        type = type.trim().toLowerCase();
        final int semicolonIndex = type.indexOf(';');
        if (semicolonIndex != -1) {
            type = type.substring(0, semicolonIndex);
        }
        return type;
    }

    static List<DLHeader> initRequestHeaders(List<DLHeader> headers, DLInfo info) {
        if (null == headers || headers.isEmpty()) {
            headers = new ArrayList<>();
            headers.add(new DLHeader("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg," +
                    "application/x-shockwave-flash, application/xaml+xml," +
                    "application/vnd.ms-xpsdocument, application/x-ms-xbap," +
                    "application/x-ms-application, application/vnd.ms-excel," +
                    "application/vnd.ms-powerpoint, application/msword, */*"));
            headers.add(new DLHeader("Accept-Ranges", "bytes"));
            headers.add(new DLHeader("Charset", "UTF-8"));
            headers.add(new DLHeader("Connection", "Keep-Alive"));
            headers.add(new DLHeader("Accept-Encoding", "identity"));
            headers.add(new DLHeader("Range", "bytes=" + 0 + "-"));
        }
        if (!hasRequestHeader("User-Agent", headers)) {
            headers.add(new DLHeader("User-Agent", DEFAULT_USER_AGENT));
        }
        if (!TextUtils.isEmpty(info.eTag)) {
            headers.add(new DLHeader("If-Match", info.eTag));
        }
        return headers;
    }

    private static boolean hasRequestHeader(String key, List<DLHeader> headers) {
        for (DLHeader header : headers) {
            if (header.key.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

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
            if (decodedContentLocation != null && !decodedContentLocation.endsWith("/")
                    && decodedContentLocation.indexOf('?') < 0) {
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
}