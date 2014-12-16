package com.luminous.pick;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * An adapter for the gallery_fragment.
 */
public class GalleryAdapter extends BaseAdapter
{

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<CustomGallery> data = new ArrayList<CustomGallery>();

    public GalleryAdapter(Context c)
    {
        inflater = (LayoutInflater) c
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = c;
    }

    @Override
    public int getCount()
    {
        return data.size();
    }

    @Override
    public CustomGallery getItem(int position)
    {
        return data.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    // use this to get all of the selected pictures' file-paths
    public ArrayList<String> getSelectedImagesFilePaths()
    {
        ArrayList<String> dataT = new ArrayList<String>();

        for (int i = 0; i < data.size(); i++)
        {
            if (data.get(i).isSelected)
            {
                dataT.add(data.get(i).sdCardPath);
            }
        }

        return dataT;
    }

    public void addAll(ArrayList<CustomGallery> files)
    {
        try
        {
            this.data.clear();
            this.data.addAll(files);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        notifyDataSetChanged();
    }

    public void changeSelection(View v, int position)
    {

        if (data.get(position).isSelected)
        {
            data.get(position).isSelected = false;
        }
        else
        {
            data.get(position).isSelected = true;
        }

        ((ViewHolder) v.getTag()).imgQueueMultiSelected.setSelected(data
                .get(position).isSelected);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        final ViewHolder holder;
        if (convertView == null)
        {

            convertView = inflater.inflate(R.layout.gallery_item, null);
            holder = new ViewHolder();
            holder.imgQueue = (ImageView) convertView
                    .findViewById(R.id.imgQueue);

            holder.imgQueueMultiSelected = (ImageView) convertView
                    .findViewById(R.id.imgQueueMultiSelected);

            holder.imgQueueMultiSelected.setVisibility(View.VISIBLE);

            convertView.setTag(holder);

        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Picasso.with(mContext)
                .load("file://" + data.get(position).sdCardPath)
                .resizeDimen(R.dimen.imageDimen, R.dimen.imageDimen) //example dimensions
                .centerCrop()
                .into(holder.imgQueue);

        holder.imgQueueMultiSelected.setSelected(data
                .get(position).isSelected);

        return convertView;
    }

    public class ViewHolder
    {
        ImageView imgQueue;
        ImageView imgQueueMultiSelected;
    }
}