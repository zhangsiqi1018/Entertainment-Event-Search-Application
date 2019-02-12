package com.example.files.eventsearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder> implements View.OnClickListener{
    private ArrayList<Map<String,String>> eventsList;
    private Map<String,Integer> icons;
    Context context;
    private Fragment favorFrag;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView eventName, venueName, date;
        public ImageView icon;
        public ImageButton favorBtn;


        public MyViewHolder(View view) {
            super(view);
            icon = (ImageView)view.findViewById(R.id.icon) ;
            eventName = (TextView) view.findViewById(R.id.eventName);
            venueName = (TextView) view.findViewById(R.id.venueName);
            date = (TextView) view.findViewById(R.id.date);
            favorBtn = (ImageButton)view.findViewById(R.id.favorBtn);

            itemView.setOnClickListener(EventsAdapter.this);
            favorBtn.setOnClickListener(EventsAdapter.this);
        }
    }


    public EventsAdapter(ArrayList<Map<String,String>> eventsList, Context context, Fragment favorFrag) {
        this.favorFrag = favorFrag;
        this.context = context;
        this.eventsList = eventsList;
        icons = new HashMap<>();
        icons.put("Music",R.drawable.music_icon);
        icons.put("Sports",R.drawable.sport_icon);
        icons.put("Arts & Theatre",R.drawable.art_icon);
        icons.put("Miscellaneous",R.drawable.music_icon);
        icons.put("Film",R.drawable.film_icon);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_table_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Map<String,String> each_event = eventsList.get(position);

        holder.itemView.setTag(position);
        holder.favorBtn.setTag(position);

        final String icon = each_event.get("category_event");
        Picasso.with(holder.itemView.getContext()).load(icons.get(icon)).into(holder.icon);

        final String eventName = each_event.get("eventName");
        holder.eventName.setText(eventName);

        String venueName_temp = "";
        if(each_event.containsKey("venueName")){
            venueName_temp = each_event.get("venueName");
            holder.venueName.setText(venueName_temp);
        }
        else{
            venueName_temp = "N/A";
            holder.venueName.setText(venueName_temp);
        }
        final String venueName = venueName_temp;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        String date_formatted_temp = "";
        String date_favor = "";
        String time_favor = "";
        try {
            date_favor = each_event.get("date");
            date = format.parse(date_favor);
            SimpleDateFormat format_temp = new SimpleDateFormat("MMM d, yyyy");
            date_formatted_temp = format_temp.format(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(each_event.containsKey("time")){
            time_favor = each_event.get("time");
            date_formatted_temp += " " + time_favor;
            holder.date.setText(date_formatted_temp);
        }
        else{
            holder.date.setText(date_formatted_temp);
        }
        final String favorEventDate = date_favor;
        final String favorEventTime = time_favor;

        //other information
        final String ticketApi = each_event.get("ticketApi");
        final String arts_array = each_event.get("arts_array");
        final String venue_loc = each_event.get("venue_loc");
        final String event_num = each_event.get("eventNum");
        final String event_url = each_event.get("eventUrl");

        final SharedPreferences sharedPref = context.getSharedPreferences("FavorFiles", Context.MODE_PRIVATE);
        final String eventId = each_event.get("eventId");
        if(sharedPref.contains(eventId)){
            holder.favorBtn.setImageResource(R.drawable.heart_fill_red);
        }
        else{
            holder.favorBtn.setImageResource(R.drawable.heart_outline_black);
        }

        holder.favorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();
                if(sharedPref.contains(eventId)){
                    holder.favorBtn.setImageResource(R.drawable.heart_outline_black);
                    editor.remove(eventId);
                    editor.apply();
                    Toast.makeText(context, eventName + "was removed from Favorites List!",Toast.LENGTH_SHORT).show();
                    if(favorFrag == null){
                        return;
                    }
                    else{
                        favorFrag.onResume();
                    }
                }
                else{
                    holder.favorBtn.setImageResource(R.drawable.heart_fill_red);
                    String eventInfo = "{\'favor_icon\':\"" + icon + "\",\'favor_eventName\':\"" + eventName+ "\",\'favor_venueName\':\"" + venueName + "\",\'favor_eventDate\':\"" + favorEventDate + "\",\'favor_eventTime\':\"" + favorEventTime + "\",\'favor_eventId\':\"" + eventId + "\",\'favor_ticketApi\':\"" + ticketApi + "\",\'favor_artsArray\':\"" + arts_array+ "\",\'favor_venueLoc\':\"" + venue_loc+ "\",\'favor_eventNum\':\"" + event_num+ "\",\'favor_eventUrl\':\"" + event_url+ "\"}";

                    Log.d("favor","Saved in favoraInfo: " + eventInfo);
                    editor.putString(eventId, eventInfo);
                    editor.apply();
                    Toast.makeText(context, eventName + "was added to Favorites List!",Toast.LENGTH_SHORT).show();
                    if(favorFrag == null){
                        return;
                    }
                    else{
                        favorFrag.onResume();
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    //item里面有多个控件可以点击（item+item内部控件）
    public enum ViewName {
        ITEM,
        PRACTISE
    }

    //自定义一个回调接口来实现Click和LongClick事件
    public interface OnItemClickListener  {
        void onItemClick(View v, ViewName viewName, int position);
        void onItemLongClick(View v);
    }

    private OnItemClickListener mOnItemClickListener;//声明自定义的接口

    //定义方法并传给外面的使用者
    public void setOnItemClickListener(OnItemClickListener  listener) {
        this.mOnItemClickListener  = listener;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();      //getTag()获取数据
        if (mOnItemClickListener != null) {
            switch (v.getId()){
                case R.id.recycler_view:
                    mOnItemClickListener.onItemClick(v, ViewName.PRACTISE, position);
                    break;
                default:
                    mOnItemClickListener.onItemClick(v, ViewName.ITEM, position);
                    break;
            }
        }
    }




}
