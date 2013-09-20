package cz.s.v.captivesimulator;

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

    public String testUrl = "http://www.apple.com/library/test/success.html";
//            testUrl = "http://v.s.cz/success.html";
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
    public void performAppleHit() {
        AsyncHttpClient firstTryClient = new AsyncHttpClient();
        firstTryClient.setUserAgent("CaptiveNetworkSupport-209.39 wispr");
        firstTryClient.get(testUrl, new AppleResponseHandler(this));
    }

    /**
     * Zkusí emulovat Apple
     *
     * @return
     */
    private boolean performAppleTest() {
        boolean result = true;

        performAppleHit();

        return result;
    }

    private void showUserSettings() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        int testTypeIndex = 0;
        String testType = sharedPrefs.getString("prefTestType", "NULL");

        StringBuilder builder = new StringBuilder();

        String[] testTypes = getResources().getStringArray(R.array.testTargetType);
        String[] testTypeValues = getResources().getStringArray(R.array.testTargetValues);
        String[] testURLs = getResources().getStringArray(R.array.testTargetURL);
        String[] testResponses = getResources().getStringArray(R.array.testTargetResponse);

        for (int i = 0; i < testTypes.length; i++) {
            if (testType.equals(testTypeValues[i])) {
                testTypeIndex = i;
            }
        }

        builder.append("Test Type: " + testTypes[testTypeIndex]);
        builder.append("\nTest Target: " + testURLs[testTypeIndex] );
        builder.append("\nSuccess Response: " + testResponses[testTypeIndex] );

        TextView settingsTextView = (TextView) findViewById(R.id.response);

        settingsTextView.setText(builder.toString());
    }

}
