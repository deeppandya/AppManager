package com.deeppandya.appmanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.deeppandya.appmanager.adapter.AppAdapter;
import com.deeppandya.appmanager.asynctask.AppListLoader;
import com.deeppandya.appmanager.model.AppModel;
import com.deeppandya.appmanager.util.CommonFunctions;
import com.deeppandya.appmanager.util.DividerItemDecoration;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<AppModel>> {

    private RecyclerView recyclerView;
    private AppAdapter mAdapter;
    int asc, sortby;
    public SharedPreferences Sp;

    public static final int ID_LOADER_APP_LIST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new AppAdapter(MainActivity.this);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(ID_LOADER_APP_LIST,null,this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sortby:
                CommonFunctions.showSortDialog(MainActivity.this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<List<AppModel>> onCreateLoader(int id, Bundle args) {
        return new AppListLoader(MainActivity.this, sortby, asc);
    }

    @Override
    public void onLoadFinished(Loader<List<AppModel>> loader, List<AppModel> data) {
// set new data to adapter
        mAdapter.setAppList(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<AppModel>> loader) {
        mAdapter.setAppList(null);
    }

    public void getSortModes() {
        int t = Integer.parseInt(Sp.getString("sortbyApps", "0"));
        if (t <= 2) {
            sortby = t;
            asc = 1;
        } else if (t > 2) {
            asc = -1;
            sortby = t - 3;
        }
    }
}
