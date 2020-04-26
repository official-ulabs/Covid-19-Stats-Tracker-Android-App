package com.ubuntu_labs.covid_19app;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button showStatsByWorld, showStatsByCountry;
    TextView currentConfirmedStats, currentRecoveredStats, currentDeathStats, lastUpdated;
    RecyclerView covidDataRecyclerView;

    String pomberJsonApi = "https://pomber.github.io/covid19/timeseries.json";
    JSONObject countryJSON;

    List<StatsJava> statsJavaList = new ArrayList<>();
    StatsAdapter statsAdapter;

    int worldConfirmed, worldRecovered, worldDeaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showStatsByCountry = findViewById(R.id.showStatsByCountry);
        showStatsByWorld = findViewById(R.id.showWorldStatsButton);

        currentConfirmedStats = findViewById(R.id.confirmedStat);
        currentRecoveredStats = findViewById(R.id.recoveredStat);
        currentDeathStats = findViewById(R.id.deathStats);

        covidDataRecyclerView = findViewById(R.id.statsRecyclerView);
        lastUpdated = findViewById(R.id.lastUpdated);

        statsAdapter = new StatsAdapter(statsJavaList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        covidDataRecyclerView.setLayoutManager(mLayoutManager);
        covidDataRecyclerView.setAdapter(statsAdapter);

        processJSONData();
    }

    private void processJSONData() {

        // TIME to process Response
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest serverRequest = new StringRequest(Request.Method.GET, pomberJsonApi, new Response.Listener<String>() {
            @Override
            public void onResponse(String req) {

                // Plain text Response is received here

                try {

                    countryJSON = new JSONObject(req);

                    // Text converted to JSON
                    // From the api, each item is { "Country": [ an array] ", another item}
                    // We need to extract the country names so that we can select country too

                    for (int i = 0; i < countryJSON.names().length();i++) {
                        String countryName = countryJSON.names().getString(i);

                        // Get each last country Data ( confirmed, recovered and death so that we can calculate for the world stats
                        JSONArray lastItem = countryJSON.getJSONArray(countryName);
                        JSONObject lastCountryDataStat = lastItem.getJSONObject(lastItem.length() - 1); // Last country data

                        StatsJava countryModel = new StatsJava();
                        countryModel.setConfirmed(lastCountryDataStat.getString("confirmed"));
                        countryModel.setCountry(countryName);
                        countryModel.setRecovered(lastCountryDataStat.getString("recovered"));
                        countryModel.setDeaths(lastCountryDataStat.getString("deaths"));
                        statsJavaList.add(countryModel);

                        lastUpdated.setText("Last Updated: " + lastCountryDataStat.getString("date"));

                        worldDeaths = Integer.parseInt(lastCountryDataStat.getString("deaths")) + worldDeaths;
                        worldConfirmed = Integer.parseInt(lastCountryDataStat.getString("confirmed")) + worldConfirmed;
                        worldRecovered = Integer.parseInt(lastCountryDataStat.getString("recovered")) + worldRecovered;

                        // We have data for each country.
                        // We need to calculate for the world too. We use the last item in each country and add it accumulative
                    }

                    Log.i("UbuntuLabs", worldConfirmed + " : " + worldRecovered + " : " + worldDeaths);

                    currentConfirmedStats.setText(String.valueOf(worldConfirmed));
                    currentDeathStats.setText(String.valueOf(worldDeaths));
                    currentRecoveredStats.setText(String.valueOf(worldRecovered));

                    statsAdapter.notifyDataSetChanged();

                    // Once data is loaded
                    Snackbar.make(showStatsByCountry, "Data is loaded daily", BaseTransientBottomBar.LENGTH_LONG).show();
                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Snackbar.make(showStatsByCountry, "Unable to reach Internet", BaseTransientBottomBar.LENGTH_LONG)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                processJSONData();
                            }
                        })
                        .show();
            }
        });

        requestQueue.add(serverRequest);
    }

}
