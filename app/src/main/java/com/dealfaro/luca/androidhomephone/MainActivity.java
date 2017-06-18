package com.dealfaro.luca.androidhomephone;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = "hw4";
    RequestQueue queue;

    private class ListElement {
        ListElement() {};

        ListElement(String title, String subtitle, String url) {
            titleE = title;
            subtitleE = subtitle;
            urlE = url;
        }

        public String subtitleE;
        public String titleE;
        public String urlE;


    }



    private ArrayList<ListElement> aList;

    private class MyAdapter extends ArrayAdapter<ListElement> {

        int resource;
        Context context;

        public MyAdapter(Context _context, int _resource, List<ListElement> items) {
            super(_context, _resource, items);
            resource = _resource;
            context = _context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout newView;

            ListElement w = getItem(position);

            // Inflate a new view if necessary.
            if (convertView == null) {
                newView = new LinearLayout(getContext());
                LayoutInflater vi = (LayoutInflater)
                        getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                vi.inflate(resource,  newView, true);
            } else {
                newView = (LinearLayout) convertView;
            }

            // Fills in the view.
            TextView tv = (TextView) newView.findViewById(R.id.itemText);
            TextView tv2 = (TextView) newView.findViewById(R.id.itemText2);
            tv.setText(w.titleE);
            tv2.setText(w.subtitleE);

            // Set a listener for the whole list item.
            newView.setTag(w.urlE);
            newView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, webView.class);
                    //String url = null;
                    String s = v.getTag().toString();
                    i.putExtra("url", s);
                    startActivity(i);
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, s, duration);
                    toast.show();

                }
            });

            return newView;
        }
    }

    private MyAdapter aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aList = new ArrayList<ListElement>();
        aa = new MyAdapter(this, R.layout.list_element, aList);
        ListView myListView = (ListView) findViewById(R.id.listView);
        myListView.setAdapter(aa);
        myGet();
        aa.notifyDataSetChanged();
    }




    public void myGet() {

        queue = Volley.newRequestQueue(this);
        String url = "https://luca-ucsc-teaching-backend.appspot.com/hw4/get_news_sites";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        TextView tv3 = (TextView)findViewById(R.id.error);
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = response.getJSONArray("news_sites");
                            tv3.setText(" ");
                            if(jsonArray.length()<1){

  tv3.setText("This array of news sites is empty. Please press the refresh button");
                            }

                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
 if(jsonObject.isNull("title")||jsonObject.isNull("url")) {
     continue;
 }else {
     if (jsonObject.isNull("subtitle")) {
         aList.add(new ListElement(
                 jsonObject.getString("title"),
                 " ",
                 jsonObject.getString("url")
         ));
     }else {

         aList.add(new ListElement(
                 jsonObject.getString("title"),
                 jsonObject.getString("subtitle"),
                 jsonObject.getString("url")
         ));
     }
     }

 }
                            aa.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }                       }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, "Error: " + error.getMessage());
            }
        });

        queue.add(req);
    }



    public void clickRefresh (View v) {
        Log.i(LOG_TAG, "Requested a refresh of the list");
        aList.clear();
        myGet();
    }




}
