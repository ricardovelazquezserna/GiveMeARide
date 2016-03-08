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

public class DBSchedule {

    private String url="https://afternoon-spire-5809.herokuapp.com/api/pckmupSchedules";

    private String departureTime="",arrivalTime="", rideId="", scheduleId="";
    //lun,mar,mier,juev,vier,sab,dom
    private boolean[] Days={true,true,true,true,true,true,false};

    private boolean post=true; //get =false

    private static JSONObject scheduledb;

    public DBSchedule (){
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getRideId() {
        return rideId;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public boolean[] getDays() {
        return Days;
    }

//getById(){}

    public void create(String dtime,String atime,String rideid, boolean[] days){

        post=true;

        scheduledb = new JSONObject();
        try {
            scheduledb.put("Departure_Time",dtime);  this.departureTime=dtime;
            scheduledb.put("Arrival_Time",atime);    this.arrivalTime=atime;

            String ds=daysToString(days);
            scheduledb.put("Days",ds);             this.Days=days;
            scheduledb.put("pckmupTripId",rideid);    this.rideId=rideid;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*{
            "Departure_Time": "1pm",
            "Arrival_Time": "2pm",
            "Days": "0,2,end",  //lunes y miercoles
            "pckmupTripId": 0  //trip=ride
          }*/

        new HttpAsyncTask().execute(url);
    }

    public void getByRideId(String rideid){

        post=false;

        String where="";

        try {
            scheduledb = new JSONObject();
            scheduledb.put("pckmupTripId",rideid);

            JSONObject w = new JSONObject();

            w.put("where",scheduledb);
            where=w.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String geturl = url+"/findOne?filter="+where;
        new HttpAsyncTask().execute(geturl);
    }

    private static String daysToString(boolean[] days){

        StringBuilder sb = new StringBuilder();

        //0 lunes, 1 martes, 2 mier...6 domingo
        for (int i=0;i<days.length;i++) {
            if(days[i]){  //if true
                sb.append(i+",");
            }
        }
        sb.append("end"); //"0,1,..,end"
        return sb.toString();
    }

    private static boolean[] daysToBool(String days){
        String[] ds=days.split(",");  // ds[0]=0, ds[1]=1..ds[n]=end
        boolean[] result={false,false,false,false,false,false,false};

        try {
            for (int i = 0; i < result.length; i++) {
                for(int j=0;j<ds.length - 1;j++){
                    if(Integer.valueOf(ds[j])==i){
                        result[i]=true;
                    }
                }

            }
        } catch(Exception ex){
            Log.i("end","end reached or wrong input format");
        }

        return result;
    }

    private static String POST(String url){

        String result = "";

        try {
            //
            URLConnection connection = new URL(url).openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true); //se supone q post
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");

            OutputStream wr = connection.getOutputStream();
            wr.write(scheduledb.toString().getBytes("UTF-8"));
            wr.flush();
            wr.close();

            InputStream response = connection.getInputStream();

            // convert inputstream to string
            if(response != null)
                result = convertInputStreamToString(response);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", "El error" + e.toString());
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
                    scheduleId = json.getString("id");
                } else {
                    Log.d("get","el get "+json.toString());

                    departureTime=json.getString("Departure_Time");
                    arrivalTime=json.getString("Arrival_Time");
                    scheduleId=json.getString("id");
                    rideId=json.getString("pckmupTripId"); //trip=ride

                    boolean[] ds=daysToBool(json.getString("Days"));
                    Days=ds;

                }

            }catch (JSONException jex){
                Log.d("Json exception", jex.getLocalizedMessage());
            }
        }
    }
}

