package com.mibarim.main.ui.activities.worker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.mibarim.main.R;

/**
 * Created by mohammad hossein on 25/01/2018.
 */

public class workerDialog extends Dialog {
    Activity activity;
    public Button submit,cancel;

    public workerDialog(@NonNull Context context) {
        super(context);
        activity = (Activity) context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.worker_dialog);

        submit = (Button)findViewById(R.id.worker_dialog_submited);
        cancel = (Button)findViewById(R.id.worker_dialog_cancel);

    }

}
