package cn.aigestudio.downloader.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.aigestudio.downloader.cons.HttpConnPars;
import cn.aigestudio.downloader.cons.PublicCons;

/**
 * 网络操作工具类
 *
 * @author AigeStudio 2015-05-08
 */
public class NetUtil {
    /**
     * 根据url构建HTTP链接对象
     *
     * @param url url路径
     * @return HTTP链接对象
     * @throws IOException 链接异常时抛出
     */
    public static HttpURLConnection buildConnection(String url) throws IOException {
        return buildConnection(url, false);
    }

    /**
     * 根据url构建HTTP链接对象
     *
     * @param url     url路径
     * @param isAlive 是否保持长连接
     * @return HTTP链接对象
     * @throws IOException 链接异常时抛出
     */
    public static HttpURLConnection buildConnection(String url, boolean isAlive) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(HttpConnPars.POST.content);
        connection.setConnectTimeout(Integer.parseInt(HttpConnPars.CONNECT_TIMEOUT.content));
        connection.setRequestProperty(HttpConnPars.ACCEPT.header, HttpConnPars.ACCEPT.content);
        connection.setRequestProperty(HttpConnPars.ACCEPT_RANGE.header, HttpConnPars.ACCEPT_RANGE.content);
        connection.setRequestProperty(HttpConnPars.ACCEPT_LANGUAGE.header, HttpConnPars.ACCEPT_LANGUAGE.content);
        connection.setRequestProperty(HttpConnPars.CHARSET.header, HttpConnPars.CHARSET.content);
        if (isAlive) {
            connection.setRequestProperty(HttpConnPars.KEEP_CONNECT.header, HttpConnPars.KEEP_CONNECT.content);
        }
        return connection;
    }

    /**
     * 获取网络类型
     *
     * @param context ...
     * @return 网络类型ID {@link PublicCons.NetType}
     */
    public static int getNetWorkType(Context context) {
        int type = PublicCons.NetType.INVALID;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String typeName = networkInfo.getTypeName();
            if (typeName.equalsIgnoreCase("WIFI")) {
                type = PublicCons.NetType.WIFI;
            } else if (typeName.equalsIgnoreCase("MOBILE")) {
                String proxyHost = android.net.Proxy.getDefaultHost();
                type = TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(context) ?
                        PublicCons.NetType.G3 : PublicCons.NetType.G2) :
                        PublicCons.NetType.WAP;
            }
        }
        return type;
    }

    /**
     * 判断是否是3G+的移动网络
     *
     * @param context ...
     * @return ...
     */
    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }
}
