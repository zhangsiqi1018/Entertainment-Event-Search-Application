package com.example.files.eventsearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class DetailActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static String url;
    private static int event_num;
    private static String[] artsArray;
    private static String artsArray_str;
    private static double[] venueLocArray;
    private static String venueLocArray_str;
    private static String venueName;
    private static String eventName;
    private static String eventUrl;
    private static String eventDate;
    private static String eventTime;
    private static String eventId;
    private static String eventCategory;
    private static boolean music_segment;

    private infoFragment infoFrag;
    private artsFragment artsFrag;
    private  venueFragment venueFrag;
    private upsFragment upsFrag;

    private static ArrayList<UpEvents> eventDefaultOrder;
    private static ArrayList<UpEvents> eventNameOrder;
    private static ArrayList<UpEvents> eventTimeOrder;
    private static ArrayList<UpEvents> eventArtsOrder;
    private static ArrayList<UpEvents> eventTypeOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final int[] ICONS = new int[] {
                R.drawable.info_icon,
                R.drawable.art_tab,
                R.drawable.venue_tab,
                R.drawable.upcoming_tab,
        };

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        infoFrag = new infoFragment();
        artsFrag = new artsFragment();
        venueFrag = new venueFragment();
        upsFrag = new upsFragment();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.getTabAt(0).setIcon(ICONS[0]);
        tabLayout.getTabAt(1).setIcon(ICONS[1]);
        tabLayout.getTabAt(2).setIcon(ICONS[2]);
        tabLayout.getTabAt(3).setIcon(ICONS[3]);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        Bundle bd = getIntent().getExtras();
        url = bd.getString("Result_url");
        event_num = bd.getInt("Event_num");
        artsArray = bd.getStringArray("Arts_array");
        venueLocArray = bd.getDoubleArray("Venue_loc");
        artsArray_str = Arrays.toString(artsArray);
        venueLocArray_str = Arrays.toString(venueLocArray);
        venueName = bd.getString("Venue_name");
        eventName = bd.getString("Event_name");
        eventUrl = bd.getString("Event_url");
        eventDate = bd.getString("Event_date");
        eventTime = bd.getString("Event_time");
        eventId = bd.getString("Event_id");
        eventCategory = bd.getString("Event_category");


        String app_title = eventName;
        Toolbar detail_toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        detail_toolbar.setTitle(app_title);

        final SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("FavorFiles", Context.MODE_PRIVATE);
        final ImageButton favorBtn = (ImageButton)findViewById(R.id.favorDetailBtn);
        if(sharedPref.contains(eventId)){
            favorBtn.setImageResource(R.drawable.heart_fill_red);
        }
        else{
            favorBtn.setImageResource(R.drawable.heart_fill_white);
        }
        favorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();
                if(sharedPref.contains(eventId)){
                    favorBtn.setImageResource(R.drawable.heart_fill_white);
                    editor.remove(eventId);
                    editor.apply();
                    Toast.makeText(getApplicationContext(), eventName + "was removed from Favorites List!",Toast.LENGTH_SHORT).show();
                }
                else{
                    favorBtn.setImageResource(R.drawable.heart_fill_red);
                    String eventInfo = "{\'favor_icon\':\"" + eventCategory + "\",\'favor_eventName\':\"" + eventName+ "\",\'favor_venueName\':\"" + venueName + "\",\'favor_eventDate\':\"" + eventDate + "\",\'favor_eventTime\':\"" + eventTime + "\",\'favor_eventId\':\"" + eventId + "\",\'favor_ticketApi\':\"" + url + "\",\'favor_artsArray\':\"" + artsArray_str+ "\",\'favor_venueLoc\':\"" + venueLocArray_str+ "\",\'favor_eventNum\':\"" + event_num+ "\",\'favor_eventUrl\':\"" + eventUrl+ "\"}";

                   // String eventInfo = "{\'favor_icon\':\"" + eventCategory + "\",\'favor_eventName\':\"" + eventName+ "\",\'favor_venueName\':\"" + venueName + "\",\'favor_eventDate\':\"" + eventDate + "\",\'favor_eventTime\':\"" + eventTime+ "\",\'favor_eventId\':\"" + eventId + "\"}";
                    Log.d("favor","Saved in favoraInfo: " + eventInfo);
                    editor.putString(eventId, eventInfo);
                    editor.apply();
                    Toast.makeText(getApplicationContext(), eventName + "was added to Favorites List!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton twitterBtn = (ImageButton)findViewById(R.id.twitterBtn);
        twitterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String twitter_url = "https://twitter.com/intent/tweet?text=Check out " + eventName + " located at " + venueName +". Website: " + eventUrl;
                Uri twitter = Uri.parse(twitter_url);
                Intent gotoTwitter = new Intent(Intent.ACTION_VIEW, twitter);
                if(gotoTwitter.resolveActivity(getPackageManager()) != null){
                    startActivity(gotoTwitter);
                }
            }
        });




    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public static class infoFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            getInfo(rootView);
            return rootView;
        }


        public void getInfo(final View rootview){
            final ProgressBar spinner;
            spinner = (ProgressBar)rootview.findViewById(R.id.info_progressBar);
            spinner.setVisibility(View.VISIBLE);
            TextView tv = (TextView) rootview.findViewById(R.id.info_progressBarText);
            tv.setText("Searching Details...");

            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(getContext());
            //String url = "http://siqi-cs571h8-nodejs.us-east-2.elasticbeanstalk.com/formData?keyword=" + keyword_url + "&category=" + category + "&distance=" + distance_url + "&curLat=" + cur_lat + "&curLon=" + cur_lng + "&location=" + location_url + "&choice=" + loc_choice_url + "&d_choice=" + unit;
            Log.d("detail-getInfo: ", url);

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.d("send request: ","send info");
                            getEventJson(rootview, response);
                            spinner.setVisibility(View.GONE);
                            rootview.findViewById(R.id.info_progressBarText).setVisibility(View.INVISIBLE);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errText = "Getting Results of HTTPRequest is failed!";
                    Log.d("detail: ", String.valueOf(error));
                    Toast.makeText(getContext(), errText, Toast.LENGTH_SHORT).show();

                    spinner.setVisibility(View.GONE);
                    rootview.findViewById(R.id.info_progressBarText).setVisibility(View.INVISIBLE);

                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }

        public void getEventJson(View rootview, String response){
            //ArrayList<Map<String,String>> eventsList = new ArrayList<>();
            try {
                int marginIndex = 50;
                JSONObject jsonObj = new JSONObject(response);
                JSONObject events = jsonObj.getJSONObject("_embedded");
                JSONObject c_event = events.getJSONArray("events").getJSONObject(event_num);

                //Event info--arts part
                if(c_event.getJSONObject("_embedded").has("attractions")){
                    JSONArray arts_array = c_event.getJSONObject("_embedded").getJSONArray("attractions");
                    if(arts_array != null && arts_array.length() != 0){
                        String  arts = "";
                        int arts_length = 0;
                        for(int i = 0; i < arts_array.length() - 1; i++){
                            arts += arts_array.getJSONObject(i).getString("name") +  " | ";
                            arts_length += arts_array.getJSONObject(i).getString("name").length();
                        }
                        arts += arts_array.getJSONObject(arts_array.length() - 1).getString("name");
                        arts_length += arts_array.getJSONObject(arts_array.length() - 1).length();

                        TextView tv = (TextView) rootview.findViewById(R.id.artText);
                        tv.setText(arts);
                        tv.setVisibility(TextView.VISIBLE);
                        TextView tV = (TextView) rootview.findViewById(R.id.artTitle);
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv.getLayoutParams();
                        lp.setMargins(280, marginIndex, 0, marginIndex);
                        tv.setLayoutParams(lp);
                        RelativeLayout.LayoutParams lP = (RelativeLayout.LayoutParams) tV.getLayoutParams();
                        lP.setMargins(0, marginIndex, 0, marginIndex);
                        tV.setLayoutParams(lP);
                        marginIndex = marginIndex + arts_length +150;
                    }
                    else{
                        TextView tv = (TextView) rootview.findViewById(R.id.artTitle);
                        tv.setVisibility(TextView.INVISIBLE);
                        TextView tV = (TextView) rootview.findViewById(R.id.artText);
                        tV.setVisibility(TextView.INVISIBLE);
                    }
                }
                else{
                    TextView tv = (TextView) rootview.findViewById(R.id.artTitle);
                    tv.setVisibility(TextView.INVISIBLE);
                    TextView tV = (TextView) rootview.findViewById(R.id.artText);
                    tV.setVisibility(TextView.INVISIBLE);
                }



                //Event info--venue part
                JSONObject venue_obj = c_event.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0);
                String venue_name = venue_obj.getString("name");
                if(venue_name != null && venue_name.length() != 0 && !venue_name.isEmpty()){
                    TextView tv = (TextView) rootview.findViewById(R.id.venueText);
                    tv.setText(venue_name);
                    tv.setVisibility(TextView.VISIBLE);
                    TextView tV = (TextView) rootview.findViewById(R.id.venueTitle);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv.getLayoutParams();
                    lp.setMargins(280, marginIndex, 0, marginIndex);
                    tv.setLayoutParams(lp);
                    RelativeLayout.LayoutParams lP = (RelativeLayout.LayoutParams) tV.getLayoutParams();
                    lP.setMargins(0, marginIndex, 0, marginIndex);
                    tV.setLayoutParams(lP);
                    marginIndex = marginIndex + 100;
                }
                else{
                    TextView tv = (TextView) rootview.findViewById(R.id.venueTitle);
                    tv.setVisibility(TextView.INVISIBLE);
                    TextView tV = (TextView) rootview.findViewById(R.id.venueText);
                    tV.setVisibility(TextView.INVISIBLE);
                }


                //Event info--time part
                JSONObject date_obj = c_event.getJSONObject("dates").getJSONObject("start");
                if(date_obj.has("localDate") ){
                    String date_str = date_obj.getString("localDate");
                    TextView tv = (TextView) rootview.findViewById(R.id.timeText);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = null;
                    String date_formatted = "";
                    try {
                        date = format.parse(date_str);
                        SimpleDateFormat format_temp = new SimpleDateFormat("MMM d, yyyy");
                        date_formatted = format_temp.format(date.getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(date_obj.has("localTime")){
                        String time = date_obj.getString("localTime");
                        String time_info = date_formatted + " " + time;
                        tv.setText(time_info);
                    }
                    else{
                        tv.setText(date_formatted);
                    }
                    tv.setVisibility(TextView.VISIBLE);
                    TextView tV = (TextView) rootview.findViewById(R.id.timeTitle);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv.getLayoutParams();
                    lp.setMargins(280, marginIndex, 0, marginIndex);
                    tv.setLayoutParams(lp);
                    RelativeLayout.LayoutParams lP = (RelativeLayout.LayoutParams) tV.getLayoutParams();
                    lP.setMargins(0, marginIndex, 0, marginIndex);
                    tV.setLayoutParams(lP);
                    marginIndex = marginIndex + 100;
                }
                else{
                    TextView tv = (TextView) rootview.findViewById(R.id.timeTitle);
                    tv.setVisibility(TextView.INVISIBLE);
                    TextView tV = (TextView) rootview.findViewById(R.id.timeText);
                    tV.setVisibility(TextView.INVISIBLE);
                }

                //Event info--category part
                JSONObject category_obj = c_event.getJSONArray("classifications").getJSONObject(0);
                if(category_obj.has("genre") || category_obj.has("segment")){
                    String genre = "";
                    String segment = "";
                    if(category_obj.has("genre")){
                        genre = category_obj.getJSONObject("genre").getString("name");
                    }
                    if(category_obj.has("segment")){
                        segment = category_obj.getJSONObject("segment").getString("name");
                        if(segment.equals("Music")){
                            music_segment = true;
                        }
                        else{
                            music_segment = false;
                        }
                    }
                    String category = segment + " | " + genre;
                    //Log.d("detail",category);
                    TextView tv = (TextView) rootview.findViewById(R.id.categoryText);
                    tv.setText(category);
                    tv.setVisibility(TextView.VISIBLE);
                    TextView tV = (TextView) rootview.findViewById(R.id.categoryTitle);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv.getLayoutParams();
                    lp.setMargins(280, marginIndex, 0, marginIndex);
                    tv.setLayoutParams(lp);
                    RelativeLayout.LayoutParams lP = (RelativeLayout.LayoutParams) tV.getLayoutParams();
                    lP.setMargins(0, marginIndex, 0, marginIndex);
                    tV.setLayoutParams(lP);
                    marginIndex = marginIndex + 100;
                }
                else{
                    TextView tv = (TextView) rootview.findViewById(R.id.categoryTitle);
                    tv.setVisibility(TextView.INVISIBLE);
                    TextView tV = (TextView) rootview.findViewById(R.id.categoryText);
                    tV.setVisibility(TextView.INVISIBLE);
                }

                //Event info--priceRange part
                if(c_event.has(("priceRanges"))){
                    JSONObject price_obj = c_event.getJSONArray("priceRanges").getJSONObject(0);
                    if(price_obj.has("min") || price_obj.has("max")){
                        String priceRange = "";
                        if(price_obj.has("min") && price_obj.has("max")){
                            String price_min = price_obj.getString("min");
                            String price_max = price_obj.getString("max");
                            priceRange = "$" + price_min + " ~ " +"$" + price_max;
                        }
                        else if(price_obj.has("min")){
                            priceRange = "$" + price_obj.getString("min");
                        }
                        else if(price_obj.has("max")){
                            priceRange = "$" + price_obj.getString("max");
                        }
                        Log.d("detail", priceRange);
                        TextView tv = (TextView) rootview.findViewById(R.id.priceText);
                        tv.setText(priceRange);
                        tv.setVisibility(TextView.VISIBLE);
                        TextView tV = (TextView) rootview.findViewById(R.id.priceTitle);
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv.getLayoutParams();
                        lp.setMargins(280, marginIndex, 0, marginIndex);
                        tv.setLayoutParams(lp);
                        RelativeLayout.LayoutParams lP = (RelativeLayout.LayoutParams) tV.getLayoutParams();
                        lP.setMargins(0, marginIndex, 0, marginIndex);
                        tV.setLayoutParams(lP);
                        marginIndex = marginIndex + 100;
                    }
                }

                else{
                    TextView tv = (TextView) rootview.findViewById(R.id.priceTitle);
                    tv.setVisibility(TextView.INVISIBLE);
                    TextView tV = (TextView) rootview.findViewById(R.id.priceText);
                    tV.setVisibility(TextView.INVISIBLE);
                }

                //Event info--ticketStatus part
                JSONObject ticket_obj = c_event.getJSONObject("dates");
                if(ticket_obj.has("status")){
                    String ticket_status = ticket_obj.getJSONObject("status").getString("code");
                    TextView tv = (TextView) rootview.findViewById(R.id.ticketText);
                    tv.setText(ticket_status);
                    tv.setVisibility(TextView.VISIBLE);
                    TextView tV = (TextView) rootview.findViewById(R.id.ticketTitle);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv.getLayoutParams();
                    lp.setMargins(280, marginIndex, 0, marginIndex);
                    tv.setLayoutParams(lp);
                    RelativeLayout.LayoutParams lP = (RelativeLayout.LayoutParams) tV.getLayoutParams();
                    lP.setMargins(0, marginIndex, 0, marginIndex);
                    tV.setLayoutParams(lP);
                    marginIndex = marginIndex + 80;
                }
                else{
                    TextView tv = (TextView) rootview.findViewById(R.id.ticketTitle);
                    tv.setVisibility(TextView.INVISIBLE);
                    TextView tV = (TextView) rootview.findViewById(R.id.ticketText);
                    tV.setVisibility(TextView.INVISIBLE);
                }


                //Event info--buy_ticket_at part
                if(c_event.has("url")){
                    //Log.d("lalala","hava ticket");
                    String buy_ticket = c_event.getString("url");
                    //Log.d("lalala",buy_ticket);
                    TextView tv = (TextView) rootview.findViewById(R.id.buy_ticketText);
                    tv.setText(Html.fromHtml("<a href='" + buy_ticket +"'>TicketMaster</a>"));
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setVisibility(TextView.VISIBLE);
                    TextView tV = (TextView) rootview.findViewById(R.id.buy_ticketTitle);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv.getLayoutParams();
                    lp.setMargins(280, marginIndex, 0, marginIndex);
                    tv.setLayoutParams(lp);
                    RelativeLayout.LayoutParams lP = (RelativeLayout.LayoutParams) tV.getLayoutParams();
                    lP.setMargins(0, marginIndex, 0, marginIndex);
                    tV.setLayoutParams(lP);
                    marginIndex = marginIndex + 120;
                }
                else{
                    TextView tv = (TextView) rootview.findViewById(R.id.buy_ticketTitle);
                    tv.setVisibility(TextView.INVISIBLE);
                    TextView tV = (TextView) rootview.findViewById(R.id.buy_ticketText);
                    tV.setVisibility(TextView.INVISIBLE);
                }


                //Event info--seat_map part
                if(c_event.has("seatmap")){
                    String seat_map = c_event.getJSONObject("seatmap").getString("staticUrl");
                    TextView tv = (TextView) rootview.findViewById(R.id.seatmapText);
                    tv.setText(Html.fromHtml("<a href='" + seat_map +"'>View Here</a>"));
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setVisibility(TextView.VISIBLE);
                    TextView tV = (TextView) rootview.findViewById(R.id.seatmapTitle);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv.getLayoutParams();
                    lp.setMargins(280, marginIndex, 0, marginIndex);
                    tv.setLayoutParams(lp);
                    RelativeLayout.LayoutParams lP = (RelativeLayout.LayoutParams) tV.getLayoutParams();
                    lP.setMargins(0, marginIndex, 0, marginIndex);
                    tV.setLayoutParams(lP);
                }
                else{
                    TextView tv = (TextView) rootview.findViewById(R.id.seatmapTitle);
                    tv.setVisibility(TextView.INVISIBLE);
                    TextView tV = (TextView) rootview.findViewById(R.id.seatmapText);
                    tV.setVisibility(TextView.INVISIBLE);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
    public static class artsFragment extends Fragment {


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_arts, container, false);
            getArts(rootView);
            return rootView;
        }
        public void getArts(final View rootview){

            int arts_size = 0;
            if(artsArray == null){
                TextView no_arts = (TextView) rootview.findViewById(R.id.noArtsText);
                no_arts.setVisibility(TextView.VISIBLE);
                return;
            }
            else{
                TextView no_arts = (TextView) rootview.findViewById(R.id.noArtsText);
                no_arts.setVisibility(TextView.INVISIBLE);
                if(artsArray.length >= 2){
                    arts_size = 2;
                    Log.d("arts-size",""+arts_size);
                }
                else{
                    arts_size = artsArray.length;
                    Log.d("arts-size",""+arts_size);
                }
            }

            for(int i = 0; i < arts_size; i++){
                String arts_name = artsArray[i];
                String arts_name_url = "";
                try {
                    arts_name_url = URLEncoder.encode(arts_name, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getContext());
                Log.d("detail-artsname: ", arts_name_url);
                String artsPhotos_url = "http://siqi-cs571h8-nodejs.us-east-2.elasticbeanstalk.com/ArtistTeamPhotos?art_teams=" + arts_name_url;
                Log.d("detail-photo_url: ",artsPhotos_url);

                // Request a string response from the provided URL.
                final String finalArts_name_url = arts_name_url;
                StringRequest stringRequest = new StringRequest(Request.Method.GET,artsPhotos_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {

                                if(music_segment == true){

                                    // Instantiate the RequestQueue.
                                    RequestQueue queue = Volley.newRequestQueue(getContext());
                                    String artsSpotify_url = "http://siqi-cs571h8-nodejs.us-east-2.elasticbeanstalk.com/Music_ArtistTeam?art_teams=" + finalArts_name_url;
                                    Log.d("detail-spotify: ", artsSpotify_url);

                                    // Request a string response from the provided URL.
                                    StringRequest stringRequest = new StringRequest(Request.Method.GET, artsSpotify_url,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response_music) {
                                                    getMusicArtsJson(rootview, response, response_music);

                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            String errText = "Getting Results of HTTPRequest is failed!";
                                            Log.d("detailActivity", String.valueOf(error));
                                            Toast.makeText(getContext(), errText, Toast.LENGTH_SHORT).show();


                                        }
                                    });

                                    // Add the request to the RequestQueue.
                                    queue.add(stringRequest);
                                }
                                else{
                                    getArtsJson(rootview, response);

                                }


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errText = "Getting Results of HTTPRequest is failed!";
                        Toast.makeText(getContext(), errText, Toast.LENGTH_SHORT).show();

                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        }

        public void getArtsJson(View rootview, String response){
            try {
                JSONObject jsonObj = new JSONObject(response);
                String arts_name = jsonObj.getJSONObject("queries").getJSONArray("request").getJSONObject(0).getString("searchTerms");
                LinearLayout layout = (LinearLayout)rootview.findViewById(R.id.artsLayout);
                TextView title_TV = new TextView(getContext());
                title_TV.setText(arts_name);
                title_TV.setTextSize(16);
                title_TV.setGravity(Gravity.CENTER);
                TextPaint tp = title_TV.getPaint();
                tp.setFakeBoldText(true);
                layout.addView(title_TV);
                JSONArray photos_array = jsonObj.getJSONArray("items");
                for(int i = 0; i < photos_array.length(); i++){
                    String photo_url = photos_array.getJSONObject(i).getString("link");
                    ImageView image = new ImageView(getContext());
                    Picasso.with(rootview.getContext()).load(photo_url).resize(300,300).centerInside().into(image);
                    image.setAdjustViewBounds(true);
                    image.setPadding(0,20,0,20);
                    layout.addView(image);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        public void getMusicArtsJson(View rootview, String response, String response_music){
            try {
                int marginIndex = 50;
                JSONObject jsonObj = new JSONObject(response_music);
                JSONArray arts_obj_temp = jsonObj.getJSONObject("body").getJSONObject("artists").getJSONArray("items");
                if(arts_obj_temp.length()> 0 && arts_obj_temp!= null){

                    rootview.findViewById(R.id.noArtsText).setVisibility(View.INVISIBLE);
                    JSONObject arts_obj = jsonObj.getJSONObject("body").getJSONObject("artists").getJSONArray("items").getJSONObject(0);
                    String art_name = arts_obj.getString("name");
                    LinearLayout layout = (LinearLayout)rootview.findViewById(R.id.artsLayout);
                    TextView title_TV = new TextView(getContext());
                    title_TV.setText(art_name);
                    title_TV.setTextSize(16);
                    title_TV.setGravity(Gravity.CENTER);
                    TextPaint tp = title_TV.getPaint();
                    tp.setFakeBoldText(true);
                    layout.addView(title_TV);

                    if(arts_obj.has("name")){
                        LinearLayout.LayoutParams params_title = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        TextView nameTitle_TV = new TextView(getContext());
                        nameTitle_TV.setText("Name");
                        nameTitle_TV.setTextSize(16);
                        tp = nameTitle_TV.getPaint();
                        tp.setFakeBoldText(true);
                        params_title.setMargins(100, marginIndex, 0, 0);
                        nameTitle_TV.setLayoutParams(params_title);
                        layout.addView(nameTitle_TV);

                        LinearLayout.LayoutParams params_text = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        TextView nameText_TV = new TextView(getContext());
                        nameText_TV.setText(art_name);
                        nameText_TV.setTextSize(16);
                        params_text.setMargins(350, -marginIndex-10, 0, 0);
                        nameText_TV.setLayoutParams(params_text);
                        layout.addView(nameText_TV);
                    }

                    JSONObject follower_obj = arts_obj.getJSONObject("followers");
                    if(follower_obj.has("total")){
                        int follower_int = follower_obj.getInt("total");
                        BigDecimal bd = new BigDecimal(follower_int);
                        String follower_str = parseNumber(",###,###", bd);
                        LinearLayout.LayoutParams params_title = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        TextView folTitle_TV = new TextView(getContext());
                        folTitle_TV.setText("Followers");
                        folTitle_TV.setTextSize(16);
                        tp = folTitle_TV.getPaint();
                        tp.setFakeBoldText(true);
                        params_title.setMargins(100, marginIndex-10, 0, 0);
                        folTitle_TV.setLayoutParams(params_title);
                        layout.addView(folTitle_TV);

                        LinearLayout.LayoutParams params_text = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        TextView folText_TV = new TextView(getContext());
                        folText_TV.setText(follower_str);
                        folText_TV.setTextSize(16);
                        params_text.setMargins(350, -marginIndex, 0, 0);
                        folText_TV.setLayoutParams(params_text);
                        layout.addView(folText_TV);
                    }

                    if(arts_obj.has("popularity")){
                        String pol = arts_obj.getString("popularity");
                        LinearLayout.LayoutParams params_title = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        TextView polTitle_TV = new TextView(getContext());
                        polTitle_TV.setText("Popularity");
                        polTitle_TV.setTextSize(16);
                        tp = polTitle_TV.getPaint();
                        tp.setFakeBoldText(true);
                        params_title.setMargins(100, marginIndex - 20, 0, 0);
                        polTitle_TV.setLayoutParams(params_title);
                        layout.addView(polTitle_TV);

                        LinearLayout.LayoutParams params_text = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        TextView polText_TV = new TextView(getContext());
                        polText_TV.setText(pol);
                        polText_TV.setTextSize(16);
                        params_text.setMargins(350, -marginIndex, 0, 0);
                        polText_TV.setLayoutParams(params_text);
                        layout.addView(polText_TV);
                    }

                    JSONObject spotify_obj = arts_obj.getJSONObject("external_urls");
                    if(spotify_obj.has("spotify")){
                        String spotify_url = spotify_obj.getString("spotify");
                        LinearLayout.LayoutParams params_title = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        TextView spoTitle_TV = new TextView(getContext());
                        spoTitle_TV.setText("Check At");
                        spoTitle_TV.setTextSize(16);
                        tp = spoTitle_TV.getPaint();
                        tp.setFakeBoldText(true);
                        params_title.setMargins(100, marginIndex-30, 0, 0);
                        spoTitle_TV.setLayoutParams(params_title);
                        layout.addView(spoTitle_TV);

                        LinearLayout.LayoutParams params_text = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        TextView spoText_TV = new TextView(getContext());
                        spoText_TV.setText(Html.fromHtml("<a href='" + spotify_url +"'>Spotify</a>"));
                        spoText_TV.setMovementMethod(LinkMovementMethod.getInstance());
                        spoText_TV.setTextSize(16);
                        params_text.setMargins(350, -marginIndex, 0, 0);
                        spoText_TV.setLayoutParams(params_text);
                        layout.addView(spoText_TV);
                    }

                    JSONObject jsonObj_photo = new JSONObject(response);
                    JSONArray photos_array = jsonObj_photo.getJSONArray("items");
                    for(int i = 0; i < photos_array.length(); i++) {
                        String photo_url = photos_array.getJSONObject(i).getString("link");

                        //layout.setPadding(0,20,0,20);
                        ImageView image = new ImageView(getContext());
                        Picasso.with(rootview.getContext()).load(photo_url).resize(300,300).centerInside().into(image);
                        image.setAdjustViewBounds(true);
                        //image.setScaleType(ImageView.ScaleType.FIT_XY);
                        layout.addView(image);
                    }
                }
                else{
                    rootview.findViewById(R.id.noArtsText).setVisibility(View.VISIBLE);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        public static String parseNumber(String pattern, BigDecimal bd) {
            DecimalFormat df = new DecimalFormat(pattern);
            return df.format(bd);
        }

    }

    public static class venueFragment extends Fragment implements OnMapReadyCallback{
        private MapView mMapView;
        private LatLng venueLoc;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_venue, container, false);
            getVenue(rootView);
            venueLoc = new LatLng(venueLocArray[0], venueLocArray[1]);
            mMapView = (MapView) rootView.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();
            mMapView.getMapAsync(this);//when you already implement OnMapReadyCallback in your fragment
            return rootView;
        }
        @Override
        public void onMapReady(GoogleMap googleMap) {
            // Add a marker in destination, and move the camera.
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(venueLoc));
            googleMap.addMarker(new MarkerOptions().position(venueLoc).title(venueName));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));


        }

        public void getVenue(final View rootview){
            final ProgressBar spinner;
            spinner = (ProgressBar)rootview.findViewById(R.id.venue_progressBar);
            spinner.setVisibility(View.VISIBLE);
            TextView tv = (TextView) rootview.findViewById(R.id.venue_progressBarText);
            tv.setText("Searching Details...");

            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(getContext());
            Log.d("detail-getInfo: ", url);

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            getVenueJson(rootview, response);
                            spinner.setVisibility(View.GONE);
                            rootview.findViewById(R.id.venue_progressBarText).setVisibility(View.GONE);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errText = "Getting Results of HTTPRequest is failed!";
                    Toast.makeText(getContext(), errText, Toast.LENGTH_SHORT).show();
                    spinner.setVisibility(View.GONE);
                    rootview.findViewById(R.id.venue_progressBarText).setVisibility(View.GONE);

                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }

        public void getVenueJson(View rootview, String response){
            //ArrayList<Map<String,String>> eventsList = new ArrayList<>();
            try {
                int marginIndex = 50;
                JSONObject jsonObj = new JSONObject(response);
                JSONObject events = jsonObj.getJSONObject("_embedded");
                JSONObject c_event = events.getJSONArray("events").getJSONObject(event_num);

                //Venue venue-name part
                JSONObject venue_info = c_event.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0);
                if(venue_info.has("name")){
                    String venue_name = venue_info.getString("name");
                    TextView tv = (TextView) rootview.findViewById(R.id.venueNameText);
                    tv.setText(venue_name);
                    tv.setVisibility(TextView.VISIBLE);
                    TextView tV = (TextView) rootview.findViewById(R.id.venueNameTitle);
                    tV.setVisibility(TextView.VISIBLE);

                }
                else{
                    TextView tv = (TextView) rootview.findViewById(R.id.venueNameTitle);
                    tv.setVisibility(TextView.GONE);
                    TextView tV = (TextView) rootview.findViewById(R.id.venueNameText);
                    tV.setVisibility(TextView.GONE);
                }


                //Venue venue--address part
                if(venue_info.has("address") && venue_info.getJSONObject("address") != null && venue_info.getJSONObject("address").length() != 0){
                    String venue_add = venue_info.getJSONObject("address").getString("line1");
                    TextView tv = (TextView) rootview.findViewById(R.id.addText);
                    tv.setText(venue_add);
                    tv.setVisibility(TextView.VISIBLE);
                    TextView tV = (TextView) rootview.findViewById(R.id.addTitle);
                    tV.setVisibility(TextView.VISIBLE);
                }
                else{
                    TextView tv = (TextView) rootview.findViewById(R.id.addTitle);
                    tv.setVisibility(TextView.GONE);
                    TextView tV = (TextView) rootview.findViewById(R.id.addText);
                    tV.setVisibility(TextView.GONE);
                }


                //Venue venue--city part
                if(venue_info.has("city") || venue_info.has("state")){
                    String city = "";
                    String state = "";
                    if(venue_info.has("city")){
                        city = venue_info.getJSONObject("city").getString("name");
                    }
                    if(venue_info.has("state")){
                        state = venue_info.getJSONObject("state").getString("name");
                    }
                    String city_str = city + " , " + state;
                    TextView tv = (TextView) rootview.findViewById(R.id.cityText);
                    tv.setText(city_str);
                    tv.setVisibility(TextView.VISIBLE);
                    TextView tV = (TextView) rootview.findViewById(R.id.cityTitle);
                    tV.setVisibility(TextView.VISIBLE);
                }
                else{
                    TextView tv = (TextView) rootview.findViewById(R.id.cityTitle);
                    tv.setVisibility(TextView.GONE);
                    TextView tV = (TextView) rootview.findViewById(R.id.cityText);
                    tV.setVisibility(TextView.GONE);
                }

                //Venue venue--phone_num part
                if(venue_info.has("boxOfficeInfo")){
                    JSONObject boInfo_obj = venue_info.getJSONObject("boxOfficeInfo");
                    if(boInfo_obj.has("phoneNumberDetail")){
                        String venue_phone = boInfo_obj.getString("phoneNumberDetail");
                        TextView tv = (TextView) rootview.findViewById(R.id.phoneText);
                        tv.setText(venue_phone);
                        tv.setVisibility(TextView.VISIBLE);
                        TextView tV = (TextView) rootview.findViewById(R.id.phoneTitle);
                        tV.setVisibility(TextView.VISIBLE);
                    }
                    else{
                        TextView tv = (TextView) rootview.findViewById(R.id.phoneTitle);
                        tv.setVisibility(TextView.GONE);
                        TextView tV = (TextView) rootview.findViewById(R.id.phoneText);
                        tV.setVisibility(TextView.GONE);
                    }
                }
                else{
                    TextView tv = (TextView) rootview.findViewById(R.id.phoneTitle);
                    tv.setVisibility(TextView.GONE);
                    TextView tV = (TextView) rootview.findViewById(R.id.phoneText);
                    tV.setVisibility(TextView.GONE);
                }

                //Venue venue--openHours part
                if(venue_info.has("boxOfficeInfo")) {
                    JSONObject boInfo_obj = venue_info.getJSONObject("boxOfficeInfo");
                    if (boInfo_obj.has("openHoursDetail")) {
                        String venue_hour = boInfo_obj.getString("openHoursDetail");
                        Log.d("d-venue-hour-length: ", "" + venue_hour.length());
                        TextView tv = (TextView) rootview.findViewById(R.id.hourText);
                        tv.setText(venue_hour);
                        tv.setVisibility(TextView.VISIBLE);
                        TextView tV = (TextView) rootview.findViewById(R.id.hourTitle);
                        tV.setVisibility(TextView.VISIBLE);
                    }
                    else{
                        TextView tv = (TextView) rootview.findViewById(R.id.hourTitle);
                        tv.setVisibility(TextView.GONE);
                        TextView tV = (TextView) rootview.findViewById(R.id.hourText);
                        tV.setVisibility(TextView.GONE);
                    }
                }
                else{
                    TextView tv = (TextView) rootview.findViewById(R.id.hourTitle);
                    tv.setVisibility(TextView.GONE);
                    TextView tV = (TextView) rootview.findViewById(R.id.hourText);
                    tV.setVisibility(TextView.GONE);
                }

                //Venue venue--generalRules part
                if(venue_info.has("generalInfo")){
                    JSONObject ruleInfo_obj = venue_info.getJSONObject("generalInfo");
                    if (ruleInfo_obj.has("generalRule")) {
                        String venue_gRule = ruleInfo_obj.getString("generalRule");
                        Log.d("d-venue-gRule-length: ", "" + venue_gRule.length());
                        TextView tv = (TextView) rootview.findViewById(R.id.g_ruleText);
                        tv.setText(venue_gRule);
                        tv.setVisibility(TextView.VISIBLE);
                        TextView tV = (TextView) rootview.findViewById(R.id.g_ruleTitle);
                        tV.setVisibility(TextView.VISIBLE);
                    }
                    else{
                        TextView tv = (TextView) rootview.findViewById(R.id.g_ruleTitle);
                        tv.setVisibility(TextView.GONE);
                        TextView tV = (TextView) rootview.findViewById(R.id.g_ruleText);
                        tV.setVisibility(TextView.GONE);
                    }
                }
                else{
                    TextView tv = (TextView) rootview.findViewById(R.id.g_ruleTitle);
                    tv.setVisibility(TextView.GONE);
                    TextView tV = (TextView) rootview.findViewById(R.id.g_ruleText);
                    tV.setVisibility(TextView.GONE);
                }


                //Venue venue--childRules part
                if(venue_info.has("generalInfo")){
                    JSONObject ruleInfo_obj = venue_info.getJSONObject("generalInfo");
                    if (ruleInfo_obj.has("childRule")) {
                        String venue_cRule = ruleInfo_obj.getString("childRule");
                        Log.d("d-venue-cRule-length: ", "" + venue_cRule.length());
                        TextView tv = (TextView) rootview.findViewById(R.id.c_ruleText);
                        tv.setText(venue_cRule);
                        tv.setVisibility(TextView.VISIBLE);
                        TextView tV = (TextView) rootview.findViewById(R.id.c_ruleTitle);
                        tV.setVisibility(TextView.VISIBLE);
                    }
                    else{
                        TextView tv = (TextView) rootview.findViewById(R.id.c_ruleTitle);
                        tv.setVisibility(TextView.GONE);
                        TextView tV = (TextView) rootview.findViewById(R.id.c_ruleText);
                        tV.setVisibility(TextView.GONE);
                    }
                }
                else{
                    TextView tv = (TextView) rootview.findViewById(R.id.c_ruleTitle);
                    tv.setVisibility(TextView.GONE);
                    TextView tV = (TextView) rootview.findViewById(R.id.c_ruleText);
                    tV.setVisibility(TextView.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static class upsFragment extends Fragment {
        private ArrayList<Map<String,String>> upEventsList = new ArrayList<>();
        private RecyclerView recyclerView;
        private UpsAdapter mAdapter;

        private String category_order = "Default";
        private String aAndD_order = "Ascending";

        private ArrayList<UpEvents> upEventsObjs;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_ups, container, false);
            recyclerView = (RecyclerView) rootView.findViewById(R.id.ups_recycler_view);

            mAdapter = new UpsAdapter(upEventsList);
            recyclerView.setHasFixedSize(true);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);

            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(mAdapter);

            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {

                    Map<String,String> each_item = upEventsList.get(position);
                    String event_uri = each_item.get("upEventUrl");
                    Uri event_uri_url = Uri.parse(event_uri);
                    Intent gotoEvent = new Intent(Intent.ACTION_VIEW, event_uri_url);
                    getContext().startActivity(gotoEvent);
                    Toast.makeText(getContext(), each_item.get("upEventName") + " is selected!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
            getUpEvents(rootView);

            final String[] ups_category_array = getResources().getStringArray(R.array.ups_category_array);
            Spinner ups_category_spinner = rootView.findViewById(R.id.ups_category_spinner);
            ArrayAdapter<String> category_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, ups_category_array);
            ups_category_spinner.setAdapter(category_adapter);
            ups_category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int arg2, long arg3) {
                    int index = parent.getSelectedItemPosition();
                    category_order = ups_category_array[index];
                    Spinner ups_order_spinner = rootView.findViewById(R.id.ups_order_spinner);
                    if(category_order.equals("Default")){
                        ups_order_spinner.setEnabled(false);
                        ups_order_spinner.setClickable(false);
                    }
                    else{
                        ups_order_spinner.setEnabled(true);
                        ups_order_spinner.setClickable(true);
                    }
                    getOrderData(rootView);
                    mAdapter.notifyDataSetChanged();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            final String[] ups_order_array = getResources().getStringArray(R.array.ups_order_array);
            Spinner ups_order_spinner = rootView.findViewById(R.id.ups_order_spinner);
            ArrayAdapter<String> order_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, ups_order_array);
            ups_order_spinner.setAdapter(order_adapter);
            ups_order_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int arg2, long arg3) {
                    int index = parent.getSelectedItemPosition();
                    aAndD_order = ups_order_array[index];
                    getOrderData(rootView);
                    mAdapter.notifyDataSetChanged();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            return rootView;
        }

        public void getUpEvents(final View rootview){
            final ProgressBar spinner;
            spinner = (ProgressBar)rootview.findViewById(R.id.ups_progressBar);
            spinner.setVisibility(View.VISIBLE);
            TextView tv = (TextView) rootview.findViewById(R.id.ups_progressBarText);
            tv.setText("Searching UpEvents...");
            rootview.findViewById(R.id.noUpsText).setVisibility(View.INVISIBLE);
            rootview.findViewById(R.id.ups_recycler_view).setVisibility(View.INVISIBLE);

            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(getContext());
            String venueName_url = null;
            try {
                venueName_url = URLEncoder.encode(venueName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String ups_url = "http://siqi-cs571h8-nodejs.us-east-2.elasticbeanstalk.com/UpcomingEvents?venue_name=" + venueName_url;
            Log.d("detail-getUps: ",ups_url);

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, ups_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject obj = null;
                            int events_num = 0;
                            try {
                                obj = new JSONObject(response);
                                events_num = obj.getJSONObject("resultsPage").getInt("totalEntries");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(events_num == 0){
                                rootview.findViewById(R.id.noUpsText).setVisibility(View.VISIBLE);
                                rootview.findViewById(R.id.ups_category_spinner).setEnabled(false);
                                rootview.findViewById(R.id.ups_category_spinner).setClickable(false);
                                rootview.findViewById(R.id.ups_order_spinner).setEnabled(false);
                                rootview.findViewById(R.id.ups_order_spinner).setClickable(false);
                            }
                            else{
                                rootview.findViewById(R.id.noUpsText).setVisibility(View.INVISIBLE);
                                getUpsJson(rootview, response);
                            }
                            spinner.setVisibility(View.GONE);
                            rootview.findViewById(R.id.ups_progressBarText).setVisibility(View.INVISIBLE);
                            rootview.findViewById(R.id.ups_recycler_view).setVisibility(View.VISIBLE);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errText = "Getting Results of HTTPRequest is failed!";
                    Log.d("detail: ", String.valueOf(error));
                    Toast.makeText(getContext(), errText, Toast.LENGTH_SHORT).show();
                    spinner.setVisibility(View.GONE);
                    rootview.findViewById(R.id.ups_progressBarText).setVisibility(View.INVISIBLE);

                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }

        public void getUpsJson(View rootview, String response){
            ArrayList<Map<String,String>> upEventsList_temp = new ArrayList<>();
            upEventsObjs = new ArrayList<>();
            JSONObject obj = null;
            try {
                obj = new JSONObject(response);
                JSONObject results = obj.getJSONObject("resultsPage").getJSONObject("results");
                JSONArray upEventsTable = results.getJSONArray("event");
                for (int i = 0; i < upEventsTable.length(); i++) {
                    Map<String,String> upEvents_eachrow = new HashMap<>();
                    UpEvents upEvent_obj = new UpEvents();
                    JSONObject upEventsRowInfo = upEventsTable.getJSONObject(i);
                    if(upEventsRowInfo.has("displayName")){
                        String upEventName = upEventsRowInfo.getString("displayName");
                        upEvents_eachrow.put("upEventName", upEventName);
                        upEvent_obj.setUpEventName(upEventName);
                    }
                    if(upEventsRowInfo.has("uri")){
                        String upEventUri = upEventsRowInfo.getString("uri");
                        upEvents_eachrow.put("upEventUrl", upEventUri);
                        upEvent_obj.setUpEventUrl(upEventUri);
                    }
                    if(upEventsRowInfo.has("performance")){
                        if(upEventsRowInfo.getJSONArray("performance").length() !=0 && upEventsRowInfo.getJSONArray("performance") != null){
                            String upEventArts = upEventsRowInfo.getJSONArray("performance").getJSONObject(0).getString("displayName");
                            upEvents_eachrow.put("upEventArts", upEventArts);
                            upEvent_obj.setUpEventArts(upEventArts);

                        }
                        else{
                            break;
                        }

                    }
                    if(upEventsRowInfo.has("start")){
                        JSONObject upEvent_temp = upEventsRowInfo.getJSONObject("start");
                        if(upEvent_temp.has("date")){
                            String upEventDate = upEvent_temp.getString("date");
                            upEvents_eachrow.put("upEventDate", upEventDate);
                            upEvent_obj.setUpEventDate(upEventDate);
                        }
                        if(upEvent_temp.has("time") && upEvent_temp.getString("time") != null && upEvent_temp.getString("time") != "null"){
                            String upEventTime = upEvent_temp.getString("time");
                            upEvents_eachrow.put("upEventTime", upEventTime);
                        }
                    }
                    if(upEventsRowInfo.has("type")){
                        String upEventType = upEventsRowInfo.getString("type");
                        upEvents_eachrow.put("upEventType", upEventType);
                        upEvent_obj.setUpEventType(upEventType);
                    }

                    upEventsObjs.add(upEvent_obj);
                    upEventsList_temp.add(upEvents_eachrow);
                }

                upEventsList.addAll(upEventsList_temp);
                Log.d("upEvents", "upEvent_obj's length: " + upEventsObjs.size());
                mAdapter.notifyDataSetChanged();


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        public void getOrderData(View rootview){
            if(upEventsObjs == null){
                return;
            }
            eventDefaultOrder = new ArrayList<>();
            eventDefaultOrder.addAll(upEventsObjs);

            Collections.sort(upEventsObjs, new NameComparator());
            eventNameOrder = new ArrayList<>();
            eventNameOrder.addAll(upEventsObjs);

            Collections.sort(upEventsObjs, new ArtsComparator());
            eventArtsOrder = new ArrayList<>();
            eventArtsOrder.addAll(upEventsObjs);

            Collections.sort(upEventsObjs, new DateComparator());
            eventTimeOrder = new ArrayList<>();
            eventTimeOrder.addAll(upEventsObjs);

            Collections.sort(upEventsObjs, new TypeComparator());
            eventTypeOrder = new ArrayList<>();
            eventTypeOrder.addAll(upEventsObjs);

            ArrayList<UpEvents> temp = new ArrayList<>();
            Spinner ups_category_spinner = (Spinner) rootview.findViewById(R.id.ups_category_spinner);
            String ups_category = ups_category_spinner.getSelectedItem().toString();
            Spinner ups_order_spinner = (Spinner) rootview.findViewById(R.id.ups_order_spinner);
            String ups_order = ups_order_spinner.getSelectedItem().toString();
            if (ups_order.equals("Ascending")) {
                if (ups_category.equals("Default")) {
                    temp.addAll(eventDefaultOrder);
                } else if (ups_category.equals("Event Name")) {
                    temp.addAll(eventNameOrder);
                } else if (ups_category.equals("Time")) {
                    temp.addAll(eventTimeOrder);
                } else if (ups_category.equals("Artist")) {
                    temp.addAll(eventArtsOrder);
                } else {
                    temp.addAll(eventTypeOrder);
                }
            } else {
                if (ups_category.equals("Default")) {
                    Collections.reverse(eventDefaultOrder);
                    temp.addAll(eventDefaultOrder);
                } else if (ups_category.equals("Event Name")) {
                    Collections.reverse(eventNameOrder);
                    temp.addAll(eventNameOrder);
                } else if (ups_category.equals("Time")) {
                    Collections.reverse(eventTimeOrder);
                    temp.addAll(eventTimeOrder);
                } else if (ups_category.equals("Artist")) {
                    Collections.reverse(eventArtsOrder);
                    temp.addAll(eventArtsOrder);
                } else {
                    Collections.reverse(eventTypeOrder);
                    temp.addAll(eventTypeOrder);
                }
            }
            upEventsList.clear();
            for (int i = 0; i < temp.size(); i++) {
                UpEvents tem = temp.get(i);
                Map<String, String> upEventList_temp = new HashMap<>();
                String upEventName_temp = tem.upEventName;
                upEventList_temp.put("upEventName", upEventName_temp);
                String upEventUrl_temp = tem.upEventUrl;
                upEventList_temp.put("upEventUrl", upEventUrl_temp);
                String upEventArt_temp = tem.upEventArts;
                upEventList_temp.put("upEventArts", upEventArt_temp);
                String upEventDate_temp = tem.upEventDate;
                upEventList_temp.put("upEventDate", upEventDate_temp);
                String upEventType_temp = tem.upEventType;
                upEventList_temp.put("upEventType", upEventType_temp);
                upEventsList.add(upEventList_temp);
            }
        }
    }


    private static class UpEvents{

        public String upEventName, upEventUrl, upEventArts, upEventDate, upEventType;

        public UpEvents(){
        }

        public void setUpEventName(String upEventName) {
            this.upEventName = upEventName;
        }
        public void setUpEventUrl(String upEventUrl) {
            this.upEventUrl = upEventUrl;
        }

        public void setUpEventArts(String upEventArts) {
            this.upEventArts = upEventArts;
        }

        public void setUpEventDate(String upEventDate) {
            this.upEventDate = upEventDate;
        }

        public void setUpEventType(String upEventType) {
            this.upEventType = upEventType;
        }
    }

    private static class NameComparator implements Comparator {
        public int compare(Object object1, Object object2) {
            UpEvents e1 = (UpEvents) object1;
            UpEvents e2 = (UpEvents) object2;
            return e1.upEventName.compareTo(e2.upEventName);
        }
    }

    private static class ArtsComparator implements Comparator {
        public int compare(Object object1, Object object2) {
            UpEvents e1 = (UpEvents) object1;
            UpEvents e2 = (UpEvents) object2;
            return e1.upEventArts.compareTo(e2.upEventArts);
        }
    }

    private static class TypeComparator implements Comparator {
        public int compare(Object object1, Object object2) {
            UpEvents e1 = (UpEvents) object1;
            UpEvents e2 = (UpEvents) object2;
            return e1.upEventType.compareTo(e2.upEventType);
        }
    }

    private static class DateComparator implements Comparator {
        public int compare(Object object1, Object object2) {
            UpEvents e1 = (UpEvents) object1;
            UpEvents e2 = (UpEvents) object2;
            return e1.upEventDate.compareTo(e2.upEventDate);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (infoFrag == null) {
                    infoFrag = new infoFragment();
                }
                return infoFrag;
            } else if (position == 1) {
                if (artsFrag == null) {
                    artsFrag = new artsFragment();
                }
                return artsFrag;
            } else if (position == 2) {
                if (venueFrag == null) {
                    venueFrag = new venueFragment();
                }
                return venueFrag;
            } else {
                if (upsFrag == null) {
                    upsFrag = new upsFragment();
                }
                return upsFrag;
            }
        }
            @Override
            public int getCount() {
                return 4;
            }
        }

}
