package net.dasherz.dapenti.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.dasherz.dapenti.R;
import net.dasherz.dapenti.adapter.AppSectionsPagerAdapter;
import net.dasherz.dapenti.constant.Constants;
import net.dasherz.dapenti.fragment.FavouriteFragment;
import net.dasherz.dapenti.fragment.PentiBaseFragment;
import net.dasherz.dapenti.util.LogUtil;

public class MainActivity extends ActionBarActivity  {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int TAB_COUNT = 4;

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;
    ActionBar mActionBar;
    FragmentManager mFragmentManager = getSupportFragmentManager();
    private boolean doubleBackToExitPressedOnce;

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.updateOnlineConfig(this);
        setContentView(R.layout.activity_main);
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager(), getResources()
                .getStringArray(R.array.tab_name_list), TAB_COUNT);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mActionBar = getSupportActionBar();
        initActionBar();
        initViewpager();
        LogUtil.d(TAG, "onCreate");
    }

    private void initActionBar() {
        //mActionBar.setHomeButtonEnabled(false);
        //mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        LogUtil.d(TAG, "initActionBar");
    }

    private void initViewpager() {
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //mActionBar.setSelectedNavigationItem(position);
            }
        });
        SharedPreferences settings = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
        mViewPager.setCurrentItem(Integer.parseInt(settings.getString("defaultChannel", "0")));
        LogUtil.d(TAG, "initViewpager");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, FragmentPreferences.class);
                startActivity(intent);
                return true;
            case R.id.refresh:
                if (mViewPager.getCurrentItem() < 3) {
                    PentiBaseFragment pentiFragment = (PentiBaseFragment) getActiveFragment(mViewPager, mViewPager.getCurrentItem());
                    pentiFragment.getLatestData();
                    return true;
                } else {
                    FavouriteFragment pentiFragment = (FavouriteFragment) getActiveFragment(mViewPager, 3);
                    pentiFragment.getLatestData();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    // http://stackoverflow.com/questions/8785221/retrieve-a-fragment-from-a-viewpager
    public Fragment getActiveFragment(ViewPager container, int position) {
        String name = makeFragmentName(container.getId(), position);
        return mFragmentManager.findFragmentByTag(name);
    }

    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.press_again), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
        LogUtil.d(TAG, "onBackPressed");
    }

}
