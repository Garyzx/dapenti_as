package net.dasherz.dapenti.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import net.dasherz.dapenti.R;
import net.dasherz.dapenti.fragment.PrefsFragement;

public class FragmentPreferences extends ActionBarActivity {
	private static final String TAG = FragmentPreferences.class.getSimpleName();

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		getFragmentManager().beginTransaction().replace(R.id.setting_content, new PrefsFragement()).commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
