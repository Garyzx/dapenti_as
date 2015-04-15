package net.dasherz.dapenti.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.dasherz.dapenti.R;
import net.dasherz.dapenti.database.DBConstants;
import net.dasherz.dapenti.database.DBHelper;
import net.dasherz.dapenti.util.LogUtil;
import net.dasherz.dapenti.util.NetUtil;

import java.io.IOException;
import java.lang.reflect.Field;

public class PentiDetailActivity extends ActionBarActivity  {
    private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html; charset=UTF-8";

    private static final String TAG = PentiDetailActivity.class.getSimpleName();
    TextView titleView;
    WebView tuguaWebView;
    String title, url, link;
    private int mContentType;
    long id;
    ProgressBar progressBar;
    private DBHelper dbhelper;
    private ShareActionProvider mShareActionProvider;
    private Intent mShareIntent;

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
        getOverflowMenu();
        setContentView(R.layout.activity_penti_detail);
        dbhelper = DBHelper.getInstance(getApplication());
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleView = (TextView) findViewById(R.id.tuguaTitle);
        tuguaWebView = (WebView) findViewById(R.id.tuguaDetailPage);
        tuguaWebView.loadData(getResources().getString(R.string.html_loading), TEXT_HTML_CHARSET_UTF_8, null);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        Intent intent = getIntent();
        id = intent.getLongExtra(DBConstants.ITEM_ID, -1);
        title = intent.getStringExtra(DBConstants.ITEM_TITLE);
        url = intent.getStringExtra(DBConstants.ITEM_DESCRIPTION);
        link = intent.getStringExtra(DBConstants.ITEM_LINK);
        mContentType = intent.getIntExtra(DBConstants.ITEM_CONTENT_TYPE, 0);
        setTitleForThisActivity();
        titleView.setText(title);
        boolean whetherBlockImage = NetUtil.whetherBlockImage(this);
        tuguaWebView.getSettings().setBlockNetworkImage(whetherBlockImage);
        tuguaWebView.getSettings().setJavaScriptEnabled(true);
        tuguaWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        tuguaWebView
                .getSettings()
                .setUserAgentString(
                        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36");
        new LoadPageTask().execute(url);
    }

    private void setTitleForThisActivity() {
        switch (mContentType) {

            case DBConstants.CONTENT_TYPE_PICTURE:
                getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_picture_detail));
                break;
            case DBConstants.CONTENT_TYPE_TUGUA:
            default:
                getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_tugua_detail));
                break;
        }
    }

    private void getOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class LoadPageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String lines = null;
            try {
                lines = NetUtil.getContentOfURL(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (lines == null) {
                lines = getResources().getString(R.string.get_data_failed);
            }
            String content = "<html xmlns=\"http://www.w3.org/1999/xhtml\" ><head><meta http-equiv='content-type' content='text/html; charset=utf-8' /></head><body>"
                    + lines + "</body>";
            lines = null;
            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            // LogUtil.d(TAG, result);
            tuguaWebView.loadDataWithBaseURL(url, result, null, TEXT_HTML_CHARSET_UTF_8, url);
            progressBar.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tugua_detail, menu);
		MenuItem item = menu.findItem(R.id.share);
        mShareActionProvider=(ShareActionProvider) MenuItemCompat.getActionProvider(item);
        mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        mShareIntent.putExtra(Intent.EXTRA_TITLE, title);
        mShareIntent.putExtra(Intent.EXTRA_TEXT, url);
        mShareIntent.setType("text/plain");
        mShareActionProvider.setShareIntent(mShareIntent);
        LogUtil.d(TAG, "onCreateOptionsMenu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int menuId = item.getItemId();
        switch (menuId) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.add_favourite:
                new AddToFavTask().execute(String.valueOf(id));
                break;
            case R.id.copy_title:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("title", title);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, getResources().getString(R.string.already_copied_to_clip), Toast.LENGTH_SHORT).show();
                break;
            case R.id.open_in_browser:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(browserIntent);
                break;

            default:
                break;
        }
        return true;
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    public class AddToFavTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(PentiDetailActivity.this, getResources().getString(R.string.already_added_to_fav),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(String... params) {

            dbhelper.addToFav(params[0]);
            return null;

        }

    }

}
