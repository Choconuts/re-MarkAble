package com.example.choconut.re_markable;

import android.os.Handler;
import android.os.Message;

import com.example.choconut.re_markable.qcloud.Module.Base;
import com.example.choconut.re_markable.qcloud.Module.Wenzhi;
import com.example.choconut.re_markable.qcloud.QcloudApiModuleCenter;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class UserHelper {

    private Handler handler;

    private static String loginUrl = "http://10.15.82.223:9090/app_get_data/app_register";
    private static String signInUrl = "http://10.15.82.223:9090/app_get_data/app_signincheck";
    private static String signOutUrl = "http://10.15.82.223:9090/app_get_data/app_logout";
    private static String entityUrl = "http://10.15.82.223:9090/app_get_data/app_get_entity";
    private static String tripleUrl = "http://10.15.82.223:9090/app_get_data/app_get_triple";
    private static String entityUploadUrl = "http://10.15.82.223:9090/app_get_data/app_upload_entity";
    private static String tripleUploadUrl = "http://10.15.82.223:9090/app_get_data/app_upload_entity";

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

                UrlHelper urlHelper = new UrlHelper(loginUrl);
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

                UrlHelper urlHelper = new UrlHelper(signInUrl);
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

                UrlHelper urlHelper = new UrlHelper(signOutUrl);
                urlHelper.put("token", token);
                JSONObject response = urlHelper.request();

                Message message = handler.obtainMessage();
                message.what = 2;
                message.obj = response;
                handler.sendMessageDelayed(message, 0);
            }
        }.start();
    }

    void geDividedtWords(JSONObject object){
        String temp = "";
        try {
            if (object.has("content")) {
                temp = object.getString("content");
            }
            else if (object.has("sent_ctx")) {
                temp = object.getString("sent_ctx");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        final String text = temp;
        new Thread(){
            @Override
            public void run() {
                super.run();
                JSONObject json_result = new JSONObject();
                boolean status = true;
                while (status) {
                    TreeMap<String, Object> config = new TreeMap<String, Object>();
                    config.put("SecretId", "AKID5BC7xkJ3Z9g7Sh5trkXFrOsbP9nLqJi1");
                    config.put("SecretKey", "PlGk7pMittiMp7EAvUs9NYeeYM5jHj5L");
                    /* 请求方法类型 POST、GET */
                    config.put("RequestMethod", "GET");
                    /* 区域参数，可选: gz:广州; sh:上海; hk:香港; ca:北美;等。 */
                    config.put("DefaultRegion", "sh");

                    /*
                     * 你将要使用接口所在的模块，可以从 官网->云api文档->XXXX接口->接口描述->域名
                     * 中获取，比如域名：cvm.api.qcloud.com，module就是new Cvm()。
                     */
                    /*
                     * DescribeInstances
                     * 的api文档地址：http://www.qcloud.com/wiki/v2/DescribeInstances
                     */
                    QcloudApiModuleCenter module = new QcloudApiModuleCenter(new Wenzhi(), config);
                    TreeMap<String, Object> params = new TreeMap<String, Object>();
                    // 将需要输入的参数都放入 params 里面，必选参数是必填的。
                    // DescribeInstances 接口的部分可选参数如下
                    params.put("text", text);
                    params.put("code", 0x00200000);
                    // 在这里指定所要用的签名算法，不指定默认为HmacSHA1
                    // params.put("SignatureMethod", "HmacSHA256");
                    // generateUrl 方法生成请求串，但不发送请求。在正式请求中，可以删除下面这行代码。
                    // 如果是POST方法，或者系统不支持UTF8编码，则仅会打印host+path信息。
                    // System.out.println(module.generateUrl("DescribeInstances", params));

                    String result = null;
                    try {
                        // call 方法正式向指定的接口名发送请求，并把请求参数params传入，返回即是接口的请求结果。
                        result = module.call("LexicalAnalysis", params);
                        // 可以对返回的字符串进行json解析，您可以使用其他的json包进行解析，此处仅为示例
                        json_result = new JSONObject(result);
//                        System.out.println(json_result);
                        if (json_result.getInt("code") == 0) status = false;
                    } catch (Exception e) {
                        System.out.println("error..." + e.getMessage());
                    }
                }

                Message message = handler.obtainMessage();
                message.what = 3;
                message.obj = json_result;
                handler.sendMessageDelayed(message, 0);
            }
        }.start();
    }

    void getEntities(String token){
        new Thread(){
            @Override
            public void run() {
                super.run();

                UrlHelper urlHelper = new UrlHelper(entityUrl);
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

                UrlHelper urlHelper = new UrlHelper(tripleUrl);
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

                UrlHelper urlHelper = new UrlHelper(entityUploadUrl);
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

                UrlHelper urlHelper = new UrlHelper(tripleUploadUrl);
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