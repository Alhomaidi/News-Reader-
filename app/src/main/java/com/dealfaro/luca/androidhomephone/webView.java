package com.dealfaro.luca.androidhomephone;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;



import android.content.Intent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class webView extends AppCompatActivity {

    static final public String MYPREFS = "myprefs";
    static final public String PREF_URL = "restore_url";
    static final public String WEBPAGE_NOTHING = "about:blank";
    static public String MY_WEBPAGE = null;
    static final public String LOG_TAG = "webview_example";

    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                MY_WEBPAGE= null;
            } else {
                MY_WEBPAGE= extras.getString("url");
            }
        } else {
            MY_WEBPAGE= (String) savedInstanceState.getSerializable("url");
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        myWebView = (WebView) findViewById(R.id.webView1);
        myWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Binds the Javascript interface
        myWebView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
        myWebView.loadUrl(MY_WEBPAGE);
        myWebView.loadUrl("javascript:alert(\"Hello\")");

    }

    public class JavaScriptInterface {
        Context mContext; // Having the context is useful for lots of things,
        // like accessing preferences.

        /** Instantiate the interface and set the context */
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void myFunction(String args) {
           // final String myArgs = args;
            Log.i(LOG_TAG, "I am in the javascript call.");
            //runOnUiThread(new Runnable() {
              //  public void run() {
                   // Button v = (Button) findViewById(R.id.button1);
                 //   v.setText(myArgs);
                //}
           // });

        }

    }


    @Override
    public void onPause() {

        Method pause = null;
        try {
            pause = WebView.class.getMethod("onPause");
        } catch (SecurityException e) {
            // Nothing
        } catch (NoSuchMethodException e) {
            // Nothing
        }
        if (pause != null) {
            try {
                pause.invoke(myWebView);
            } catch (InvocationTargetException e) {
            } catch (IllegalAccessException e) {
            }
        } else {
            // No such method.  Stores the current URL.
            String suspendUrl = myWebView.getUrl();
            SharedPreferences settings = getSharedPreferences(webView.MYPREFS, 0);
            SharedPreferences.Editor ed = settings.edit();
            ed.putString(PREF_URL, suspendUrl);
            ed.commit();
            // And loads a URL without any processing.
            myWebView.clearView();
            myWebView.loadUrl(WEBPAGE_NOTHING);
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        // If we stored a pause URL, reload it.
        SharedPreferences settings = getSharedPreferences(webView.MYPREFS, 0);
        String suspendUrl = settings.getString(PREF_URL, null);
        if (suspendUrl != null) {
            myWebView.loadUrl(suspendUrl);
        }
    }




}
