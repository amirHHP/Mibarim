package com.mibarim.main.ui.fragments.helpFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Hamed on 5/3/2016.
 */
public class secondSlide extends Fragment {

    private Tracker mTracker;
    @Bind(R.id.slide_2_msg)
    protected TextView slide_2_msg;

    private Animation slideUp;
    private Animation slideDown;
    public secondSlide() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BootstrapApplication application = (BootstrapApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.slide_2, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTracker.setScreenName("secondSlide");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Help").setAction("secondSlide").build());
        ButterKnife.bind(this, getView());
        /*slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        slide_2_msg.setAnimation(slideUp);*/
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        /*if (slide_2_msg != null) {
            if (isVisibleToUser) {
                slide_2_msg.startAnimation(slideDown);
            } else {
                slide_2_msg.startAnimation(slideUp);
            }
        }*/
    }

}