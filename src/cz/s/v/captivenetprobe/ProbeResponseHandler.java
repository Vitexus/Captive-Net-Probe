/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.s.v.captivenetprobe;

import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import cz.s.v.captivenetprobe.R;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vitex
 */
public class ProbeResponseHandler extends AsyncHttpResponseHandler {
   public String responseBody = "";

    private TestCaptiveNetwork appContext;

    public ProbeResponseHandler(TestCaptiveNetwork context) {
        appContext = context;
    }

    @Override
    public void onStart() {
        appContext.testTryNumber++;
        appContext.testTryResult = -1;
        TextView text = (TextView) appContext.findViewById(R.id.result);
        text.setText("Test under progress. Please wait ...");

        MenuItem statusIcon = appContext.menu.findItem(R.id.action_icon);
        statusIcon.setIcon(android.R.drawable.ic_menu_rotate);

        WebView myWebView = (WebView) appContext.findViewById(R.id.webview);
        myWebView.loadData("?", "text/plain", "utf-8");
        Toast.makeText(appContext.getBaseContext(), "#" + appContext.testTryNumber + " test", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(String response) {

        responseBody = response;

        if (response.equals(appContext.probeSuccessResponse)) {
            appContext.testTryResult = 2;
        } else {
            appContext.testTryResult = 1;
        }

        TextView responseCode = (TextView) appContext.findViewById(R.id.response);
        responseCode.setMovementMethod(new ScrollingMovementMethod());
        responseCode.setText(response.toString());

    }

    @Override
    public void onFailure(Throwable e, String response) {
        Toast.makeText(appContext.getBaseContext(), response.toString() + " Error: " + e.toString(), Toast.LENGTH_SHORT).show();
        appContext.testTryResult = 0;
        TextView text = (TextView) appContext.findViewById(R.id.response);
        text.setText(response.toString());

    }

    @Override
    public void onFinish() {
        Toast.makeText(appContext.getBaseContext(), "Finish", Toast.LENGTH_SHORT).show();
        TextView resumeText = (TextView) appContext.findViewById(R.id.result);

        WebView myWebView = (WebView) appContext.findViewById(R.id.webview);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setAppCacheEnabled(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        myWebView.clearHistory();
        myWebView.clearFormData();
        myWebView.clearCache(true);
        myWebView.setWebViewClient(new RedirectingWebViewClient(appContext));

        Map<String, String> noCacheHeaders = new HashMap<String, String>(2);
        noCacheHeaders.put("Pragma", "no-cache");
        noCacheHeaders.put("Cache-Control", "no-cache, must-revalidate, no-store");

        MenuItem statusIcon = appContext.menu.findItem(R.id.action_icon);

        if (appContext.testTryResult == 2) {
            statusIcon.setIcon(R.drawable.ic_state_online);
            resumeText.setText("Online :)");
            myWebView.loadUrl(appContext.probeUrl, noCacheHeaders);
        }

        if (appContext.testTryResult == 1) {
            statusIcon.setIcon(R.drawable.ic_state_blocked);
            resumeText.setText("Blocked :|");
            myWebView.loadUrl(appContext.probeUrl, noCacheHeaders);

        }

        if (appContext.testTryResult == 0) {
            statusIcon.setIcon(R.drawable.ic_state_unknown);
            resumeText.setText("Offline :(");
            myWebView.loadData("Network error...", "text/html", "utf-8");
        }

    }

    /**
     * WebViev that can handle redirects
     */
    private class RedirectingWebViewClient extends WebViewClient {

        private final TestCaptiveNetwork appContext;

        private RedirectingWebViewClient(TestCaptiveNetwork context) {
            appContext = context;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            if (url.equals(appContext.probeUrl)) {
                appContext.testTryNumber++;
            }
            TextView text = (TextView) appContext.findViewById(R.id.result);
            text.setText("WebView loading url: " + url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (url.equals(appContext.probeUrl)) {
                Toast.makeText(appContext, "Page Loaded ... #" + appContext.testTryNumber, Toast.LENGTH_SHORT).show();

                /*            
                 long interval = System.currentTimeMillis() - m_start;
                 Toast.makeText(appContext, "Loaded this webpage [" + refreshCount + "] "
                 + "times in [" + interval + "] ms", Toast.LENGTH_SHORT).show();
                 */
                if (appContext.testTryResult == 1) { //Was BLOCKED - try again
                    if (appContext.testTryNumber == 2) {
                        AsyncHttpClient secondTryClient = new AsyncHttpClient();
                        secondTryClient.setUserAgent(appContext.probeUserAgent);
                        secondTryClient.get(appContext.probeUrl, new ProbeResponseHandler(appContext));
                    }
                }

            }
        }
    }

}
