package cz.s.v.captivenetprobe;

import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import cz.s.v.captivenetprobe.R;

public class TestCaptiveNetwork extends Activity {

    public Menu menu;
    /**
     * Pořadové číslo testu
     */
    public int testTryNumber = 0;

    /**
     * Výsledek: -1 netestovano, 0 chyba, 1 blokovano, 2 ok
     */
    public int testTryResult = -1;

    public String probeUrl = "";
    public String probeUserAgent = "";
    public String probeSuccessResponse = "";
    
    
    
    private static final int RESULT_SETTINGS = 1;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        showUserSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_refresh:
                if (isNetworkAvailable()) {
                performAppleTest();
            } else {
                Toast.makeText(getBaseContext(), "Offline :(", Toast.LENGTH_SHORT).show();
            }

                return true;

            case R.id.menu_settings:
                Intent i = new Intent(this, UserSettingActivity.class);
                startActivityForResult(i, RESULT_SETTINGS);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SETTINGS:
                showUserSettings();
                break;

        }

    }

    private boolean isNetworkAvailable() {
        boolean available = false;
        /**
         * Getting the system's connectivity service
         */
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        /**
         * Getting active network interface to get the network's status
         */
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isAvailable()) {
            available = true;
        }

        /**
         * Returning the status of the network
         */
        return available;
    }

    /**
     * Otestuje Captive capability na síti
     */
    public void performProbeHit() {
        AsyncHttpClient firstTryClient = new AsyncHttpClient();
        firstTryClient.setUserAgent(probeUserAgent);
        firstTryClient.get(probeUrl, new ProbeResponseHandler(this));
    }

    /**
     * Zkusí emulovat Apple
     *
     * @return
     */
    private boolean performAppleTest() {
        boolean result = true;

        performProbeHit();

        return result;
    }

    private void showUserSettings() {
        int testTypeIndex = 0;
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        String testType = sharedPrefs.getString("prefTestType", "NULL");
        if (testType.equals("NULL")) {
            Toast.makeText(getBaseContext(), "Please choose test type ...", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, UserSettingActivity.class);
            startActivityForResult(i, RESULT_SETTINGS);
        } else {

            StringBuilder builder = new StringBuilder();

            String[] testTypes = getResources().getStringArray(R.array.testTargetType);
            String[] testTypeValues = getResources().getStringArray(R.array.testTargetValues);
            String[] testURLs = getResources().getStringArray(R.array.testTargetURL);
            String[] testResponses = getResources().getStringArray(R.array.testTargetResponse);
            String[] testUserAgents = getResources().getStringArray(R.array.testTargetUserAgent);

            for (int i = 0; i < testTypes.length; i++) {
                if (testType.equals(testTypeValues[i])) {
                    testTypeIndex = i;
                }
            }

            probeSuccessResponse = testResponses[testTypeIndex];
            probeUrl = testURLs[testTypeIndex];
            probeUserAgent = testUserAgents[testTypeIndex];
            
            builder.append("Test Type: " + testTypes[testTypeIndex]);
            builder.append("\nUser Agent: " + testUserAgents[testTypeIndex]);
            builder.append("\nTest Target: " + testURLs[testTypeIndex]);
            builder.append("\nSuccess Response: " + testResponses[testTypeIndex]);

            TextView settingsTextView = (TextView) findViewById(R.id.response);

            settingsTextView.setText(builder.toString());
        }
    }

}
