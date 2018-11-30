package com.mibarim.main.ui.activities.worker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mibarim.main.R;
import com.mibarim.main.authenticator.TokenRefreshActivity;
import com.mibarim.main.util.SafeAsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohammad hossein on 22/01/2018.
 */

public class workerServiceActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private workerRecyclerAdapter mAdapter;
    private List<workerModel> items;
    ItemClickListiner itemClickListiner;
    String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worker_service);
        authToken = getIntent().getStringExtra("worker_token");
        items = new ArrayList<>();
        setRecyclerView();

        itemClickListiner = new ItemClickListiner() {
            @Override
            public void onButtonClick(View view, final int position) {
                final workerDialog dialog = new workerDialog(workerServiceActivity.this);
                dialog.show();
                dialog.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendRequestToServer(position);
                        dialog.dismiss();
                    }
                });

            }
        };

        refresh();
    }

    public void setRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.worker_recycle);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManager);
    }

    public void refresh() {
        new SafeAsyncTask<Boolean>() {
            List<workerModel> models;

            @Override
            public Boolean call() throws Exception {
                workerModel workerModel = new workerModel();
                GenerateWorkerService generateWorkerService = new GenerateWorkerService();
                models = new ArrayList<>();
                items = generateWorkerService.services(workerModel, "Bearer " + authToken);


                return true;
            }

            @Override
            protected void onSuccess(Boolean aBoolean) throws Exception {
                fillRecycle();
                super.onSuccess(aBoolean);
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);
            }

        }.execute();


    }

    public void sendRequestToServer(int position) {
        final workerModel model = items.get(position);
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {

                GenerateWorkerService generateWorkerService = new GenerateWorkerService();
                generateWorkerService.requestServices(model.worker_Id, "Bearer " + authToken);
                return true;
            }

            @Override
            protected void onSuccess(Boolean aBoolean) throws Exception {
                final workerDialogSubmit dialog = new workerDialogSubmit(workerServiceActivity.this);
                dialog.show();
                dialog.submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                super.onSuccess(aBoolean);
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                Toast.makeText(getApplicationContext(),"مشکلی در ارتباط با سرور پیش آمده است",Toast.LENGTH_LONG).show();
                super.onException(e);
            }
        }.execute();
    }

    public void fillRecycle() {
        mAdapter = new workerRecyclerAdapter(this, itemClickListiner, items);
        recyclerView.setAdapter(mAdapter);

    }

    public interface ItemClickListiner {
        public void onButtonClick(View view, int position);
    }
}
