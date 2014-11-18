package com.example.kpp.mykpp001;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * WebAPIとの通信クラス
 * @author T.Kawamoto
 * @version 1.0
 */
public class HttpAccesser extends AsyncTaskLoader<TransData> {
    private static final String TAG = HttpAccesser.class.getName();

//    private final static String SERVER_URL = "http://192.168.0.23:8080/testapp/api/resource/putHealthInfo";
    private final static String SERVER_URL = "http://54.64.73.55:8000/testapp/api/resource/putHealthInfo";

    // 送信データ
    private TransData mSendData = null;
    // HTTP通信
    private HttpURLConnection mHttpConnection = null;

    public HttpAccesser(Context context, TransData sendData) {
        super(context);
        this.mSendData = sendData;
    }

    /**
     * バックグラウンド処理
     * @return
     */
    public TransData loadInBackground() {
        TransData recvData = send(mSendData);
        return recvData;
    }

    /*
     * Loderの準備が完了した際に呼ばれる
     */
    protected void onStartLoading() {
        // 開始処理
        forceLoad();
    }

    /*
     * サーバへの値の送受信
     */
    private TransData send(TransData sendData) {
        // HTTPリクエスト送信用ストリーム
        OutputStream out = null;
        // HTTPレスポンス取得用ストリーム
        InputStream in = null;
        boolean ret = false;
        TransData recvData = new TransData();

        // 接続初期化
        ret = initConnection();
        if(false == ret) {
            return recvData;
        }

        try {
            // 接続
            mHttpConnection.connect();

            // リクエスト送信
            out = mHttpConnection.getOutputStream();
            Gson gson = new Gson();
            String obj2 = gson.toJson(sendData);
            PrintStream ps = new PrintStream(out);
            ps.print(obj2);
            ps.close();

            // レスポンス取得
            in = mHttpConnection.getInputStream();
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            String json = new String(buffer);
            recvData = gson.fromJson(json, TransData.class);
            Log.d(TAG, "----------status:" + recvData.status);
            Log.d(TAG, "----------success");
        } catch(Exception e) {
            e.printStackTrace();
            Log.d(TAG, "----------exception");
        } finally {
            try {
                if(in != null) {
                    in.close();
                }
                if(out != null) {
                    out.close();
                }
                if(mHttpConnection != null) {
                    mHttpConnection.disconnect();
                }
            } catch(Exception e) {
//                recvData = null;
            }
        }

        return recvData;
    }

    /*
     * 接続初期化
     */
    private boolean initConnection() {
        // URL指定
        URL url;
        try {
            url = new URL(SERVER_URL);

            // HttpURLConnectionインスタンス作成
            mHttpConnection = (HttpURLConnection)url.openConnection();

            // POST設定
            mHttpConnection.setRequestMethod("POST");

            // HTTPヘッダの設定
            mHttpConnection.setRequestProperty("Content-Type","application/json; charset=utf-8");

            // URL 接続を使用して入出力を行う
            mHttpConnection.setDoInput(true);
            mHttpConnection.setDoOutput(true);

            // キャッシュは使用しない
            mHttpConnection.setUseCaches(false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        finally {
            if(mHttpConnection != null) {
                mHttpConnection.disconnect();
            }
        }
        return true;
    }
}