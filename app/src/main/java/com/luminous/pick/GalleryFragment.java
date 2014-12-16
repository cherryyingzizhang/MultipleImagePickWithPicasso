package com.luminous.pick;

/**
 * Gallery Fragment which basically replaces the original CustomGalleryActivity the
 * original library had.
 */

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;


/**
 * This fragment allows the user to multi-pick images from their album.
 */
public class GalleryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private GridView mGridGallery;
    private GalleryAdapter mAdapter;
    private Handler mHandler;
    private ImageView mImgNoMedia;
    private LoaderManager mLoaderManager;
    private Button confirmButton; // this button can be used when user finished selecting all his images.

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.gallery_fragment, container, false);

        mGridGallery = (GridView) rootView.findViewById(R.id.gridGallery);
        mGridGallery.setFastScrollEnabled(true);
        mAdapter = new GalleryAdapter(getActivity());

        rootView.findViewById(R.id.llBottomContainer).setVisibility(View.VISIBLE);
        mGridGallery.setOnItemClickListener(mItemMulClickListener);

        mGridGallery.setAdapter(mAdapter);
        mImgNoMedia = (ImageView) rootView.findViewById(R.id.imgNoMedia);

        confirmButton = (Button) rootView.findViewById(R.id.btnGalleryOk);

        confirmButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // do whatever you want.
                ArrayList<String> filePaths = mAdapter.getSelectedImagesFilePaths();
                for (String filePath : filePaths)
                {
                    Log.e("FilePath For A Selected Image:","" + filePath);
                }
            }
        });

        mLoaderManager = getLoaderManager();
        mLoaderManager.initLoader(1, null, this);
        mHandler = new Handler();

        return rootView;
    }

    AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener()
    {

        @Override
        public void onItemClick(AdapterView<?> l, View v, int position, long id)
        {
            mAdapter.changeSelection(v, position);
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media._ID;
        return new CursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        final ArrayList<CustomGallery> galleryList = new ArrayList<CustomGallery>();
        try
        {
            if (data != null && data.moveToFirst())
            {
                while (data.moveToNext())
                {
                    CustomGallery item = new CustomGallery();

                    int dataColumnIndex = data
                            .getColumnIndex(MediaStore.Images.Media.DATA);

                    item.sdCardPath = data.getString(dataColumnIndex);
                    galleryList.add(item);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        new Thread()
        {
            @Override
            public void run()
            {
                Looper.prepare();
                mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mAdapter.addAll(galleryList);
                        checkImageStatus();
                    }
                });
                Looper.loop();
            }
        }.start();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        //required for implementation
    }

    private void checkImageStatus()
    {
        if (mAdapter.isEmpty())
        {
            mImgNoMedia.setVisibility(View.VISIBLE);
        }
        else
        {
            mImgNoMedia.setVisibility(View.GONE);
        }
    }
}
