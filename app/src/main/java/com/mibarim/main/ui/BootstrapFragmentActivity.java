package com.mibarim.main.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mibarim.main.BootstrapApplication;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;


/**
 * Base class for all Bootstrap Activities that need fragments.
 */
public class BootstrapFragmentActivity extends AppCompatActivity {

    @Inject protected Bus eventBus;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BootstrapApplication.component().inject(this);
    }

    @Override
    public void setContentView(final int layoutResId) {
        super.setContentView(layoutResId);

        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventBus.unregister(this);
    }
}
