package com.example.sreeshav.prog02;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.AsyncTask;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.*;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.tweetui.*;
import io.fabric.sdk.android.Fabric;

public class CongressionalView extends AppCompatActivity {
    private static Button button_rep;
    String location;
    String latitude;
    String longitude;
    GridView myGrid;
    TextView set_name;
    TextView set_party;
    TextView set_website;
    TextView set_email;
    TextView set_twitter;
    ImageView set_photo;
    String first_name;
    String last_name;
    String title;
    String party;
    String website;
    String email;
    String twitter;
    String bioguide_id;
    ArrayList name_list;
    ArrayList party_list;
    List website_list;
    List email_list;
    List twitter_list;
    List bioguide_list;
    List loc = new ArrayList();
    int numberOfReps;
    Adapter adapter;
    String TWITTER_KEY = "Sreesha";
    String TWITTER_SECRET = "Venkat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_congressional_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        onClickButtonListenerRep();
        Intent in = getIntent();
        Bundle b = in.getExtras();
        location = b.getString("Location");
        latitude = b.getString("Latitude");
        longitude = b.getString("Longitude");
        myGrid = (GridView) findViewById(R.id.gridview);
        adapter = new Adapter(this);
        myGrid.setAdapter(adapter);
        new RetrieveFeedTask().execute();
    }

    public void onClickButtonListenerRep() {
        button_rep = (Button) findViewById(R.id.ib1);
        button_rep.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("com.example.sreeshav.prog02.DetailedView");
                        startActivity(intent);
                    }
                }
        );
}

    public class RetrieveFeedTask extends AsyncTask<Void, Void, String> {
        EditText emailText;
        TextView responseView;
        ProgressBar progressBar;
        static final String API_KEY = "5defbe7ded894800a8af921fdb4651d0";
        static final String API_URL = "congress.api.sunlightfoundation.com/legislators/locate?";
        private Exception exception;

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here

            try {
                URL url;
                if (location != null) {
                    url = new URL("http://" + API_URL + "zip=" + location + "&apikey=" + API_KEY);
                } else {
                    url = new URL("http://" + API_URL + "latitude=" + latitude + "&longitude=" + longitude + "&apikey=" + API_KEY);
                }
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            name_list = new ArrayList();
            party_list = new ArrayList();
            website_list = new ArrayList();
            email_list = new ArrayList();
            twitter_list = new ArrayList();
            bioguide_list = new ArrayList();

            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            Log.i("INFO", response);

            try {
                System.out.println(new JSONTokener(response).nextValue());
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray results = object.getJSONArray("results");
                numberOfReps = results.length();
                for (int i = 0; i < results.length(); i++) {
                    JSONObject reps = results.getJSONObject(i);
                    first_name = reps.getString("first_name");
                    last_name = reps.getString("last_name");
                    title = reps.getString("title");
                    party = reps.getString("party");
                    website = reps.getString("website");
                    email = reps.getString("oc_email");
                    twitter = reps.getString("twitter_id");
                    bioguide_id = reps.getString("bioguide_id");
                    if (title.equals("Sen")) {
                        title = "Senator";
                    } else {
                        title = "Representative";
                    }
                    if (party.equals("D")) {
                        party = "Party: Democrat";
                    } else {
                        party = "Party: Republican";
                    }
                    String fullName = title + " " + first_name + " " + last_name;
                    name_list.add(fullName);
                    party_list.add(party);
                    website_list.add(website);
                    email_list.add(email);
                    twitter_list.add(twitter);
                    bioguide_list.add(bioguide_id);
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public class Adapter extends BaseAdapter {
        Context context;
        Adapter (Context c) {
            this.context=c;
        }

        @Override
        public int getCount() {
            return numberOfReps;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            StringBuilder concat = new StringBuilder();
            StringBuilder concat2 = new StringBuilder();
            for (int i = 0; i < name_list.size(); i++) {
                concat.append((String) name_list.get(i) + ",");
                concat.append((String) party_list.get(i) + ",");
            }
            Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
            sendIntent.putExtra("Clicked", "Yes");
            sendIntent.putExtra("nameAndParty", concat.toString());
            startService(sendIntent);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.content_congressional_view, null);
            }
            set_name = (TextView) convertView.findViewById(R.id.rep1);
            set_party = (TextView) convertView.findViewById(R.id.rep1Party);
            set_website = (TextView) convertView.findViewById(R.id.rep1Website);
            set_website.setClickable(true);
            set_website.setMovementMethod(LinkMovementMethod.getInstance());
            set_email = (TextView) convertView.findViewById(R.id.rep1Email);
            set_email.setClickable(true);
            set_email.setMovementMethod(LinkMovementMethod.getInstance());
            set_twitter = (TextView) convertView.findViewById(R.id.rep1Twitter);
            set_photo = (ImageView) convertView.findViewById(R.id.photo);
            Picasso.with(context).load("https://theunitedstates.io/images/congress/225x275/" + bioguide_list.get(position) + ".jpg").into(set_photo);
            if (name_list.size() == 0) {
                set_name.setText(" ");
            } else {
                set_name.setText((String) name_list.get(position));
            }
            if (party_list.size() == 0) {
                set_party.setText(" ");
            } else {
                set_party.setText((String) party_list.get(position));
            }
            if (website_list.size() == 0) {
                set_website.setText(" ");
            } else {
                String link = "<a href='" + website_list.get(position) + "'> Website</a>";
                String text = link;
                set_website.setText(Html.fromHtml(text));
            }
            if (email_list.size() == 0) {
                set_email.setText(" ");
            } else {
                String email_link = "<a href='" + email_list.get(position) + "'> Email</a>";
                String email_text = email_link;
                set_email.setText(Html.fromHtml(email_text));
            }
            set_twitter.setText("Latest Tweet" + position);

            TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
            @Override
            public void success(Result<AppSession> appSessionResult) {
                AppSession session = appSessionResult.data;
                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                twitterApiClient.getStatusesService().userTimeline(null, (String) twitter_list.get(position), 1, null, null, false, false, false, true, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> listResult) {
                        for(Tweet tweet: listResult.data) {
                            set_twitter.setText("Latest Tweet: " + tweet.text);
                        }
                    }
                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                    }
                });
            }
            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
            }
        });
//
//                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
//                SearchService service = twitterApiClient.getSearchService();
//
//                service.tweets("@" + (String) twitter_list.get(position), null, null, null, "recent", 1, null, null,
//                        null, true, new Callback<Search>() {
//                            @Override
//                            public void success(Result<Search> searchResult) {
//                                TwitterSession session = Twitter.getSessionManager().getActiveSession();
//
//                                List<Tweet> tweets = searchResult.data.tweets;
//
//                                for (Tweet tweet : tweets) {
//                                    set_twitter.setText("Latest Tweet: " + tweet.text);
//                                    break;
//                                }
//                            }
//                            @Override
//                            public void failure(TwitterException error) {
//
//                            }
//                        }
//                );
            return convertView;
        }
    }

}
