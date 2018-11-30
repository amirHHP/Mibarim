package com.mibarim.main.ui.fragments.PlusFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.R;
import com.mibarim.main.models.InviteModel;
import com.mibarim.main.ui.activities.InviteActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Hamed on 3/5/2016.
 */
public class InviteFragment extends Fragment {

    private RelativeLayout layout;
    @Bind(R.id.share_btn)
    protected BootstrapButton share_btn;
    @Bind(R.id.inviteCode)
    protected TextView inviteCode;
    @Bind(R.id.pass_title)
    protected TextView pass_title;
    @Bind(R.id.pass_desc)
    protected TextView pass_desc;
    @Bind(R.id.driver_title)
    protected TextView driver_title;
    @Bind(R.id.driver_desc)
    protected TextView driver_desc;

    public InviteFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_invite, container, false);

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getView());
        reloadInvite();
    }

    public void reloadInvite() {
        final InviteModel inviteModel = ((InviteActivity) getActivity()).getInviteCode();
        inviteCode.setText(inviteModel.InviteCode);
        pass_title.setText(inviteModel.InvitePassTitle);
        pass_desc.setText(inviteModel.InvitePassenger);
        driver_title.setText(inviteModel.InviteDriverTitle);
        driver_desc.setText(inviteModel.InviteDriver);
        share_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((InviteActivity) getActivity()).ShareInvite(inviteModel.InviteLink);
                    return true;
                }
                return false;
            }
        });
    }
}
