package com.mibarim.main.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.ui.activities.MainCardActivity;
import com.mibarim.main.util.FontsOverride;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Base activity for a Bootstrap activity which does not use fragments.
 */
public abstract class BootstrapActivity extends AppCompatActivity {

    /**
     * The {@link Tracker} used to record screen views.
     */
    //protected Tracker mTracker;

    @Inject protected Bus bus;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BootstrapApplication.component().inject(this);

        //iransens font
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/IRANSans(FaNum)_Light.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/IRANSans(FaNum)_Light.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/IRANSans(FaNum)_Light.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/IRANSans(FaNum)_Light.ttf");

        // Obtain the shared Tracker instance.
        /*BootstrapApplication application = (BootstrapApplication) getApplication();
        mTracker = application.getDefaultTracker();
*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public void setContentView(final int layoutResId) {
        super.setContentView(layoutResId);

        // Used to inject views with the Butterknife library
        ButterKnife.bind(this);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            // This is the home button in the top left corner of the screen.
            case android.R.id.home:
                // Don't call finish! Because activity could have been started by an
                // outside activity and the home button would not operated as expected!
                final Intent homeIntent = new Intent(this, MainCardActivity.class);
                startActivity(homeIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
