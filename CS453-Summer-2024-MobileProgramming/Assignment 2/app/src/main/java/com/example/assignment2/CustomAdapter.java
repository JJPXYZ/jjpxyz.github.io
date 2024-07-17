package com.example.assignment2;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<Animal> {
    private final List<Animal> animals;

    public CustomAdapter(Context context, int resource, List<Animal> animals) {
        super(context, resource, animals);
        this.animals = animals;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_row, parent, false);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.rowText);
            holder.imageView = convertView.findViewById(R.id.rowImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Animal animal = getItem(position);
        holder.textView.setText(animal.getName());
        try {
            InputStream inputStream = getContext().getAssets().open(animal.getImage());
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            holder.imageView.setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
