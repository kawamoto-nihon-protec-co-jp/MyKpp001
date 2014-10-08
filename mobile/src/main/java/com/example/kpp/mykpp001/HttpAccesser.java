package com.example.kpp.mykpp001;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kawamoto on 2014/10/06.
 */
public class HttpAccesser extends AsyncTaskLoader<TransData> {
    private final static String SERVER_URL = "http://54.64.73.55:8000/jersey2_sample/admin/putMessage";
   // private final static String SERVER_URL = "http://192.168.0.23:8080/jersey2_sample/admin/putMessage";
    private final static String ENCODE = "UTF-8";

    private TransData mSendData = null;                     // 送信データ
    private HttpURLConnection mHttpConnection = null;       // HTTP通信

    public HttpAccesser(Context context, TransData sendData) {
        super(context);

        this.mSendData = sendData;
    }

    public TransData loadInBackground() {
        // ここに非同期処理で実施したい処理を記載する
        TransData recvData = null;

        recvData = send(mSendData);

        return recvData;
    }

    public void onCanceled(Boolean data) {
        // キャンセル処理
    }

    protected void onStartLoading() {
        // 開始処理
        forceLoad();
    }

    public TransData send(TransData sendData) {
        OutputStream out = null;            // HTTPリクエスト送信用ストリーム
        OutputStreamWriter outw = null;            // HTTPリクエスト送信用ストリーム
        InputStream in = null;              // HTTPレスポンス取得用ストリーム
        BufferedReader reader = null;       // レスポンスデータ出力用バッファ
        int retByte = 0;
        boolean ret = false;
        byte[] sendMessage = null;
        byte[] recvMessage = null;
        TransData recvData = null;

        // 接続初期化
        ret = initConnection();
        if(false == ret) {
            return null;
        }

        try {
            // 送信データ生成
            sendMessage = new byte[255];
            System.arraycopy(mSendData.data.getBytes(), 0, sendMessage, 0, mSendData.data.length());

            // 接続
            mHttpConnection.connect();//otomo

            // int code = mHttpConnection.getResponseCode();

            // データを出力
            //Create JSONObject here
            /*JSONObject jsonParam = new JSONObject();
            jsonParam.put("ID", sendData);
            jsonParam.put("description", "Real");
            jsonParam.put("enable", "true");*/
            //out = new BufferedOutputStream(mHttpConnection.getOutputStream());
            out = mHttpConnection.getOutputStream();
            //out.write(sendMessage);
            //out.flush();
            //out.close();
            Gson gson = new Gson();
            String obj2 = gson.toJson(sendData);
            PrintStream ps = new PrintStream(out);
            ps.print(obj2);//"{\"data\":\""+ sendData.data +"\"}");
            ps.close();

            // レスポンスを取得
            //in = new BufferedInputStream(mHttpConnection.getInputStream());
            in = mHttpConnection.getInputStream();
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            String json = new String(buffer);

            TransData tData = gson.fromJson(json, TransData.class);
            recvData = tData;
//            reader = new BufferedReader(new InputStreamReader(in));
//            String s;
//            while((s = reader.readLine()) != null){
//                System.out.println(s);
//            }
//            reader.close();

            /*recvMessage = new byte[1024];
            retByte = in.read(recvMessage);
            if(0 > retByte) {
                return null;
            }*/

            // 受信データ解析
            //recvData = new TransData();
            //ret = analyzeRecvData(recvMessage, recvData);
            //if(false == ret) {
            //    return null;
            //}
        } catch(Exception e) {
            e.printStackTrace();
//            recvData = null;
        } finally {
            try {
//                if(reader != null) {
//                    reader.close();
//                }
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
            //mHttpConnection.setRequestProperty("Content-Type","application/octet-stream");
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

    private boolean analyzeRecvData(byte[] recvMessage, TransData recvData) {
        try {
             if(null == recvData)
            {
                return false;
            }

            recvData.data = new String(recvMessage, ENCODE);
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
