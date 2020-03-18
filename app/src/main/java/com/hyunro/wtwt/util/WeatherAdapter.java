package com.hyunro.wtwt.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hyunro.wtwt.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder>{
    public ArrayList<Map<String, Object>> weatherByThreeHoursArray = new ArrayList<>();
    Context context;

    public WeatherAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.recycler_weather_bythreehours, parent, false);

        return new WeatherAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> item = weatherByThreeHoursArray.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return weatherByThreeHoursArray.size();
    }

    public void addItem(Map<String, Object> item) {
        weatherByThreeHoursArray.add(item);
    }

    public void setItems(ArrayList<Map<String, Object>> items) {
        this.weatherByThreeHoursArray = items;
    }
    public Map<String, Object> getItem(int position) {
        return weatherByThreeHoursArray.get(position);
    }
    public void setItem(int position, Map<String, Object> item) {
        weatherByThreeHoursArray.set(position, item);
        return;
    }








    class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateByThreeHours;
        TextView timeByThreeHours;
        ImageView weatherByThreeHours;
        TextView weatherTextByThreeHours;
        TextView tempByThreeHours;
        TextView rainByThreeHours;
        TextView windByThreeHours;
        TextView humidityByThreeHours;

        public ViewHolder(View itemView) {
            super(itemView);

            dateByThreeHours = itemView.findViewById(R.id.dateByThreeHours);
            timeByThreeHours = itemView.findViewById(R.id.timeByThreeHours);
            weatherByThreeHours = itemView.findViewById(R.id.weatherByThreeHours);
            weatherTextByThreeHours = itemView.findViewById(R.id.weatherTextByThreeHours);
            tempByThreeHours = itemView.findViewById(R.id.tempByThreeHours);
            rainByThreeHours = itemView.findViewById(R.id.rainByThreeHours);
            windByThreeHours = itemView.findViewById(R.id.windByThreeHours);
            humidityByThreeHours = itemView.findViewById(R.id.humidityByThreeHours);
        }

        public void setItem(Map<String, Object> today) {
            dateByThreeHours.setText((String)today.get("fcstDate"));

            String fcstTime = (String)today.get("fcstTime");
            timeByThreeHours.setText(fcstTime+"시");

            String todaySkyCodeByThreeHours = (String)today.get("SKY")+(String)today.get("PTY");
            String flag = "1";
            if(fcstTime.equals("0") || fcstTime.equals("3") || fcstTime.equals("21") ) flag = "2";
            int todaySkyImageIdByThreeHours = context.getResources().getIdentifier( "ic_weather_"+flag+todaySkyCodeByThreeHours, "drawable",context.getPackageName());
            weatherByThreeHours.setImageResource(todaySkyImageIdByThreeHours);
            weatherTextByThreeHours.setText(skyText.get(todaySkyCodeByThreeHours));

            tempByThreeHours.setText((String)today.get("T3H")+"˚C");
            if(Integer.parseInt((String)today.get("fcstTime"))%6==0) {
                rainByThreeHours.setText((String)today.get("POP")+"% "+(String)today.get("R06")+"mm");
            }
            String[] weatherDirectionArray = {"북", "북동", "북동", "동", "동", "남동", "남동", "남", "남", "남서", "남서", "서", "서", "북서", "북서", "북", "북"};
            Double weatherDirectionAngle = Double.parseDouble((String)today.get("VEC"));
            windByThreeHours.setText(weatherDirectionArray[(int)(weatherDirectionAngle/22.5)]+" "+today.get("WSD")+"m/s");
            humidityByThreeHours.setText((String)today.get("REH")+"%");


        }
    }

    public static Map<String, String> skyText = new HashMap<String, String>() {{
        put("10", "맑음");
        put("11", "비");
        put("12","비/눈");
        put("13","눈");
        put("14","소나기");
        put("30","구름많음");
        put("31","구름많고\n비");
        put("32","구름많고\n비/눈");
        put("33","구름많고\n눈");
        put("34","구름많고\n소나기");
        put("40","흐림");
        put("41","흐리고\n비");
        put("42","흐리고\n비/눈");
        put("43","흐리고\n눈");
        put("44","흐리고\n소나기");
    }};
}
