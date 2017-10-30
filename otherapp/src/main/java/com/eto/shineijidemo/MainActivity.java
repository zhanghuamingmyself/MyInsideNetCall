package com.eto.shineijidemo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;


import com.eto.udpvideo.mamager.VideoCall;
import com.eto.udpvideo.receiver.MessageUtils;

import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

public class MainActivity extends Activity {

    EditText edit01 ;
    LinearLayout layout_local;
    LinearLayout layout_remote;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        edit01 = (EditText) findViewById(R.id.edit01);
        layout_local = (LinearLayout) findViewById(R.id.layout_local);
        layout_remote = (LinearLayout) findViewById(R.id.layout_remote);


        initCall();
        edit01.setText("192.168.3.105");

//        setWifiApEnabled(this,true,"android");
        test();
        getDeviceInfo();

    }

    private String getDeviceInfo(){
        StringBuffer sb =new StringBuffer();
        sb.append("主板："+ Build.BOARD);
        sb.append("\n系统启动程序版本号："+ Build.BOOTLOADER);
        sb.append("\n系统定制商："+Build.BRAND);
        sb.append("\ncpu指令集："+Build.CPU_ABI);
        sb.append("\ncpu指令集2："+Build.CPU_ABI2);
        sb.append("\n设置参数："+Build.DEVICE);
        sb.append("\n显示屏参数："+Build.DISPLAY);
//        sb.append("\n无线电固件版本："+Build.getRadioVersion());
        sb.append("\n硬件识别码："+Build.FINGERPRINT);
        sb.append("\n硬件名称："+Build.HARDWARE);
        sb.append("\nHOST:"+Build.HOST);
        sb.append("\n修订版本列表："+Build.ID);
        sb.append("\n硬件制造商："+Build.MANUFACTURER);
        sb.append("\n版本："+Build.MODEL);
        sb.append("\n硬件序列号："+Build.SERIAL);
        sb.append("\n手机制造商："+Build.PRODUCT);
        sb.append("\n描述Build的标签："+Build.TAGS);
        sb.append("\nTIME:"+Build.TIME);
        sb.append("\nbuilder类型："+Build.TYPE);
        sb.append("\nUSER:"+Build.USER);
        Log.d("getDeviceInfo",sb.toString());
        return sb.toString();
    }



    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn01:
                String ip= edit01.getText().toString().trim();
                VideoCall.call(ip, MessageUtils.CallType.CALLTYPE_VIDEO);

                break;
            case R.id.btn02:
                VideoCall.answer();
                break;
            case R.id.btn03:
                VideoCall.hangUp();
                break;
        }
    }


    void initCall(){
        VideoCall.setDebug(true);
        VideoCall.addCallStateListener(new VideoCall.CallStateListener(){

            @Override
            public void onHangUp(String callId) {
                log("listerer:"+"onHangUp");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        VideoCall.stopRefreshCamra();
                    }
                });
            }

            @Override
            public void onDialFailed(String callId) {
                log("listerer:"+"onDialFailed"+callId);
            }

            @Override
            public void onAnswer( String callId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        VideoCall.refreshCamra();
//                        VideoCall.setOutVolume(3.0f);
                        VideoCall.setInputVolume(3.0f);
                    }
                });

            }

            @Override
            public void onAlerting(String callId) {
                log("listerer:"+"onAlerting");
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        VideoCall.sendPreImage();
//                    }
//                });
            }

            @Override
            public void onIncomingCall(String callId, MessageUtils.CallType callType, String data) {
                log("listerer:"+"onIncomingCall");
//                if(VideoCall.getCallType() == MessageUtils.CallType.CALLTYPE_VIEWDOOR) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //VideoCall.sendPreImage();
                            Toast.makeText(mContext,"有来电....",Toast.LENGTH_LONG).show();
                            VideoCall.answer();
                        }
                    });
