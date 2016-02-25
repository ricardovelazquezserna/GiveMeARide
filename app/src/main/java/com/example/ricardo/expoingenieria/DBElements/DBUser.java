package com.example.ricardo.expoingenieria.DBElements;

/**
 * Created by Valeria on 2/24/16.
 */

import android.os.AsyncTask;
import android.util.Log;


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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DBUser {

    SimpleDateFormat dateFormat = null;
    private String url = "https://afternoon-spire-5809.herokuapp.com/api/pckmupUsers";

    private String firstName = "", lastName = "", userName = "", institution = "", handicap = "", interests = "", Email = "";
    private Calendar birthDay = null;
    private String Password = ""; //POST ONLY
    private String userid = ""; //READ ONLY

    private boolean post = true; //get =false

    static JSONObject userdb;

    public DBUser() {
        //inicializando...
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        birthDay = Calendar.getInstance();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }

    public String getInstitution() {
        return institution;
    }

    public String getHandicap() {
        return handicap;
    }

    public String getInterests() {
        return interests;
    }

    public String getEmail() {
        return Email;
    }

    public Calendar getBirthDay() {
        return birthDay;
    }

    public String getPassword() {
        return Password;
    }

    public String getUserid() {
        return userid;
    }

    public void create(String firstname, String lastname, Calendar bday, String username,
                       String email, String password, String institution, String handicap, String interests) {
        post = true;

        userdb = new JSONObject();
        try {
            userdb.put("FirstName", firstname);
            this.firstName = firstname;
            userdb.put("LastName", lastname);
            this.lastName = lastname;
            userdb.put("BirthDay", dateFormat.format(bday.getTime())); //ej. "2015-12-31"
            this.birthDay = bday;
            userdb.put("UserName", username);
            this.userName = username;
            userdb.put("email", email); //debe ser tipo algo@algo.com , sino no funciona!! y no se debe repetir..
            this.Email = email;
            userdb.put("password", password);
            this.Password = password;
            userdb.put("Institution", institution);
            this.institution = institution;
            userdb.put("Handicap", handicap);
            this.handicap = handicap;
            userdb.put("Interests", interests);
            this.interests = interests;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*{
          "FirstName": "V",
          "LastName": "C",
          "BirthDay": "2015-12-07",
          "UserName": "vc",
          "Institution": "algo",
          "Handicap": "no",
          "Interests": "me",
          "password":"1234",
          "email": "v@v.com"
        }*/

        new HttpAsyncTask().execute(url);
    }

    public void create(String firstname, String lastname, Calendar bday, String username,
                       String email, String password) {

        userdb = new JSONObject();
        try {
            userdb.put("FirstName", firstname);
            this.firstName = firstname;
            userdb.put("LastName", lastname);
            this.lastName = lastname;
            userdb.put("BirthDay", dateFormat.format(bday.getTime()));
            this.birthDay = bday;
            userdb.put("UserName", username);
            this.userName = username;
            userdb.put("email", email);
            this.Email = email;
            userdb.put("password", password);
            this.Password = password;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HttpAsyncTask().execute(url);
    }

    //Este metodo no lo he probado !!!!! pero si lo prueban me avisan si funciona o no, por favor
    public void create(String firstname, String lastname, String email, String password) {

        //BDay y UserName son elementos Requeridos, sin implementar en UI (quitar requerido o implementar en registro UI?)
        Calendar bday = Calendar.getInstance();
        try {
            bday.setTime(dateFormat.parse("2016-01-01")); //default por el momento
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String username=email; //default por el momento

        userdb = new JSONObject();
        try {
            userdb.put("FirstName", firstname);
            this.firstName = firstname;
            userdb.put("LastName", lastname);
            this.lastName = lastname;
            userdb.put("BirthDay", dateFormat.format(bday.getTime()));
            this.birthDay = bday;
            userdb.put("UserName", username);
            this.userName = username;
            userdb.put("email", email);
            this.Email = email;
            userdb.put("password", password);
            this.Password = password;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HttpAsyncTask().execute(url);
    }

    public void getByEmail(String email) {

        post = false;

        String where = "";

        try {
            userdb = new JSONObject();
            userdb.put("email", email);

            JSONObject w = new JSONObject();

            w.put("where", userdb);
            where = w.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String geturl = url + "/findOne?filter=" + where + "&access_token=6kmhbUhA0uGsg5yo9a9JApoNtGAKCL5nxDFZa7QCmhk3EG1o5nPMrtGiUolBene2";
        new HttpAsyncTask().execute(geturl);
    }

    //getRides(){}


    public static String POST(String url) {

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
            wr.write(userdb.toString().getBytes("UTF-8"));
            wr.flush();
            wr.close();

            InputStream response = connection.getInputStream();

            // convert inputstream to string
            if (response != null)
                result = convertInputStreamToString(response);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", "El error" + e.toString());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private static String GET(String url) {

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
            if (response != null)
                result = convertInputStreamToString(response);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", "El error" + e.toString());
        }

        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            if (post)
                return POST(urls[0]);
            else
                return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject json = new JSONObject(result);

                if (post) {
                    userid = json.getString("id");
                } else {
                    Log.d("get", "el get " + json.toString());
                    //
                    firstName = json.getString("FirstName");
                    lastName = json.getString("LastName");
                    userid = json.getString("id");

                    String bday = json.getString("BirthDay");
                    birthDay = parseDate(bday);

                    userName = json.getString("UserName");
                    Email = json.getString("email");

                    if (json.has("Institution"))
                        institution = json.getString("Institution");
                    else
                        institution = "";

                    if (json.has("Handicap"))
                        handicap = json.getString("Handicap");
                    else
                        handicap = "";

                    if (json.has("Interests"))
                        interests = json.getString("Interests");
                    else
                        interests = "";

                }

            } catch (JSONException jex) {
                Log.d("Json exception", jex.getLocalizedMessage());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /////////////////////
    private Calendar parseDate(String input) throws java.text.ParseException {

        //NOTE: SimpleDateFormat uses GMT[-+]hh:mm for the TZ which breaks
        //things a bit.  Before we go on we have to repair this.
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

        //this is zero time so we need to add that TZ indicator for
        if (input.toLowerCase().endsWith("Z")) {
            input = input.substring(0, input.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;

            String s0 = input.substring(0, input.length() - inset);
            String s1 = input.substring(input.length() - inset, input.length());

            input = s0 + "GMT" + s1;
        }

        Date date = df.parse(input);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal;

    }

    /////////////////////
}
