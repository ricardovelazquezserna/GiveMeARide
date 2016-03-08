package com.example.ricardo.expoingenieria.DBElements;

/**
 * Created by Valeria on 3/7/16.
 */

import android.os.AsyncTask;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class DBRoute {

    private String url="https://afternoon-spire-5809.herokuapp.com/api/pckmupRoutes";

    private double[] Lat=new double[50];
    private double[] Lng=new double[50];
    private String routeId="", rideId="";

    private boolean post=true; //get =false

    private static JSONObject routedb;

    public DBRoute (){
    }

    public double[] getLat() {
        return Lat;
    }

    public double[] getLng() {
        return Lng;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getRideId() {
        return rideId;
    }

    public void create(double[] lat, double[] lng,String rideid){

        post=true;

        routedb = new JSONObject();
        JSONArray array=new JSONArray();

        try {

            for(int i=0;i<lat.length;i++){

                JSONObject latlng = new JSONObject();

                latlng.put("lat",lat[i]);
                latlng.put("lng",lng[i]);

                array.put(latlng);
            }

            routedb.put("LatLng",array);  this.Lat=lat; this.Lng=lng;
            routedb.put("pckmupTripId",rideid);  this.rideId=rideid;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*{
            "LatLng": [
               {"lat":1,"lng":2}
              ],
            "pckmupTripId": 0
         }*/

        new HttpAsyncTask().execute(url);
    }

    public void getByRideId(String rideid){

        post=false;

        String where="";
        Lat=new double[50];
        Lng=new double[50];

        try {
            routedb = new JSONObject();
            routedb.put("pckmupTripId",rideid);

            JSONObject w = new JSONObject();

            w.put("where",routedb);
            where=w.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String geturl = url+"/findOne?filter="+where;
        new HttpAsyncTask().execute(geturl);
    }



    private static String POST(String url){

        String result = "";

        try {
            //
            URLConnection connection = new URL(url).openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true); // post
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");

            OutputStream wr = connection.getOutputStream();
            wr.write(routedb.toString().getBytes("UTF-8"));
            wr.flush();
            wr.close();

            InputStream response = connection.getInputStream();

            // convert inputstream to string
            if(response != null)
                result = convertInputStreamToString(response);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", "El error"+e.toString());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }


    private static String GET(String url){

        String result = "";

        try {
            //
            URLConnection connection = new URL(url).openConnection();
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(25000);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");

            InputStream response = connection.getInputStream();

            // convert inputstream to string
            if(response != null)
                result = convertInputStreamToString(response);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", "El error"+e.toString());
        }

        return result;
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            if(post)
                return POST(urls[0]);
            else
                return GET(urls[0]);

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject json = new JSONObject(result);

                if(post) {
                    routeId = json.getString("id");
                } else {
                    Log.d("get","el get "+json.toString());

                    JSONArray latlng = json.getJSONArray("LatLng");

                    for (int i = 0; i < latlng.length(); ++i) {
                        JSONObject rut = latlng.getJSONObject(i);
                        Lat[i] = rut.getDouble("lat");
                        Lng[i] = rut.getDouble("lng");
                    }

                    rideId=json.getString("pckmupTripId"); //trip=ride
                    routeId=json.getString("id");

                }

            } catch (JSONException jex) {
                Log.d("Json exception", jex.getLocalizedMessage());
            } catch (Exception ex){
                ex.printStackTrace();
            }


        }
    }
}
