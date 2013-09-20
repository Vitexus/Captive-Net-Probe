package cz.s.v.captivesimulator;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
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

            case R.id.action_settings:
                Toast.makeText(getBaseContext(), "Not yet implemented ", Toast.LENGTH_SHORT).show();
//                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

}
