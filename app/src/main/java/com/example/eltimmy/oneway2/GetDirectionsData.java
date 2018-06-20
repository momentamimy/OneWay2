package com.example.eltimmy.oneway2;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by eltimmy on 3/16/2018.
 */



public class GetDirectionsData extends AsyncTask<Object,String,String> {

    GoogleMap mMap;
    String url;
    String getDirectionsData;
    int waypoints;
    ArrayList<Polyline> polylines;

    String distance, duration;

    TextView dis,dur;

    boolean navigation;
    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        dis = (TextView) objects[2];
        dur=(TextView)  objects[3];
        waypoints=(int)objects[4];
        polylines=(ArrayList<Polyline>) objects[5];
        navigation=(boolean)objects[6];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            getDirectionsData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getDirectionsData;
    }

    @Override
    protected void onPostExecute(String s) {
        DataParser dataParser2 = new DataParser();
        double distance,duration;
        distance=Integer.parseInt(dataParser2.getDistance(s,0));
        duration=Integer.parseInt(dataParser2.getDuration(s,0));
        if (distance<1000)
        {
            DecimalFormat DForm = new DecimalFormat("#");
            dis.setText(Double.valueOf(DForm.format(distance))+" m");
        }
        else
        {
            DecimalFormat threeDForm = new DecimalFormat("#.###");
            dis.setText(Double.valueOf(threeDForm.format(distance/1000))+" Km");
        }
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        dur.setText(Double.valueOf(twoDForm.format(duration/60))+" Mins");
        removePolylines();
        for(int i=0;i<waypoints;i++)
        {
            DataParser dataParser = new DataParser();
            String[] directionsList = dataParser.parseDirections(s,i);
            displayDirectionList(directionsList);
        }
    }

    private void displayDirectionList(String[] directionsList) {

        int count = directionsList.length;
        for (int i = 0; i < count; i++) {
            PolylineOptions options = new PolylineOptions();
            if (navigation==true)
            {
                options.color(Color.argb(255,62,113,175
                ));
                options.width(60);
            }
            else
            {
                options.color(Color.BLUE);
                options.width(10);
            }
            options.addAll(PolyUtil.decode(directionsList[i]));

            polylines.add(mMap.addPolyline(options));
        }
    }
    public void removePolylines()
    {
        if (!polylines.isEmpty())
        {
            for (int i=0;i<polylines.size();i++)
            {
                polylines.get(i).remove();
            }

        }
    }
}