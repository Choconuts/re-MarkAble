package com.example.choconut.re_markable;

import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class UserHelper {

    private Handler handler;

    UserHelper(Handler h){
        handler = h;
    }

    void login(String username, String password, String email){
        String msg;
        msg = "注册成功";

        new Thread(){
            @Override
            public void run() {
                super.run();

                UrlHelper urlHelper = new UrlHelper("http://10.15.82.223:9090/app_get_data/app_register");
                urlHelper.put("username", username);
                urlHelper.put("password", password);
                urlHelper.put("email", email);
                JSONObject response = urlHelper.request();

                Message message = handler.obtainMessage();
                message.what = 0;
                message.obj = response;
                handler.sendMessageDelayed(message, 0);
            }
        }.start();
    }

    void signIn(String username, String password){
        String msg;

        new Thread(){
            @Override
            public void run() {
                super.run();

                UrlHelper urlHelper = new UrlHelper("http://10.15.82.223:9090/app_get_data/app_signincheck");
                urlHelper.put("username", username);
                urlHelper.put("password", password);
                JSONObject response = urlHelper.request();

                Message message = handler.obtainMessage();
                message.what = 1;
                message.obj = response;
                handler.sendMessageDelayed(message, 0);
            }
        }.start();
    }

    void signOut(String token){
        new Thread(){
            @Override
            public void run() {
                super.run();

                UrlHelper urlHelper = new UrlHelper("http://10.15.82.223:9090/app_get_data/app_logout");
                urlHelper.put("token", token);
                JSONObject response = urlHelper.request();

                Message message = handler.obtainMessage();
                message.what = 2;
                message.obj = response;
                handler.sendMessageDelayed(message, 0);
            }
        }.start();
    }

    void getEntities(String token){
        new Thread(){
            @Override
            public void run() {
                super.run();

                UrlHelper urlHelper = new UrlHelper("http://10.15.82.223:9090/app_get_data/app_get_entity");
                urlHelper.put("token", token);
                JSONObject response = urlHelper.request();

                Message message = handler.obtainMessage();
                message.what = 3;
                message.obj = response;
                handler.sendMessageDelayed(message, 0);
            }
        }.start();
    }

    void getTriples(String token){
        new Thread(){
            @Override
            public void run() {
                super.run();

                UrlHelper urlHelper = new UrlHelper("http://10.15.82.223:9090/app_get_data/app_get_triple");
                urlHelper.put("token", token);
                JSONObject response = urlHelper.request();

                Message message = handler.obtainMessage();
                message.what = 4;
                message.obj = response;
                handler.sendMessageDelayed(message, 0);
            }
        }.start();
    }

    void uploadEntities(String token, String entities){
        new Thread(){
            @Override
            public void run() {
                super.run();

                UrlHelper urlHelper = new UrlHelper("http://10.15.82.223:9090/app_get_data/app_upload_entity");
                urlHelper.put("token", token);
                urlHelper.put("entities", entities);
                JSONObject response = urlHelper.request();

                Message message = handler.obtainMessage();
                message.what = 5;
                message.obj = response;
                handler.sendMessageDelayed(message, 0);
            }
        }.start();
    }

    void uploadTriples(String token, String triples){
        new Thread(){
            @Override
            public void run() {
                super.run();

                UrlHelper urlHelper = new UrlHelper("http://10.15.82.223:9090/app_get_data/app_upload_entity");
                urlHelper.put("token", token);
                urlHelper.put("triples", triples);
                JSONObject response = urlHelper.request();

                Message message = handler.obtainMessage();
                message.what = 5;
                message.obj = response;
                handler.sendMessageDelayed(message, 0);
            }
        }.start();
    }

    static int getStatus(Message message) {
        return message.what;
    }
    static String getMsg(Message message) {
        try {
            JSONObject object = new JSONObject(message.obj.toString());
            return object.getString("msg");
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
    static String getToken(Message message) {
        try {
            JSONObject object = new JSONObject(message.obj.toString());
            return object.getString("token");
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
    static JSONObject getJson(Message message) {
        try {
            return new JSONObject(message.obj.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}


class UrlHelper {

    private String urlString;

    UrlHelper(String url) {
        urlString = url;
    }

    private HashMap<String, String> body = new HashMap<>();

    void put(String k, String v) {
        body.put(k, v);
    }

    JSONObject request(){
        try {
            URL url = new URL(urlString);
            URLConnection urlConnection = url.openConnection();// 打开连接
            HttpURLConnection connection = null;
            if (urlConnection instanceof HttpURLConnection) {
                connection = (HttpURLConnection) urlConnection;
            } else {
                System.out.println("请输入 URL 地址");
                return null;
            }
            connection.setRequestMethod("POST");
            // 设置之后可以向服务端写入数据、、可以使用conn.getOutputStream().write()
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            urlConnection.connect();

            OutputStream outputStream = urlConnection.getOutputStream();
            StringBuilder param = new StringBuilder();
            // 拼接参数
//            parm.append("username=").append("test_s2rf").
//                    append("&").append("email=").append("124ed").
//                    append("&").append("password=").append("w1r345y6");
            for (Map.Entry<String, String> entry: body.entrySet()) {
                param.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            // 写入参数内容
            outputStream.write(param.toString().getBytes("UTF-8"));
            // 刷新输出流,把所有的数据流数据都传输过去
            outputStream.flush();
            // 关闭输出流
            outputStream.close();

            InputStream input = urlConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            StringBuilder bs = new StringBuilder();
            String l = null;
            while ((l = bufferedReader.readLine()) != null) {
                bs.append(l).append("\n");
            }
            return new JSONObject(bs.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}