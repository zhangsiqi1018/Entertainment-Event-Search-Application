package com.example.files.eventsearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private ArrayList<Map<String,String>> eventsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EventsAdapter mAdapter;
    private String url;
    public static final String RESULT_URL = "Result_url";
    public static final String EVENT_NUM = "Event_num";
    public static final String ARTS_ARRAY = "Arts_array";
    public static final String VENUE_LOC = "Venue_loc";
    public static final String VENUE_NAME = "Venue_name";
    public static final String EVENT_NAME = "Event_name";
    public static final String EVENT_URL = "Event_url";
    public static final String EVENT_DATE = "Event_date";
    public static final String EVENT_TIME = "Event_time";
    public static final String EVENT_ID = "Event_id";
    public static final String EVENT_CATEGORY = "Event_category";

    public Map<Integer, String[]> map_arts = new HashMap<>();
    public Map<Integer, double[]> map_venueLoc = new HashMap<>();
    public Map<Integer, String> map_venueName = new HashMap<>();
    public Map<Integer, String> map_eventName = new HashMap<>();
    public Map<Integer, String> map_eventUrl = new HashMap<>();
    public Map<Integer, String> map_eventDate = new HashMap<>();
    public Map<Integer, String> map_eventTime = new HashMap<>();
    public Map<Integer, String> map_eventId = new HashMap<>();
    public Map<Integer, String> map_eventCategory = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Search Results");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new EventsAdapter(eventsList, getApplicationContext(), new Fragment());

        recyclerView.setHasFixedSize(true);

        // vertical RecyclerView
        // keep movie_list_row.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        // horizontal RecyclerView
        // keep movie_list_row.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        // recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 16));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(MyItemClickListener);
        getResults();
    }

    public void onResume(){
        super.onResume();
        mAdapter.notifyDataSetChanged();

    }

    private EventsAdapter.OnItemClickListener MyItemClickListener = new EventsAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View v, EventsAdapter.ViewName viewName, int position) {
            //viewName可区分item及item内部控件
            Map<String,String> each_item = eventsList.get(position);
            switch (v.getId()){
                default:
                    getDetail(position);
                    Toast.makeText(getApplicationContext(), each_item.get("eventName") + " is selected!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onItemLongClick(View v) {

        }
    };

    public void getResults(){
        final ProgressBar spinner;
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        TextView tv = (TextView) findViewById(R.id.progressBarText);
        tv.setText("Searching Events...");
        findViewById(R.id.noResultText).setVisibility(View.INVISIBLE);
        url = getIntent().getStringExtra("Result_url");

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        //String url = "http://siqi-cs571h8-nodejs.us-east-2.elasticbeanstalk.com/formData?keyword=" + keyword_url + "&category=" + category + "&distance=" + distance_url + "&curLat=" + cur_lat + "&curLon=" + cur_lng + "&location=" + location_url + "&choice=" + loc_choice_url + "&d_choice=" + unit;
        Log.d("result-url: ", url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject obj = null;
                        int events_num = 0;
                        try {
                            obj = new JSONObject(response);
                            events_num = obj.getJSONObject("page").getInt("totalElements");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(events_num == 0){
                            findViewById(R.id.noResultText).setVisibility(View.VISIBLE);
                            //Log.d("events_num: ", events_num +"");
                        }
                        else{
                            findViewById(R.id.noResultText).setVisibility(View.INVISIBLE);
                            //Log.d("events_num: ", events_num +"");
                            ArrayList<Map<String,String>> eventsList_temp = getJson(response);
                            for(int i = 0; i < eventsList_temp.size(); i++){
                                eventsList.add(eventsList_temp.get(i));
                            }
                            //Log.d("event.size: ", eventsList.size() +"");
                        }

                        spinner.setVisibility(View.GONE);
                        findViewById(R.id.progressBarText).setVisibility(View.INVISIBLE);
                        mAdapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errText = "Getting Results of HTTPRequest is failed!";
                Log.d("resultActivity", String.valueOf(error));
                Toast.makeText(getApplicationContext(), errText, Toast.LENGTH_SHORT).show();

                spinner.setVisibility(View.GONE);
                findViewById(R.id.progressBarText).setVisibility(View.INVISIBLE);

            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);


    }


    public ArrayList<Map<String,String>> getJson(String response){
        ArrayList<Map<String,String>> eventsList = new ArrayList<>();
        String[] artsArray;
        double[] venueLoc_arr;
        JSONObject obj = null;
        try {
            obj = new JSONObject(response);
            JSONObject events = obj.getJSONObject("_embedded");
            //Log.d(TAG, obj.toString());
            JSONArray eventsTable = events.getJSONArray("events");
            //Log.d(TAG, eventsTable.toString());
            for (int i = 0; i < eventsTable.length(); i++) {
                Map<String,String> events_eachrow = new HashMap<>();

                JSONObject eventsRowInfo = eventsTable.getJSONObject(i);
                events_eachrow.put("ticketApi",url);
                events_eachrow.put("eventNum",i+"");
                //Log.d(TAG, eventsRowInfo.toString());
                if(eventsRowInfo.has("name")){
                    String eventName = eventsRowInfo.getString("name");
                    events_eachrow.put("eventName", eventName);
                    map_eventName.put(i, eventName);
                }
                JSONObject object = eventsRowInfo.getJSONObject("_embedded");
                JSONObject venue = object.getJSONArray("venues").getJSONObject(0);
                if(venue.has("location")){
                    venueLoc_arr = new double[2];
                    double lat = Double.parseDouble(venue.getJSONObject("location").getString("latitude"));
                    venueLoc_arr[0] = lat;
                    double lng = Double.parseDouble(venue.getJSONObject("location").getString("longitude"));
                    venueLoc_arr[1] = lng;
                    //Log.d("resultAct-venueLoc: ",i+": "+ venueLoc_arr[0] +" " + venueLoc_arr[1]);
                    events_eachrow.put("venue_loc", Arrays.toString(venueLoc_arr));
                    map_venueLoc.put(i, venueLoc_arr);
                }
                if(venue.has("name")){
                    String venueName = venue.getString("name");
                    events_eachrow.put("venueName", venueName);
                    map_venueName.put(i, venueName);
                }
                JSONObject dateObject = eventsRowInfo.getJSONObject("dates").getJSONObject("start");
                if(dateObject.has("localDate")){
                    String date = dateObject.getString("localDate");
                    events_eachrow.put("date", date);
                    map_eventDate.put(i, date);
                }
                if(dateObject.has("localTime")){
                    String time = dateObject.getString("localTime");
                    events_eachrow.put("time", time);
                    map_eventTime.put(i, time);
                }
                if(eventsRowInfo.has("id")){
                    String eventId = eventsRowInfo.getString("id");
                    events_eachrow.put("eventId", eventId);
                    map_eventId.put(i, eventId);
                }

                if(eventsRowInfo.has("url")){
                    String eventUrl = eventsRowInfo.getString("url");
                    events_eachrow.put("eventUrl", eventUrl);
                    map_eventUrl.put(i, eventUrl);
                }

                JSONObject category_event_obj = eventsRowInfo.getJSONArray("classifications").getJSONObject(0);
                if(category_event_obj.has("segment")){
                    String category_event = category_event_obj.getJSONObject("segment").getString("name");
                    events_eachrow.put("category_event", category_event);
                    map_eventCategory.put(i, category_event);
                }

                if(eventsRowInfo.getJSONObject("_embedded").has("attractions")) {
                    JSONArray arts_array = eventsRowInfo.getJSONObject("_embedded").getJSONArray("attractions");
                    if (arts_array != null && arts_array.length() != 0) {
                        artsArray = new String[arts_array.length()];
                        for (int j = 0; j < arts_array.length() - 1; j++) {
                            artsArray[j] = arts_array.getJSONObject(j).getString("name");
                        }
                        artsArray[arts_array.length() - 1] = arts_array.getJSONObject(arts_array.length() - 1).getString("name");
                        map_arts.put(i, artsArray);
                        events_eachrow.put("arts_array", Arrays.toString(artsArray));
                    }
                }
                Log.d("event_info", events_eachrow.toString());
                eventsList.add(events_eachrow);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return eventsList;

    }
    public void getDetail(int pos){
        //start the ResultActivity
        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
        //String url = "http://siqi-cs571h8-nodejs.us-east-2.elasticbeanstalk.com/formData?keyword=" + keyword_url + "&category=" + category + "&distance=" + distance_url + "&curLat=" + cur_lat + "&curLon=" + cur_lng + "&location=" + location_url + "&choice=" + loc_choice_url + "&d_choice=" + unit;
        Bundle bd = new Bundle();
        bd.putString(RESULT_URL, url);
        bd.putInt(EVENT_NUM, pos);
        String[] arts_arr = null;
        double[] venueLoc_array = null;
        String venue_name = "";
        String event_name = "";
        String event_url = "";
        String event_date = "";
        String event_time = "";
        String event_id = "";
        String event_c = "";
        if(map_arts.containsKey(pos)){
            arts_arr = map_arts.get(pos);
            //Log.d("resultActivity-array: ",arts_arr.length + "");
        }
        bd.putStringArray(ARTS_ARRAY, arts_arr);

        if(map_venueLoc.containsKey(pos)){
            venueLoc_array = map_venueLoc.get(pos);
            //Log.d("resultAct-venue: ",pos+" lat: "+venueLoc_array[0] +"");
        }
        bd.putDoubleArray(VENUE_LOC,venueLoc_array);

        if(map_venueName.containsKey(pos)){
            venue_name = map_venueName.get(pos);
            //Log.d("resultAct-ups: ",pos+": "+ venue_name +"");
        }
        bd.putString(VENUE_NAME, venue_name);

        if(map_eventName.containsKey(pos)){
            event_name = map_eventName.get(pos);
            //Log.d("resultAct-ups: ",pos+": "+ event_name +"");
        }
        bd.putString(EVENT_NAME, event_name);

        if(map_eventUrl.containsKey(pos)){
            event_url = map_eventUrl.get(pos);
            //Log.d("resultAct-ups: ",pos+": "+ event_url +"");
        }
        bd.putString(EVENT_URL, event_url);

        if(map_eventDate.containsKey(pos)){
            event_date = map_eventDate.get(pos);
            //Log.d("resultAct-ups: ",pos+": "+ event_date +"");
        }
        bd.putString(EVENT_DATE, event_date);

        if(map_eventTime.containsKey(pos)){
            event_time = map_eventTime.get(pos);
            //Log.d("resultAct-ups: ",pos+": "+ event_time +"");
        }
        bd.putString(EVENT_TIME, event_time);

        if(map_eventId.containsKey(pos)){
            event_id = map_eventId.get(pos);
            //Log.d("resultAct-ups: ",pos+": "+ event_id +"");
        }
        bd.putString(EVENT_ID, event_id);

        if(map_eventCategory.containsKey(pos)){
            event_c = map_eventCategory.get(pos);
            //Log.d("resultAct-ups: ",pos+": "+ event_c +"");
        }
        bd.putString(EVENT_CATEGORY, event_c);
        intent.putExtras(bd);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
