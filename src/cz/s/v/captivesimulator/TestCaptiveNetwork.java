package cz.s.v.captivesimulator;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class TestCaptiveNetwork extends Activity {

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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (isNetworkAvailable()) {
                performAppleTest();
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
     * Výsledek: -1 netestovano, 0 chyba, 1 blokovano, 2 ok
     */
    public int testResult = -1;

    /**
     * Otestuje Captive capability na síti
     *
     * @return test result
     */
    public void performAppleHit() {

        Toast.makeText(getBaseContext(), "First attempt " + testUrl, Toast.LENGTH_SHORT).show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.setUserAgent("CaptiveNetworkSupport-209.39 wispr");
        client.get(testUrl, new AsyncHttpResponseHandler() {
            String responseSuccessAppleSSL = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2//EN\">\n"
                    + "<HTML>\n"
                    + "<HEAD>\n"
                    + "	<TITLE>Success</TITLE>\n"
                    + "</HEAD>\n"
                    + "<BODY>\n"
                    + "Success\n"
                    + "</BODY>\n"
                    + "</HTML>\n";

            String responseSuccessApple = "<HTML><HEAD><TITLE>Success</TITLE></HEAD><BODY>Success</BODY></HTML>";

            public String responseBody = "";

            @Override
            public void onStart() {
                testResult = -1;
                TextView text = (TextView) findViewById(R.id.result);
                text.setText("Test under progress. Please wait ...");
                WebView myWebView = (WebView) findViewById(R.id.webview);

                myWebView.clearHistory();
                myWebView.clearFormData();
                myWebView.clearCache(true);

                WebSettings webSettings = myWebView.getSettings();
                webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

                myWebView.loadData("?", "text/plain", "utf-8");
            }

            @Override
            public void onSuccess(String response) {

                responseBody = response;

                if (response.equals(responseSuccessApple)) {
                    testResult = 2;

                } else {
                    response.length();
                    responseSuccessApple.length();

                    testResult = 1;
                }

                TextView responseCode = (TextView) findViewById(R.id.response);
                responseCode.setMovementMethod(new ScrollingMovementMethod());
                responseCode.setText(response.toString());

            }

            @Override
            public void onFailure(Throwable e, String response) {
                Toast.makeText(getBaseContext(), response.toString() + " Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                testResult = 0;
                TextView text = (TextView) findViewById(R.id.response);
                text.setText(response.toString());
            }

            @Override
            public void onFinish() {
                Toast.makeText(getBaseContext(), "Finish", Toast.LENGTH_SHORT).show();
                TextView resumeText = (TextView) findViewById(R.id.result);

                    WebView myWebView = (WebView) findViewById(R.id.webview);
                    WebSettings webSettings = myWebView.getSettings();
                    webSettings.setJavaScriptEnabled(true);
                    webSettings.setBuiltInZoomControls(true);
                    myWebView.clearHistory();
                    myWebView.clearFormData();
                    myWebView.clearCache(true);
                    webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
                
                
                if (testResult == 2) {
                    resumeText.setText("Online :)");
                    myWebView.loadUrl(testUrl);
                }

                if (testResult == 1) {
                    resumeText.setText("Blocked :|");
                    myWebView.loadUrl(testUrl);
                }

                if (testResult == 0) {
                    resumeText.setText("Offline :(");
                    myWebView.loadData("Network error...", "text/html", "utf-8");
                }

            }

        });

    }

    /**
     * WebViev that can handle redirects
     */
    private class RedirectingWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
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
