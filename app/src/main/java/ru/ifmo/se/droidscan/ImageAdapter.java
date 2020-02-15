package ru.ifmo.se.droidscan;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }


    public int getCount() {
        return images.length;
    }

    public Object getItem(int position) {
        return images[position];
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(390, 400));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        Glide
                .with(mContext)
                .load(images[position])
                .into(imageView);

        return imageView;
    }

    //тут захардкожено я потом подумаю, как сделать иначе
    private File[] images = new File("/storage/emulated/0/Android/data/ru.ifmo.se.droidscan/files/DCIM").listFiles();


}
