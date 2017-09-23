package centercontrolleros.com.icentercontrolleros.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import centercontrolleros.com.icentercontrolleros.R;
import centercontrolleros.com.icentercontrolleros.ui.adapter.CustomMusicAdapter;
import centercontrolleros.com.icentercontrolleros.ui.model.MyResolveInfo;
import centercontrolleros.com.icentercontrolleros.ui.untils.Logger;


public class MusicMangerActivity extends AppCompatActivity {
    Toolbar mToobar;
    List<MyResolveInfo> mListMusic;
    private RecyclerView recycle_music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_manger);
        addControls();
    }

    private void addControls() {
        mToobar = (Toolbar) findViewById(R.id.toobarMusic);
        setSupportActionBar(mToobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mToobar.setTitle("Application Music");
        mToobar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recycle_music = (RecyclerView) findViewById(R.id.recycle_music);
        mListMusic = getListMediaAppInstalled(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycle_music.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recycle_music.getContext(),
                LinearLayoutManager.VERTICAL);
        recycle_music.addItemDecoration(dividerItemDecoration);
        CustomMusicAdapter mAdapter = new CustomMusicAdapter((ArrayList<MyResolveInfo>) mListMusic, MusicMangerActivity.this);
        recycle_music.setAdapter(mAdapter);
        for (int i = 0; i < mListMusic.size() ; i ++){
            Logger.d("mDataProvider", mListMusic.get(i).getResolveInfo().activityInfo.packageName);
        }
    }

    public List<MyResolveInfo> getListMediaAppInstalled(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> playerList;
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        playerList = packageManager.queryBroadcastReceivers(intent, 0);

        List<MyResolveInfo> myResolveInfos = new ArrayList<>();
        for (ResolveInfo resolveInfo : playerList) {
            myResolveInfos.add(new MyResolveInfo(resolveInfo));
        }
        return myResolveInfos;
    }
}
