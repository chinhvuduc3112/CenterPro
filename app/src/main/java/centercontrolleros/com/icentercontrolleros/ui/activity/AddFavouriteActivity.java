package centercontrolleros.com.icentercontrolleros.ui.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import centercontrolleros.com.icentercontrolleros.R;
import centercontrolleros.com.icentercontrolleros.model.AppInfor;
import centercontrolleros.com.icentercontrolleros.ui.adapter.CustomListAppAdapter;
import centercontrolleros.com.icentercontrolleros.ui.untils.Logger;

public class AddFavouriteActivity extends AppCompatActivity {
    RecyclerView mRecyclerApp;
    private PackageManager mPackageManager = null;
    private List<ApplicationInfo> mListApp = null;
    private CustomListAppAdapter mAdapter = null;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_favourite);
        addControls();
        new LoadApplications().execute();
    }

    private void addControls() {
        mToolbar = (Toolbar) findViewById(R.id.toobarFavourite);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Application Install");

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mRecyclerApp = (RecyclerView) findViewById(R.id.recycleappinstall);
        mPackageManager = getPackageManager();
//        mListApp = checkForLaunchIntent(mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA));
//        mAdapter = new CustomListAppAdapter((ArrayList<ApplicationInfo>) mListApp, AddFavouriteActivity.this);


//        for (int i = 0; i < mListApp.size(); i++) {
//            Logger.d("mListFavourite", mListApp.get(i).packageName);
//        }
    }


    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (null != mPackageManager.getLaunchIntentForPackage(info.packageName)) {
                    applist.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applist;
    }

private class LoadApplications extends AsyncTask<Void, Void, List<ApplicationInfo>> {
    private ProgressDialog progress = null;

    @Override
    protected List<ApplicationInfo> doInBackground(Void... params) {
        mListApp = checkForLaunchIntent(mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA));
        return mListApp;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(List<ApplicationInfo> result) {
        RecyclerView.LayoutManager mlayoutManager = new LinearLayoutManager(getApplicationContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerApp.getContext(), LinearLayoutManager.VERTICAL);
        mRecyclerApp.addItemDecoration(dividerItemDecoration);
        mRecyclerApp.setItemAnimator(new DefaultItemAnimator());
        mRecyclerApp.setLayoutManager(mlayoutManager);
        mAdapter = new CustomListAppAdapter((ArrayList<ApplicationInfo>) result, AddFavouriteActivity.this);
        mRecyclerApp.setAdapter(mAdapter);
        progress.dismiss();
        super.onPostExecute(result);
    }

    @Override
    protected void onPreExecute() {
        progress = ProgressDialog.show(AddFavouriteActivity.this, null,
                "Loading application info...");
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
}
