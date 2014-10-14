package com.example.kpp.mykpp001;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kawamoto on 2014/10/06.
 */
public class HttpAccesser extends AsyncTaskLoader<TransData> {
    private final static String SERVER_URL = "http://54.64.73.55:8000/jersey2_sample/admin/putMessage";
//    private final static String SERVER_URL = "http://192.168.0.23:8080/jersey2_sample/admin/putMessage";
    private final static String ENCODE = "UTF-8";

    private TransData mSendData = null;                     // 送信データ
    private HttpURLConnection mHttpConnection = null;       // HTTP通信

    public HttpAccesser(Context context, TransData sendData) {
        super(context);
        this.mSendData = sendData;
    }

    // Loderの準備が完了した際に呼ばれる
    protected void onStartLoading() {
        // 開始処理
        forceLoad();
    }

    // バックグラウンド処理
    public TransData loadInBackground() {
        // ここに非同期処理で実施したい処理を記載する
        TransData recvData = send(mSendData);
        return recvData;
    }

    // サーバへの値の送受信
    public TransData send(TransData sendData) {
        OutputStream out = null;            // HTTPリクエスト送信用ストリーム
        InputStream in = null;              // HTTPレスポンス取得用ストリーム
        boolean ret = false;
        TransData recvData = null;

        // 接続初期化
        ret = initConnection();
        if(false == ret) {
            return null;
        }

        try {
            // 接続
            mHttpConnection.connect();

            // データを出力
            out = mHttpConnection.getOutputStream();
            Gson gson = new Gson();
            String obj2 = gson.toJson(sendData);
            PrintStream ps = new PrintStream(out);
            ps.print(obj2);
            ps.close();

            // レスポンスを取得
            in = mHttpConnection.getInputStream();
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            String json = new String(buffer);

            recvData = gson.fromJson(json, TransData.class);

        } catch(Exception e) {
            e.printStackTrace();
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

    // 接続初期化
    private boolean initConnection() {
        // URL指定
        URL url;
        try {
            url = new URL(SERVER_URL);

            // HttpURLConnectionインスタンス作成
            mHttpConnection = (HttpURLConnection)url.openConnection();

            // POST設定
            mHttpConnection.setRequestMethod("POST");

            // HTTPヘッダの「Content-Type」を「application/octet-stream」に設定
            mHttpConnection.setRequestProperty("Content-Type","application/json");

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