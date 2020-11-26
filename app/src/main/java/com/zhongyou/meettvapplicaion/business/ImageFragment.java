package com.zhongyou.meettvapplicaion.business;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.entities.SpaceStatusPublish;
import com.zhongyou.meettvapplicaion.utils.helper.ImageHelper;
import com.squareup.picasso.Picasso;

public class ImageFragment extends Fragment {

    private static final String PARAM = "space_status_publish";

    private SpaceStatusPublish spaceStatusPublish;

    public static ImageFragment newInstance(SpaceStatusPublish spaceStatusPublish) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM, spaceStatusPublish);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            spaceStatusPublish = getArguments().getParcelable(PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        ImageView imageView = view.findViewById(R.id.image_view);
        Log.v("onCreateView", spaceStatusPublish.getUrl());
        Picasso.with(getActivity()).load(ImageHelper.getThumb(spaceStatusPublish.getUrl())).into(imageView);
        return view;
    }

}
