package com.example.sreeshav.prog02;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainView extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;
    private static Button election_data;
    private static Button select_repr;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeView mShakeDetector;
    public static ArrayList names = new ArrayList();
    public static ArrayList parties = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        setAmbientEnabled();
        election_data = (Button) findViewById(R.id.election_data);

        election_data.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 Intent intent = new Intent("android.intent.action.VoteView");
                                                 startActivity(intent);
                                             }
                                         }
        );
        select_repr = (Button) findViewById(R.id.select_rep);
        select_repr.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                                               startService(sendIntent);
                                           }
                                       }
        );
        Intent in = getIntent();
        Bundle extras = in.getExtras();
        final String val = extras.getString("VAL");
        String[] parts = val.split(",");
        for (int i = 0; i < parts.length; i++) {
            if (i % 2 == 0) {
                names.add(parts[i]);
            } else {
                parties.add(parts[i]);
            }
        }

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.text);
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        pager.setAdapter(new RepresentativeAdapter(this, getFragmentManager()));
        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeView();

    }

    public static class RepresentativeAdapter extends FragmentGridPagerAdapter {
        private final Context mContext;
        private List mRows;

        public RepresentativeAdapter(Context ctx, FragmentManager f) {
            super(f);
            mContext = ctx;
        }

        // A simple container for static data in each page
        private static class Page {
            // static resources
            String titleRes;
            String textRes;
            int iconRes = 1;

        }

        @Override
        public Fragment getFragment(int row, int col) {
            Page page = PAGES[row][col];
            String title = (String) names.get(col);
            String text = (String) parties.get(col);
            CardFragment fragment = CardFragment.create(title, text);
            return fragment;
        }

        private final Page[][] PAGES = new Page[1][parties.size()];

        @Override
        public int getRowCount() {
            return PAGES.length;
        }

        public int getColumnCount(int rowNum) {
            return PAGES[rowNum].length;
        }
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mTextView.setTextColor(getResources().getColor(android.R.color.white));
            mClockView.setVisibility(View.VISIBLE);

            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
            mTextView.setTextColor(getResources().getColor(android.R.color.black));
            mClockView.setVisibility(View.GONE);
        }
    }


}
