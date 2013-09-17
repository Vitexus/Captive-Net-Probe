package cz.s.v.applecaptive;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.widget.Toast;
import com.loopj.android.http.*;

public class TestCaptiveNetwork extends Activity {

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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (isNetworkAvailable()) {

                if (performAppleTest()) {
                    Toast.makeText(getBaseContext(), "Online :)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "Blocked :(", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getBaseContext(), "Offline :(", Toast.LENGTH_SHORT).show();
            }

                return true;

            case R.id.action_settings:
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
     * Výsledek posledního tesu
     */
    public boolean testResult = false;
    public boolean testDone = false;

    /**
     * Otestuje Captive capability na síti
     *
     * @return test result
     */
    private void performAppleHit() {
        String testUrl = "https://www.apple.com/library/test/success.html";
//            testUrl = "http://v.s.cz/success.html";

        Toast.makeText(getBaseContext(), "First attempt " + testUrl , Toast.LENGTH_SHORT).show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.setUserAgent("CaptiveNetworkSupport-209.39 wispr");
        client.get(testUrl, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {

                String responseSuccess = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2//EN\">\n"
                        + "<HTML>\n"
                        + "<HEAD>\n"
                        + "	<TITLE>Success</TITLE>\n"
                        + "</HEAD>\n"
                        + "<BODY>\n"
                        + "Success\n"
                        + "</BODY>\n"
                        + "</HTML>\n";
                if (response.equals(responseSuccess)) {
                    testResult = true;
                    
                } else {
                    testResult = false;
                     Toast.makeText(getBaseContext(), response.toString() , Toast.LENGTH_SHORT).show();
                }
                testDone = true;
            }
            
            @Override
            public void onFailure(Throwable e, String response) {
                 Toast.makeText(getBaseContext(),  response.toString() + " Error: "  + e.toString() , Toast.LENGTH_SHORT).show();
            }
            
        });
    }

    /**
     * Zkusí emulovat Apple
     *
     * @return
     */
    private boolean performAppleTest() {
        boolean result;
        
        performAppleHit();
 
        result = testResult;

        return result;
    }

}
