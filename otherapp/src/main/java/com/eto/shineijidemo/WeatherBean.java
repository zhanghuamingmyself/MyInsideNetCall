package com.eto.shineijidemo;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/10.
 */

public class WeatherBean extends JsonBean{
    public WeatherData data;
    public String message;
    public String city;
    public  int status;



    public static  class WeatherData extends JsonBean{
        public TodayData yesterday;
        public List<TodayData> forecast;
        public String ganmao;
        public String wendu;
        public String shidu ;
        //public int pm25 ;
       // public int pm10 ;
        public String quality  ;



        public WeatherData(){
            forecast = new ArrayList<TodayData>();
        }


        @Override
        public void fromJson(JSONObject jobj) {
            forecast.add(new TodayData());
            super.fromJson(jobj);
            forecast.remove(0);
        }
    }


    public static class TodayData extends JsonBean{

        public TodayData(){
            System.out.println("create TodayData");
        }
        public String date;
        public String high;
        public String low;
        public String fl;
        public String fx;
        public String type;
       // public int aqi;
        public String notice;
        public String sunrise;
        public String sunset;
//"date": "27日星期六",
// "high": "高温 34℃",
// "fengli": "微风级",
// "low": "低温 21℃",
// "fengxiang": "西南风",
// "type": "晴"
}
}
