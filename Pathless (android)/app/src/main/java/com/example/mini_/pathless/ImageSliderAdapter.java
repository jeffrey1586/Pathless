package com.example.mini_.pathless;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;


/**
 * This ViewPagerAdapter is as an adapter for the image slider in the DetailActivity.
 * The code is based on a tutorial from Sanktips on Youtube. However the it is modified
 * to fit the Pathless app.
 */

public class ImageSliderAdapter extends PagerAdapter {

    // Vars.
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<String> images;

    public ImageSliderAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        // Setting up the inflater, view and imageView.
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout, null);
        ImageView imageView = view.findViewById(R.id.imageView);


        // Setting the image with Glide.
        String uri = images.get(position);
        RequestOptions options = new RequestOptions();
        Glide.with(context)
                .load(uri)
                .apply(options.centerCrop())
                .into(imageView);
        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;
    }

    // The remove method of the viewPager.
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}