//                }
            }

            @Override
            public void onOpenDoor(String data) {
                log("listerer:"+"onOpenDoor "+data);
            }

            @Override
            public void InitError(final String data) {
                log("listerer:"+"InitError");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext,"初始化失败->"+data,Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onRecBoardcastFangHao(String data,String ip) {
                try {
                    JSONObject jobj = new JSONObject(data);
                    String fangHao = jobj.getString("fangHao");
                    String ip1 = jobj.getString("ip");
                    String mac = jobj.getString("mac");

                    VideoCall.sendMenKouJiIp(getLanIpAddress(),VideoCall.DEV_TYPE_MENKOUJI,ip1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onReturnBoardcastFangHao(String data,String ip) {

            }

            @Override
            public void onSendData(String data,String ip) {

            }

            @Override
            public void onSendRetData(String data,String ip) {

            }

            @Override
            public void onGetData(String data,String ip) {

            }

            @Override
            public void onGetRetData(String data,String ip) {

            }
        },this,"6e8d082f20ecf5845859d69a17053b94",false);

        setTitle(getLocalIpAddress());
        VideoCall.initCameraConfig(this,layout_local,layout_remote);


    }

    public String getlocalIp(){
        WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        WifiInfo mWifiInfo = wifiManager.getConnectionInfo();
        if(mWifiInfo ==null){
            return "ip=null";
        }else{
            int ipAddress=mWifiInfo.getIpAddress();
            Log.e("222","!!=="+ipAddress+"===");
            if(ipAddress==0)  return "ip=null";
            return "ip="+((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."
                    +(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));
        }
    }

    public static  String getLocalIpAddress()
    {

        String ips = "";
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress()))
                    {
                        Log.e("IpAddress", inetAddress.getHostAddress());
                        ips+=inetAddress.getHostAddress()+"\n";
                        inetAddress.getAddress();
                    }
                }
            }
        }
        catch (Exception ex)
        {
            Log.e(" IpAddress", ex.toString());
        }
        return ips;
    }

    public static  String getLanIpAddress()
    {

        String ips = "";
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress()))
                    {
                        String ip = inetAddress.getHostAddress();
                        Log.e("IpAddress", ip);
                        if(ip.endsWith(".1")){
                            continue;
                        }
                        ips+=ip;

                    }
                }
            }
        }
        catch (Exception ex)
        {
            Log.e(" IpAddress", ex.toString());
        }
        return ips;
    }


    public void log(String log){
        System.out.println("###>:MainActivity:"+log);
    }

    void test(){
//        String json = "{\"message\":\"Success !\",\"status\":200,\"city\":\"中山市\",\"count\":18,\"data\":{\"shidu\":\"92%\",\"wendu\":\"27\",\"ganmao\":\"-\",\"yesterday\":{\"date\":\"09日星期三\",\"sunrise\":\"06:00\",\"high\":\"高温 33.0℃\",\"low\":\"低温 27.0℃\",\"sunset\":\"19:03\",\"aqi\":23.0,\"fx\":\"无持续风向\",\"fl\":\"<3级\",\"type\":\"雷阵雨\",\"notice\":\"空旷场地不要使用有金属尖端的雨伞\"},\"forecast\":[{\"date\":\"10日星期四\",\"sunrise\":\"06:01\",\"high\":\"高温 32.0℃\",\"low\":\"低温 26.0℃\",\"sunset\":\"19:02\",\"aqi\":29.0,\"fx\":\"无持续风向\",\"fl\":\"<3级\",\"type\":\"雷阵雨\",\"notice\":\"雷雨较大时要远离树木，选择建筑物躲雨\"},{\"date\":\"11日星期五\",\"sunrise\":\"06:01\",\"high\":\"高温 32.0℃\",\"low\":\"低温 27.0℃\",\"sunset\":\"19:01\",\"aqi\":34.0,\"fx\":\"无持续风向\",\"fl\":\"<3级\",\"type\":\"雷阵雨\",\"notice\":\"空旷场地不要使用有金属尖端的雨伞\"},{\"date\":\"12日星期六\",\"sunrise\":\"06:01\",\"high\":\"高温 33.0℃\",\"low\":\"低温 28.0℃\",\"sunset\":\"19:01\",\"aqi\":35.0,\"fx\":\"无持续风向\",\"fl\":\"<3级\",\"type\":\"多云\",\"notice\":\"今日多云，骑上单车去看看世界吧\"},{\"date\":\"13日星期日\",\"sunrise\":\"06:02\",\"high\":\"高温 34.0℃\",\"low\":\"低温 28.0℃\",\"sunset\":\"19:00\",\"aqi\":36.0,\"fx\":\"无持续风向\",\"fl\":\"<3级\",\"type\":\"多云\",\"notice\":\"今日多云，骑上单车去看看世界吧\"},{\"date\":\"14日星期一\",\"sunrise\":\"06:02\",\"high\":\"高温 35.0℃\",\"low\":\"低温 28.0℃\",\"sunset\":\"18:59\",\"aqi\":38.0,\"fx\":\"无持续风向\",\"fl\":\"<3级\",\"type\":\"多云\",\"notice\":\"绵绵的云朵，形状千变万化\"}]}}";
//        WeatherBean wb = new WeatherBean();
//        wb.fromJson(json);
//        System.out.println(wb.city);
    }



    public static boolean setWifiApEnabled(Context context,boolean enabled,String ssid) {


        String networkSSID = ssid;



        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (enabled) { // disable WiFi in any case
                //wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi

                wifiManager.setWifiEnabled(false);
    }
        try {
            //热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            //配置热点的名称(可以在名字后面加点随机数什么的)
            apConfig.SSID = networkSSID;
            apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);


            //通过反射调用设置热点
            Method method = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            //返回热点打开状态
            return (Boolean) method.invoke(wifiManager, apConfig, enabled);
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }
}
