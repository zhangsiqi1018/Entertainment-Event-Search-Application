package com.example.files.eventsearch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static String url = "";



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
    private resultFragment resultFrag;
    private static favorFragment favorFrag;

    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    public static float cur_lat;
    public static float cur_lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tabs);


        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == 0){

            getLngAndLat(this);
        }

        else{

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                } else {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    //Log.d("locationq","just grant");
                    getLngAndLat(this);

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, "Failed to get current location permission!", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void getLngAndLat(Context context) {


        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {

                cur_lat = (float) location.getLatitude();
                cur_lng = (float) location.getLongitude();
                Toast.makeText(MainActivity.this, "Fetch location successfully.", Toast.LENGTH_SHORT).show();
                Log.d("cur_location", "g:"+ cur_lat + "," + cur_lng);
            } else {
                getLngAndLatWithNetwork();
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                cur_lat = (float) location.getLatitude();
                cur_lng = (float) location.getLongitude();
                Toast.makeText(MainActivity.this, "Fetch location successfully.", Toast.LENGTH_SHORT).show();
                Log.d("cur_location", "n1:"+cur_lat + "," + cur_lng);
            }

        }

    }


    public void getLngAndLatWithNetwork() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            cur_lat = (float) location.getLatitude();
            cur_lng = (float) location.getLongitude();
            Toast.makeText(MainActivity.this, "Fetch location successfully.", Toast.LENGTH_SHORT).show();
            Log.d("cur_location", "n2:"+cur_lat + "," + cur_lng);

        }
    }

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
        @Override
        public void onProviderEnabled(String provider) {

        }
        @Override
        public void onProviderDisabled(String provider) {

        }
        @Override
        public void onLocationChanged(Location location) {

        }
    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class resultFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String RESULT_URL = "Result_url";

        private boolean error_keyword;
        private boolean error_location;

        private static final int TRIGGER_AUTO_COMPLETE = 100;
        private static final long AUTO_COMPLETE_DELAY = 300;
        private Handler handler;
        private AutoSuggestAdapter autoSuggestAdapter;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);


            //AUTO part
            final AppCompatAutoCompleteTextView autoCompleteTextView = rootView.
                    findViewById(R.id.keywordText);
            //final TextView selectedText = rootView.findViewById(R.id.selected_item);

            //Setting up the adapter for AutoSuggest
            autoSuggestAdapter = new AutoSuggestAdapter(getContext(),
                    android.R.layout.simple_dropdown_item_1line);
            autoCompleteTextView.setThreshold(2);
            autoCompleteTextView.setAdapter(autoSuggestAdapter);
            autoCompleteTextView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            //selectedText.setText(autoSuggestAdapter.getObject(position));
                        }
                    });

            autoCompleteTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int
                        count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                    handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                            AUTO_COMPLETE_DELAY);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            handler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.what == TRIGGER_AUTO_COMPLETE) {
                        if (!TextUtils.isEmpty(autoCompleteTextView.getText())) {
                            String keyword = autoCompleteTextView.getText().toString();
                            String keyword_url = null;
                            try {
                                keyword_url = URLEncoder.encode(keyword, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            makeApiCall(keyword_url);
                        }
                    }
                    return false;
                }
            });


            //Check and set the form functions
            checkAndSetForm(rootView);
            return rootView;
        }
        private void makeApiCall(String text) {
            ApiCall.make(getContext(), text, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //parsing logic, please change it as per your requirement
                    List<String> stringList = new ArrayList<>();
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        JSONArray array = responseObject.getJSONObject("_embedded").getJSONArray("attractions");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject row = array.getJSONObject(i);
                            stringList.add(row.getString("name"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //IMPORTANT: set data here and notify
                    autoSuggestAdapter.setData(stringList);
                    autoSuggestAdapter.notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }

        public void checkAndSetForm(final View rootView){
            RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup);
            rootView.findViewById(R.id.locationText).setEnabled(false);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.currentRadio) {
                        EditText inputLocationText = rootView.findViewById(R.id.locationText);
                        inputLocationText.setEnabled(false);
                        inputLocationText.setError(null);
                    } else {
                        rootView.findViewById(R.id.locationText).setEnabled(true);
                    }
                }
            });


            Button searchBtn = (Button)rootView.findViewById(R.id.searchBtn);
            searchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorCheck(rootView);
                    if ((!error_keyword) && (!error_location)) {
                        try {
                            requestHTTP(rootView);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        String errorcheck = "Please fix all fields with errors";
                        Toast.makeText(getContext(), errorcheck, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Button clearBtn = (Button)rootView.findViewById(R.id.clearBtn);
            clearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatAutoCompleteTextView autoCompleteTextView = rootView.
                            findViewById(R.id.keywordText);
                    autoCompleteTextView.setText("");
                    Spinner category_spinner = (Spinner) rootView.findViewById(R.id.category_spinner);
                    category_spinner.setSelection(0);
                    EditText distanceText = (EditText) rootView.findViewById(R.id.distanceText);
                    distanceText.setText("");
                    RadioButton currentBtn  = (RadioButton)rootView.findViewById(R.id.currentRadio);
                    currentBtn.setChecked(true);
                    RadioButton otherBtn  = (RadioButton)rootView.findViewById(R.id.otherlocRadio);
                    otherBtn.setChecked(false);
                    EditText locationText = (EditText) rootView.findViewById(R.id.locationText);
                    locationText.setText("");
                    locationText.setEnabled(false);
                    TextView errorKeyword = (TextView) rootView.findViewById(R.id.errorKeyword);
                    errorKeyword.setVisibility(View.GONE);
                    TextView errorLoc = (TextView) rootView.findViewById(R.id.errorlocation);
                    errorLoc.setVisibility(View.GONE);
                }
            });
        }

        public void errorCheck(final View rootView){
            AppCompatAutoCompleteTextView autoCompleteTextView = rootView.
                    findViewById(R.id.keywordText);
            String keyword = autoCompleteTextView.getText().toString().trim();
            if(keyword == null || keyword.length() == 0 || keyword.equals("")){
                TextView errorKeyword = (TextView) rootView.findViewById(R.id.errorKeyword);
                errorKeyword.setText("Please enter mandatory field");
                errorKeyword.setVisibility(View.VISIBLE);
                //autoCompleteTextView.setError("Please enter mandatory field");
                error_keyword = true;
            }
            else{
                TextView errorKeyword = (TextView) rootView.findViewById(R.id.errorKeyword);
                errorKeyword.setVisibility(View.GONE);
                error_keyword = false;
            }

            EditText locationText = (EditText) rootView.findViewById(R.id.locationText);
            String location = locationText.getText().toString().trim();
            if(locationText.isEnabled()){
                if(location == null || location.length() == 0 || location.equals("")){
                    TextView errorLoc = (TextView) rootView.findViewById(R.id.errorlocation);
                    errorLoc.setText("Please enter mandatory field");
                    errorLoc.setVisibility(View.VISIBLE);
                    //locationText.setError("Please enter mandatory field");
                    error_location = true;
                }
                else{
                    TextView errorLoc = (TextView) rootView.findViewById(R.id.errorlocation);
                    errorLoc.setVisibility(View.GONE);
                    error_location = false;
                }
            }
            else{
                TextView errorLoc = (TextView) rootView.findViewById(R.id.errorlocation);
                errorLoc.setVisibility(View.GONE);
                error_location = false;
            }

        }

        public void requestHTTP(final View rootView) throws UnsupportedEncodingException {

            EditText keywordText = (EditText) rootView.findViewById(R.id.keywordText);
            Spinner category_spinner = (Spinner) rootView.findViewById(R.id.category_spinner);
            EditText distanceText = (EditText) rootView.findViewById(R.id.distanceText);
            Spinner unit_spinner = (Spinner) rootView.findViewById(R.id.unit_spinner);
            RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup);
            EditText locationText = (EditText) rootView.findViewById(R.id.locationText);
            String keyword = keywordText.getText().toString();
            String keyword_url = URLEncoder.encode(keyword, "UTF-8");

            String category = "";
            if(category_spinner.getSelectedItem().toString().trim().equals("Arts & Theatre")){
                category = "artsthreatre";
            }
            else{
                category = category_spinner.getSelectedItem().toString().trim().toLowerCase();
            }

            String distance = distanceText.getText().toString();
            String distance_url = URLEncoder.encode(distance, "UTF-8");

            String unit = unit_spinner.getSelectedItem().toString().toLowerCase();
            if(unit.equals("kilometers")){
                unit = "km";
            }

            int selectedRadioButtonID = radioGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = (RadioButton) rootView.findViewById(selectedRadioButtonID);
            String loc_choice = selectedRadioButton.getText().toString();
            String loc_choice_url = "";
            if(loc_choice.equals( "Current Location")){
                loc_choice_url = "here";
            }
            else{
                loc_choice_url = "other";
            }

            String location = locationText.getText().toString();
            String location_url = URLEncoder.encode(location, "UTF-8");

            //start the ResultActivity
            Intent intent = new Intent(getContext(), ResultActivity.class);
            url = "http://siqi-cs571h8-nodejs.us-east-2.elasticbeanstalk.com/formData?keyword=" + keyword_url + "&category=" + category + "&distance=" + distance_url + "&curLat=" + cur_lat + "&curLon=" + cur_lng + "&location=" + location_url + "&choice=" + loc_choice_url + "&d_choice=" + unit;
            intent.putExtra(RESULT_URL, url);
            startActivity(intent);
        }
    }


    public static class favorFragment extends Fragment {

        private ArrayList<Map<String,String>> favorEventsList = new ArrayList<>();
        private RecyclerView recyclerView;
        private EventsAdapter mAdapter;
        private View rootView;
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


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.d("favor", "call the onCreate function" );
            rootView = inflater.inflate(R.layout.fragment_favor, container, false);
            recyclerView = (RecyclerView) rootView.findViewById(R.id.favor_recycler_view);

            mAdapter = new EventsAdapter(favorEventsList, getContext(),favorFrag);
            recyclerView.setHasFixedSize(true);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);

            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(MyItemClickListener);
            getFavorEvents(rootView);
            return rootView;
        }
        private EventsAdapter.OnItemClickListener MyItemClickListener = new EventsAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View v, EventsAdapter.ViewName viewName, int position) {
                //viewName可区分item及item内部控件
                Map<String,String> each_item = favorEventsList.get(position);
                switch (v.getId()){
                    default:
                        getDetail(each_item, position);
                        Toast.makeText(getContext(), each_item.get("eventName") + " is selected!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onItemLongClick(View v) {

            }
        };

        public void onResume(){
            super.onResume();
            //getFavorDetail(rootView);
            SharedPreferences sharedPref = getContext().getSharedPreferences("FavorFiles", Context.MODE_PRIVATE);
            //sharedPref.edit().clear().commit();
            Map<String, ?> favorEvents = sharedPref.getAll();
            Log.d("favor","Call onResume and get favor list's size:  "+ favorEvents.size());
            if(favorEvents.size() == 0 || favorEvents == null){
                rootView.findViewById(R.id.noFavorText).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.favor_recycler_view).setVisibility(View.INVISIBLE);
                return;
            }
            else{
                rootView.findViewById(R.id.noFavorText).setVisibility(View.INVISIBLE);
                rootView.findViewById(R.id.favor_recycler_view).setVisibility(View.VISIBLE);
                getFavorEvents(rootView);
            }

        }

        public void getFavorEvents( View rootview){
            SharedPreferences sharedPref = getContext().getSharedPreferences("FavorFiles", Context.MODE_PRIVATE);
            //sharedPref.edit().clear().commit();
            Map<String, ?> favorEvents = sharedPref.getAll();
            favorEventsList.clear();
            for(Map.Entry<String,?> entry : favorEvents.entrySet()){
                String favorEvent = (String) entry.getValue();
                Map<String,String> favorEventMap = new HashMap<>();
                try {
                    JSONObject favorEventRow = new JSONObject(favorEvent);
                    String favor_icon = favorEventRow.getString("favor_icon");
                    favorEventMap.put("category_event", favor_icon);

                    String favor_eventName = favorEventRow.getString("favor_eventName");
                    favorEventMap.put("eventName", favor_eventName);

                    String favor_eventVenue = favorEventRow.getString("favor_venueName");
                    favorEventMap.put("venueName", favor_eventVenue);

                    String favor_eventDate = favorEventRow.getString("favor_eventDate");
                    favorEventMap.put("date", favor_eventDate);

                    String favor_eventTime = favorEventRow.getString("favor_eventTime");
                    favorEventMap.put("time", favor_eventTime);

                    String favor_eventId = favorEventRow.getString("favor_eventId");
                    favorEventMap.put("eventId", favor_eventId);

                    String favor_ticketApi = favorEventRow.getString("favor_ticketApi");
                    favorEventMap.put("ticketApi", favor_ticketApi);

                    String favor_artsArra = favorEventRow.getString("favor_artsArray");
                    favorEventMap.put("arts_array", favor_artsArra);

                    String favor_venueLoc = favorEventRow.getString("favor_venueLoc");
                    favorEventMap.put("venue_loc", favor_venueLoc);

                    String favor_eventNum = favorEventRow.getString("favor_eventNum");
                    favorEventMap.put("eventNum", favor_eventNum);

                    String favor_eventUrl= favorEventRow.getString("favor_eventUrl");
                    favorEventMap.put("eventUrl", favor_eventUrl);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                favorEventsList.add(favorEventMap);
            }
            Log.d("favor", "Call getFavorDetail and get favorEventsList's size: " + favorEventsList.size());
            mAdapter.notifyDataSetChanged();
        }


        public void getDetail(Map<String,String> each_item, int pos){
            Log.d("favor", "each_favorite_event: "+ each_item.toString());
            Log.d("favor", "each_favorite_event_pos: "+pos);

            //start the ResultActivity
            Intent intent = new Intent(getContext(), DetailActivity.class);
            Bundle bd = new Bundle();
            bd.putString(RESULT_URL, each_item.get("ticketApi"));
            bd.putInt(EVENT_NUM, Integer.parseInt(each_item.get("eventNum")));

            if(each_item.containsKey("arts_array")){
                String artArray_str = each_item.get("arts_array");
                String temp = artArray_str.substring(1, artArray_str.length()-1);
                String[] artArray = temp.split(",");
                bd.putStringArray(ARTS_ARRAY, artArray);
                Log.d("favor-detail", "arts: "+Arrays.toString(artArray));
            }
            if(each_item.containsKey("venue_loc")){
                String venueLoc_str = each_item.get("venue_loc");
                String temp = venueLoc_str.substring(1, venueLoc_str.length()-1);
                String[] venueLoc_temp = temp.split(",");
                double[] venueLoc_array = new double[2];
                venueLoc_array[0] = Double.valueOf(venueLoc_temp[0]);
                venueLoc_array[1] = Double.valueOf(venueLoc_temp[1]);
                bd.putDoubleArray(VENUE_LOC,venueLoc_array);
                Log.d("favor-detail", "venue loc: "+Arrays.toString(venueLoc_array));
            }
            if(each_item.containsKey("venueName")){
                String venue_name = each_item.get("venueName");
                bd.putString(VENUE_NAME, venue_name);
                Log.d("favor-detail", "venue: "+venue_name);
            }
            if(each_item.containsKey("eventName")){
                String event_name = each_item.get("eventName");
                bd.putString(EVENT_NAME, event_name);
                Log.d("favor-detail", "event: "+event_name);
            }
            if(each_item.containsKey("date")){
                String event_date = each_item.get("date");
                bd.putString(EVENT_DATE, event_date);
                Log.d("favor-detail", "event_date: "+event_date);
            }
            if(each_item.containsKey("time")){
                String event_time = each_item.get("time");
                bd.putString(EVENT_TIME, event_time);
                Log.d("favor-detail", "event_time: "+event_time);
            }
            if(each_item.containsKey("eventId")){
                String event_id = each_item.get("eventId");
                bd.putString(EVENT_ID, event_id);
                Log.d("favor-detail", "event_id: "+event_id);
            }
            if(each_item.containsKey("category_event")){
                String event_c = each_item.get("category_event");
                bd.putString(EVENT_CATEGORY, event_c);
                Log.d("favor-detail", "event_category: "+event_c);
            }

            if(each_item.containsKey("eventUrl")){
                String event_url = each_item.get("eventUrl");
                bd.putString(EVENT_URL, event_url);
                Log.d("favor-detail", "event_url: "+event_url);
            }
            intent.putExtras(bd);
            startActivity(intent);
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
                if (resultFrag == null) {
                    resultFrag = new resultFragment();
                }
                return resultFrag;
            }
            else {
                if (favorFrag == null) {
                    favorFrag = new favorFragment();
                }
                return favorFrag;
            }

        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }
}
