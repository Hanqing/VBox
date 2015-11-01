package com.smart.vbox.support.utils;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author lhq
 * created at 2015/10/24 10:02
 */
public class OkHttpClientManager {
    private static final String TAG = "OkHttpClientManager";
    private static com.squareup.okhttp.OkHttpClient sInstance;

    public static com.squareup.okhttp.OkHttpClient getInstance() {
        if (sInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (sInstance == null) {
                    sInstance = new com.squareup.okhttp.OkHttpClient();
                    //cookie enabled
                    sInstance.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
                    //从主机读取数据超时
                    sInstance.setReadTimeout(15, TimeUnit.SECONDS);
                    //连接主机超时
                    sInstance.setConnectTimeout(20, TimeUnit.SECONDS);
                }
            }
        }
        return sInstance;
    }


//    public void xx(String url) {
//
//        Request.Builder builder = new Request.Builder();
//        builder.url(url)
//                .addHeader(NewAcString.APP_VERSION, NewAcString.APP_VERSION_400)
//                .addHeader(NewAcString.DEVICETYPE, NewAcString.DEVICETYPE_1)
//                .addHeader(NewAcString.MARKET, NewAcString.MARKET_PORTAL)
//                .addHeader(NewAcString.PRODUCTID, NewAcString.PRODUCTID_2000)
//                .addHeader(NewAcString.RESOLUTION, NewAcString.RESOLUTION_WIDTH_HEIGHT)
//                .addHeader(NewAcString.UUID, NewAcString.UUID_X);
//
//        Request request = builder.build();
//
//        Call call = OkHttpClientManager.getInstance().newCall(request);
//        call.enqueue(new com.squareup.okhttp.Callback() {
//                         @Override
//                         public void onFailure(Request request, IOException e) {
//
//                         }
//
//                         @Override
//                         public void onResponse(com.squareup.okhttp.Response response) throws IOException {
//                             response.toString();
//                         }
//                     }
//        );
//    }

}
