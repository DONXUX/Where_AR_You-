package com.example.wherearyou;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class ListViewAdapter extends BaseAdapter {
    Bitmap bitmap;
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();

    public ListViewAdapter(){ }

    @Override
    public int getCount(){
        return listViewItemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_friends_info, parent, false);
        }

        final CircleImageView iconImageView = (CircleImageView) convertView.findViewById(R.id.friend_photo);
        final TextView friendNameView = (TextView) convertView.findViewById(R.id.friend_name);

        final ListViewItem listViewItem = listViewItemList.get(position);
        Log.d(TAG, "순서2");

        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {

                    URL url = new URL(listViewItem.getIcon());


                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();

                    bitmap = BitmapFactory.decodeStream(is);

                    Log.d(TAG, "에러이름" + listViewItem.getFriendName());
                    Log.d(TAG, "에러사진" + listViewItem.getIcon());
                    iconImageView.setImageBitmap(bitmap);
                    friendNameView.setText(listViewItem.getFriendName());

                } catch (IOException ex) {

                }
            }
        };
        mThread.start();
        try {
            mThread.join();
            Log.d(TAG, "에러이름" + listViewItem.getFriendName());
            Log.d(TAG, "에러사진" + listViewItem.getIcon());
            iconImageView.setImageBitmap(bitmap);
            friendNameView.setText(listViewItem.getFriendName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return convertView;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public Object getItem(int position){
        return listViewItemList.get(position);
    }

    public void addItem(String name, String photoUrl){
        ListViewItem item = new ListViewItem();

        Log.d(TAG, "순서1");
        Log.d(TAG, "순서밑에" + photoUrl);
        Log.d(TAG, "순서밑에" + name);
        item.setIcon(photoUrl);
        item.setFriendName(name);
        listViewItemList.add(item);
    }
}
