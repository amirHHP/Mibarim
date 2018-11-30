package com.mibarim.main.ui.fragments.helpFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.mibarim.main.R;

import butterknife.ButterKnife;

/**
 * Created by Hamed on 5/3/2016.
 */
public class fourthSlide extends Fragment {

    /*@Bind(R.id.slide_4_msg)
    protected TextView slide_4_msg;*/

    private Animation slideUp;
    private Animation slideDown;

    public fourthSlide() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.slide_4, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getView());
        slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        //slide_4_msg.setAnimation(slideUp);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        /*if (slide_4_msg != null) {
            if (isVisibleToUser) {
                slide_4_msg.startAnimation(slideDown);
            } else {
                slide_4_msg.startAnimation(slideUp);
            }
        }*/
    }

}