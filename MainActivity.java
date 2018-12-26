package com.example.shroudyism.pewnews;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shroudyism.wallpaperfinder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import static com.example.shroudyism.pewnews.NotificationUtils.remindUser;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    static ProgressBar pb;
    static EditText etQuery;
    Button  btnSearch;
    static TextView tv;
    EditText etDate;
    EditText etCountry;
    Button btnReset;
    ScrollView sv;
    RecyclerView rv;
    NewsAdapter adapter;
    Context context;
    Button notification;
    static ArrayList<News> newsCollection=new ArrayList<News>();


    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void testNotification (View view){

        remindUser(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();

        inflater.inflate(R.menu.menusettings,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.actionSetting)
        {

            Intent intent=new Intent(MainActivity.this,Settings.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv=findViewById(R.id.rv);
        etQuery =findViewById(R.id.etQuery);
        btnSearch=findViewById(R.id.btnSearch);
        btnReset = findViewById(R.id.btnReset);
        etDate=findViewById(R.id.date);
        pb=findViewById(R.id.pb);
        etCountry=findViewById(R.id.country);
        notification=findViewById(R.id.notification);

        final String link=etQuery.getText().toString();

        rv=(RecyclerView)findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);

        rv.addOnItemTouchListener( new RecyclerItemClickListener(context, rv ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                try {
                    URL url=new URL(adapter.mData.get(position).getUrl());



                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(url.toString()));
                    startActivity(browserIntent);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onLongItemClick(View view, int position) {
                // do whatever
            }
        }));

        adapter=new NewsAdapter(newsCollection,this);
        rv.setAdapter(adapter);


        notification.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                testNotification(v);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               while(newsCollection.size()!=0)
                newsCollection.remove(0);

               adapter.notifyDataSetChanged();

            }
        });



        btnSearch.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                new  DownloadTask().execute();
            }
        });

        adapter.notifyDataSetChanged();


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();
                adapter.mData.remove(position);
                adapter.notifyDataSetChanged();

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rv);

        
        setUpSharedPreferences();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setUpSharedPreferences() {

        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key.equals("date") && sharedPreferences.getBoolean(key,false))
            etDate.setVisibility(View.VISIBLE);
        else
            etDate.setVisibility(View.INVISIBLE);

        if(key.equals("country") && sharedPreferences.getBoolean(key,false)){
            etCountry.setVisibility(View.VISIBLE);
        }
        else
            etCountry.setVisibility(View.INVISIBLE);
    }


    class DownloadTask extends AsyncTask<String, Void, ArrayList<News>> {
        public static final String TAG = "REST";

        public String dataParsed="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity.pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<News> doInBackground(String... params) {

            String query=MainActivity.etQuery.getText().toString();

            try {

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("newsapi.org")
                        .appendPath("v2")
                        .appendPath("top-headlines")
                        .appendQueryParameter("q",query)
                        .appendQueryParameter("apikey", "8e7a05776ac24fb39a6d89291c03b4a5");

                String myurl = builder.build().toString();

                URL url=new URL(myurl);

                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();

                InputStream inputStream=connection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

                connection.setRequestMethod("GET");
                connection.connect();

                String data="";

                String line="";

                while(line != null){
                    line=br.readLine();
                    data+=line;

                }



                News news=new News();
                JSONObject JO=new JSONObject(data);
                JSONArray JA= (JSONArray) JO.get("articles");

                for(int i=0;i<JA.length();i++) {
                    JSONObject jO = (JSONObject) JA.get(i);

                    news=new News();

                   if(jO.isNull("title")||jO.isNull("content"))
                       continue;

                    news.setTitle(jO.get("title").toString());
                    Log.d("XYZ","Bhaji Chal gya");
                    news.setContent(jO.get("content").toString());

                    news.setUrl(jO.get("url").toString());
                    String d=jO.get("publishedAt").toString().substring(0,10);
                    news.setDate(d);
                    newsCollection.add(news);
                }


            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(ArrayList<News> news) {
            super.onPostExecute(news);

            Log.d("Free",dataParsed);
            pb.setVisibility(View.INVISIBLE);


        }
    }
}
