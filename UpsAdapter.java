package com.example.files.eventsearch;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;

public class UpsAdapter extends RecyclerView.Adapter<UpsAdapter.MyViewHolder>{
    private ArrayList<Map<String,String>> upEventsList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView up_cv;
        public TextView up_name, up_artist, up_date, up_type;




        public MyViewHolder(View view) {
            super(view);
            up_cv = (CardView)view.findViewById(R.id.cv);;
            up_name = (TextView) view.findViewById(R.id.up_name);
            up_artist = (TextView) view.findViewById(R.id.up_artist);
            up_date = (TextView) view.findViewById(R.id.up_date);
            up_type = (TextView) view.findViewById(R.id.up_type);


        }
    }


    public UpsAdapter(ArrayList<Map<String,String>> upEventsList) {

        this.upEventsList = upEventsList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ups_table_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Map<String,String> each_Upevent = upEventsList.get(position);
        holder.up_name.setText(each_Upevent.get("upEventName"));
        holder.up_artist.setText(each_Upevent.get("upEventArts"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        String date_formatted = "";
        try {
            date = format.parse(each_Upevent.get("upEventDate"));
            SimpleDateFormat format_temp = new SimpleDateFormat("MMM d, yyyy");
            date_formatted = format_temp.format(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(each_Upevent.containsKey("upEventTime")){
            holder.up_date.setText(date_formatted + " " + each_Upevent.get("upEventTime"));
        }
        else{
            holder.up_date.setText(date_formatted);
        }
        holder.up_type.setText("Type: "+each_Upevent.get("upEventType"));
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return upEventsList.size();
    }
}
