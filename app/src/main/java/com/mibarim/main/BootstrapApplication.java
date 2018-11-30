

package com.mibarim.main;

import android.app.Application;
import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.os.StrictMode;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.mibarim.main.util.FontsOverride;
import com.onesignal.OneSignal;

/**
 * Mibarim application
 */
public abstract class BootstrapApplication extends Application {

    private static BootstrapApplication instance;
    private com.mibarim.main.BootstrapComponent component;

    /**
     * Create main application
     */
    public BootstrapApplication() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        // Call syncHashedEmail anywhere in your app if you have the user's email.
        // This improves the effectiveness of OneSignal's "best-time" notification scheduling feature.
        // OneSignal.syncHashedEmail(userEmail);

        init();


        instance = this;

        // Perform injection
        //Injector.init(this, )
        component = DaggerComponentInitializer.init();

        onAfterInjection();
        //for font awesome
        TypefaceProvider.registerDefaultIconSets();
        //iransens font
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/IRANSans(FaNum)_Light.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/IRANSans(FaNum)_Light.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/IRANSans(FaNum)_Light.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/IRANSans(FaNum)_Light.ttf");
        /*CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                //.addCustomStyle(AppCompatButton.class, R.attr.buttonStyle)
                .setDefaultFontPath("fonts/IRANSans(FaNum)_Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );*/
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }
    public static com.mibarim.main.BootstrapComponent component() {
        return instance.component;
    }

    protected abstract void onAfterInjection();

    protected abstract void init();

    public static BootstrapApplication getInstance() {
        return instance;
    }

    public com.mibarim.main.BootstrapComponent getComponent() {
        return component;
    }

    public final static class DaggerComponentInitializer {

        public static com.mibarim.main.BootstrapComponent init() {
            return DaggerBootstrapComponent.builder()
                    .androidModule(new AndroidModule())
                    .bootstrapModule(new BootstrapModule())
                    .build();
        }

    }

    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

}
