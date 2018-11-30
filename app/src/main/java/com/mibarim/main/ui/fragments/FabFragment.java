package com.mibarim.main.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

//import com.getkeepsafe.taptargetview.TapTarget;
//import com.getkeepsafe.taptargetview.TapTargetView;
//import com.mibarim.driver.BootstrapApplication;

//import com.mibarim.driver.ui.activities.MainActivity;
import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.R;
import com.mibarim.main.ui.activities.MainActivity;
import com.mibarim.main.ui.activities.MainCardActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Hamed on 3/5/2016.
 */
public class FabFragment extends Fragment {

    private RelativeLayout layout;

    @Bind(R.id.fab)
    protected FloatingActionButton fab;

    public FabFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_fab, container, false);
        return layout;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, layout);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ((MainCardActivity) getActivity()).gotoRouteLists();
                ((MainActivity) getActivity()).gotoRouteLists();
            }
        });


//        displayUserGuide();


    }

    public void hideTheFab() {
        fab.hide();
//        LinearInterpolator linearInterpolator = new LinearInterpolator();
//        fab.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
//        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
//        int fab_bottomMargin = layoutParams.bottomMargin;
//        fab.animate().setDuration(200).translationY(fab.getHeight() + 100).setInterpolator(new LinearInterpolator()).start();


    }

    public void showTheFab() {
        if (!fab.isShown())
            fab.show();
//        fab.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
    }


    /*public void displayUserGuide() {
        SharedPreferences prefs = getActivity().getSharedPreferences(
                "com.mibarim.driver", Context.MODE_PRIVATE);
        if (prefs.getInt("ShowTheMainGuide", 0) == 0) {

            TapTargetView.showFor(getActivity(), TapTarget.forView(fab, "انتخاب مسیر", "پیش از هر کار ایستگاه مورد نظر خود را انتخاب کنید.")
                    .cancelable(false)
                    .textColor(android.R.color.white)
                    .targetCircleColor(android.R.color.white)
                    .outerCircleColor(R.color.google_blue)
                    .transparentTarget(false)
                    .tintTarget(false)
                    .drawShadow(true), new TapTargetView.Listener() {
                @Override
                public void onTargetClick(TapTargetView view) {
                    super.onTargetClick(view);
                    // .. which evidently starts the sequence we defined earlier
                    ((MainActivity) getActivity()).showUserGuide();
                }

            });

        }

        *//*else {
            ((MainActivity) getActivity()).showUserGuide();
        }*//*

    }*/


}
