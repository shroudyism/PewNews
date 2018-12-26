package com.example.shroudyism.pewnews;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.shroudyism.wallpaperfinder.R;
import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder> {

    Context  context;
    public ArrayList<News> mData;
    public Activity mActivity;

    public NewsAdapter(ArrayList<News> data, Activity activity) {
        this.mData = data;
        this.mActivity = activity;
    }


    @NonNull
    @Override
    public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        view.findViewById(R.id.linear);
        view.setBackgroundColor(Color.rgb(204,229,255));
        //view.setOnClickListener(new MyOnClickListener());

        RecyclerView recyclerView = view.findViewById(R.id.rv);

        new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                Log.d("BHAi","SWIPED");
                mData.remove(i);

            }
        };


        return new NewsHolder(view);
    }
    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            RecyclerView rv = (RecyclerView) v.findViewById(R.id.rv);
            if(rv!=null) {
                int itemPosition = rv.indexOfChild(v);
            }


            // URL url=new URL();
           // Intent intent =new Intent(Intent.ACTION_VIEW,Uri.parse(mData.get(rv.indexOfChild(v)).url.toString()));

            //context.startActivity(intent);


        }
    }
    @Override
    public void onBindViewHolder(@NonNull final NewsHolder newsHolder, final int i) {

        News news=mData.get(i);

        newsHolder.setHeading(news.getTitle().toString());
        newsHolder.setContent(news.getContent().toString());
        newsHolder.setUrl(news.getUrl().toString());
    }

    @Override
    public int getItemCount() {

        if(mData==null)
        return 0;

        return mData.size();
    }



    public class NewsHolder extends RecyclerView.ViewHolder {

    TextView heading;
    TextView content;
    String url;
    TextView date;

        public TextView getDate() {
            return date;
        }

        public void setDate(TextView date) {
            this.date = date;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public TextView getHeading() {
            return heading;
        }

        public void setHeading(String heading) {
            this.heading.setText(heading);
        }

        public TextView getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content.setText(content);
        }

        public NewsHolder(@NonNull View itemView) {
        super(itemView);

        heading =(TextView) itemView.findViewById(R.id.heading);
        content =(TextView) itemView.findViewById(R.id.content);
        date=(TextView) itemView.findViewById(R.id.date);


    }
}

}
