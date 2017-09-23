package centercontrolleros.com.icentercontrolleros.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.List;

import centercontrolleros.com.icentercontrolleros.R;
import centercontrolleros.com.icentercontrolleros.model.AppInfor;
import centercontrolleros.com.icentercontrolleros.model.DatabaseHelper;
import centercontrolleros.com.icentercontrolleros.ui.ReOrderListenner.OnCustomerListChangedListener;
import centercontrolleros.com.icentercontrolleros.ui.ReOrderListenner.OnStartDragListener;
import centercontrolleros.com.icentercontrolleros.ui.fragment.DraggableExampleFragment;
import centercontrolleros.com.icentercontrolleros.ui.fragment.ExampleDataProviderFragment;
import centercontrolleros.com.icentercontrolleros.ui.model.AbstractDataProvider;

public class ManagerAppActivity extends AppCompatActivity implements OnCustomerListChangedListener, OnStartDragListener {

    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";


    RelativeLayout mRelativeLayout;
    RecyclerView mRecyclerView;
    DatabaseHelper mDatabase;
    Toolbar mToolbar;
    Context mContext;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_app);
        mContext = this;
        addControls();
        addEvents();

        initView();
    }


    private void initView() {
        getSupportFragmentManager().beginTransaction()
                .add(new ExampleDataProviderFragment(), FRAGMENT_TAG_DATA_PROVIDER)
                .commit();

        getSupportFragmentManager().beginTransaction().replace(R.id.content_list_drag, new DraggableExampleFragment(), FRAGMENT_LIST_VIEW).commit();

    }


    private void addEvents() {
        mToolbar = (Toolbar) findViewById(R.id.toobarFavourite);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mToolbar.setTitle("Application Favourite");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(ManagerAppActivity.this, AddFavouriteActivity.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mIntent);
            }
        });

    }


    private void addControls() {
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_add);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleFavourite);

        mDatabase = new DatabaseHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();

    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }


    public AbstractDataProvider getDataProvider() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DATA_PROVIDER);
        return ((ExampleDataProviderFragment) fragment).getDataProvider();
    }

    @Override
    public void onNoteListChanged(List<AppInfor> favouriteApps) {

    }
}
