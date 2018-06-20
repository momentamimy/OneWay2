package com.example.eltimmy.oneway2;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by eltimmy on 3/16/2018.
 */

public class DataParser {
    public String[] parseDirections(String jsonData,int index) {
        JSONArray jsonArray=null;
        JSONObject jsonObject;

        try {

           /* jsonObject=new JSONObject(jsonData);
            jsonArray=jsonObject.getJSONArray("routes")
                    .getJSONObject(0).getJSONArray("legs")
                    .getJSONObject(0).getJSONArray("steps");

*/
            jsonObject=new JSONObject(jsonData);
            jsonArray=jsonObject.getJSONArray("routes")
                    .getJSONObject(0).getJSONArray("legs")
                    .getJSONObject(index).getJSONArray("steps");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    return getpaths(jsonArray);
    }

    String[] getpaths(JSONArray googleStepsJson)
    {
        int count=googleStepsJson.length();
        String[] polylines=new String[count];
        for (int i=0;i<count;i++)
        {
            try {
                polylines[i]=getpath(googleStepsJson.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    return polylines;
    }



    String getpath(JSONObject googlePathJson)
    {
        String polyline="";
        try {

            polyline=googlePathJson.getJSONObject("polyline").getString("points");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polyline;
    }
    public JSONArray getWaypointOrder(String jsonData){
        JSONArray jsonArray=null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray=jsonObject.getJSONArray("routes")
                    .getJSONObject(0).getJSONArray("waypoint_order");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    //get duration and distance
    public JSONArray getLegsJSONArray(String jsonData) {
        JSONArray jsonArray=null;
        JSONObject jsonObject;
        try {
            jsonObject=new JSONObject(jsonData);
            jsonArray=jsonObject.getJSONArray("routes")
                    .getJSONObject(0).getJSONArray("legs");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }


    public String getDuration(String jsonData,int i)
    {
        String Duration="";
        JSONArray jsonArray=getLegsJSONArray(jsonData);

        try {

            Duration=jsonArray.getJSONObject(i).getJSONObject("duration").getString("value");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Duration ;
    }

    public String getDistance(String jsonData,int i)
    {
        String Distance="";

        JSONArray jsonArray=getLegsJSONArray(jsonData);

        try {

            Distance=jsonArray.getJSONObject(i).getJSONObject("distance").getString("value");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Distance ;
    }

}
