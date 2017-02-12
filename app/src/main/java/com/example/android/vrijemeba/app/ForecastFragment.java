/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.vrijemeba.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.vrijemeba.app.data.WeatherContract;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int FORECAST_LOADER = 0;

    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
        private static final String[] FORECAST_COLUMNS = {
                        // In this case the id needs to be fully qualified with a table name, since
                        // the content provider joins the location & weather tables in the background
                        // (both have an _id column)
                        // On the one hand, that's annoying.  On the other, you can search the weather table
                        // using the location set by the user, which is only in the Location table.
                        // So the convenience is worth it.
                        WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                        WeatherContract.WeatherEntry.COLUMN_DATE,
                        WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                        WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                        WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                        WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
                        WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
                        WeatherContract.LocationEntry.COLUMN_COORD_LAT,
                        WeatherContract.LocationEntry.COLUMN_COORD_LONG
                        };

                // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
                // must change.
                static final int COL_WEATHER_ID = 0;
        static final int COL_WEATHER_DATE = 1;
        static final int COL_WEATHER_DESC = 2;
        static final int COL_WEATHER_MAX_TEMP = 3;
        static final int COL_WEATHER_MIN_TEMP = 4;
        static final int COL_LOCATION_SETTING = 5;
        static final int COL_WEATHER_CONDITION_ID = 6;
        static final int COL_COORD_LAT = 7;
        static final int COL_COORD_LONG = 8;

    private ForecastAdapter mForecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(com.example.android.vrijemeba.app.R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == com.example.android.vrijemeba.app.R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create some dummy data for the ListView.  Here's a sample weekly forecast
//        String[] data = {
//                "Mon 6/23 - Sunny - 31/17",
//                "Tue 6/24 - Foggy - 21/8",
//                "Wed 6/25 - Cloudy - 22/17",
//                "Thurs 6/26 - Rainy - 18/11",
//                "Fri 6/27 - Foggy - 21/10",
//                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
//                "Sun 6/29 - Sunny - 20/7"
//        };
//        List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));

        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
//        String locationSetting = Utility.getPreferredLocation(getActivity());

//        mForecastAdapter =
//                new ArrayAdapter<String>(
//                        getActivity(), // The current context (this activity)
//                        com.example.android.vrijemeba.app.R.layout.list_item_forecast, // The name of the layout ID.
//                        com.example.android.vrijemeba.app.R.id.list_item_forecast_textview, // The ID of the textview to populate.
//                        new ArrayList<String>());

        // Sort order:  Ascending, by date.
//                String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
//                Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
//                        locationSetting, System.currentTimeMillis());
//
//                        Cursor cur = getActivity().getContentResolver().query(weatherForLocationUri,
//                                null, null, null, sortOrder);

                // The CursorAdapter will take data from our cursor and populate the ListView
                                // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
                                        // up with an empty list the first time we run.
//                                                mForecastAdapter = new ForecastAdapter(getActivity(), cur, 0);
        // The CursorAdapter will take data from our cursor and populate the ListView.
                mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(com.example.android.vrijemeba.app.R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(com.example.android.vrijemeba.app.R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                String forecast = mForecastAdapter.getItem(position);
//                Intent intent = new Intent(getActivity(), DetailActivity.class)
//                        .putExtra(Intent.EXTRA_TEXT, forecast);
//                startActivity(intent);
//            }
//        });
        // We'll call our MainActivity
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                                        // if it cannot seek to that position.
                                                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                                if (cursor != null) {
                                        String locationSetting = Utility.getPreferredLocation(getActivity());
                                        Intent intent = new Intent(getActivity(), DetailActivity.class)
                                                        .setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                                                locationSetting, cursor.getLong(COL_WEATHER_DATE)
                                                                ));
                                        startActivity(intent);
                                    }
                            }
                    });

        return rootView;
    }

    @Override
        public void onActivityCreated(Bundle savedInstanceState) {
                getLoaderManager().initLoader(FORECAST_LOADER, null, this);
                super.onActivityCreated(savedInstanceState);
            }

    // since we read the location when we create the loader, all we need to do is restart things
        void onLocationChanged( ) {
                updateWeather();
                getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
            }


    private void updateWeather() {
//        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity(), mForecastAdapter);
//        //weatherTask.execute("71210");
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        String location = prefs.getString(getString(R.string.pref_location_key),
//                getString(R.string.pref_location_default));
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
                String location = Utility.getPreferredLocation(getActivity());
        weatherTask.execute(location);
    }

    @Override

        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                String locationSetting = Utility.getPreferredLocation(getActivity());

                        // Sort order:  Ascending, by date.
                                String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
                Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                                locationSetting, System.currentTimeMillis());

                return new CursorLoader(getActivity(),
                        weatherForLocationUri,
                        FORECAST_COLUMNS,
                                null,
                                null,
                                sortOrder);
            }

                @Override
        public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
                mForecastAdapter.swapCursor(cursor);
            }

                @Override
        public void onLoaderReset(Loader<Cursor> cursorLoader) {
                mForecastAdapter.swapCursor(null);
            }

}
