package com.niyo.auto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.niyo.ClientLog;
import com.niyo.R;

public class AutoActivity extends Activity implements OnInitListener {
    /** Called when the activity is first created. */
	
	private static final String LOG_TAG = AutoActivity.class.getSimpleName();
//	private SpeechRecognizer mRecognizer;
	private TextToSpeech mTts;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.where_to_layout);
        Log.d(LOG_TAG, "onCreate started");
//        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
//        mRecognizer.setRecognitionListener(new AutoRecognitionListener());
//        
//        
        mTts = new TextToSpeech(this,
                this  // TextToSpeech.OnInitListener
                );
//        
//        final AutoActivity context = this;
//        findViewById(R.id.gotoEvents).setOnClickListener(new OnClickListener()
//        {
//			@Overridejj
//			public void onClick(View v) {
//				Intent intent = new Intent(context, NextEventsActivity.class);
//				startActivity(intent);
//			}
//		});
        
        WebView webView = (WebView)findViewById(R.id.webView1);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setLoadWithOverviewMode(true);
		settings.setUseWideViewPort(true);
		settings.setDomStorageEnabled(true);
		webView.setWebViewClient(new AutoWVClient());
		JSInterface inter = new JSInterface();
		webView.addJavascriptInterface(inter, "Native");
		webView.setWebChromeClient(new WebChromeClient() 
		{
			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) 
			{
				return super.onJsAlert(view, url, message, result);
		    }
		});
		
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setAppCacheEnabled(true);
        webView.loadUrl("file:///android_asset/html/directions.html");
        
        
    }
    
    public void navigateTo(View view) 
	{
    	String locationStr = (String)view.getTag();
    	String[] coordinsatesStrArray = locationStr.split(",");
    	String geoQuery = "geo:"+coordinsatesStrArray[0]+","+coordinsatesStrArray[1];
    	Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(geoQuery)); 
    	if (isCallable(intent))
    	{
    		startActivity(intent);
    		processRoute(coordinsatesStrArray);
    	}
    	else
    	{
    		Toast.makeText(this, "Go get yourself a map app", Toast.LENGTH_LONG).show();
    	}
	}
    
    private void processRoute(String[] coordinsatesStrArray) {
		AutoPoint to = new AutoPoint(Double.parseDouble(coordinsatesStrArray[0]), Double.parseDouble(coordinsatesStrArray[1]));
		String serviceString = Context.LOCATION_SERVICE;
		LocationManager locationManager;
		locationManager = (LocationManager)getSystemService(serviceString);
		
		String provider = LocationManager.GPS_PROVIDER;
		Location location = locationManager.getLastKnownLocation(provider);
		ClientLog.d(LOG_TAG, "location is "+location);
		AutoPoint from = new AutoPoint(location.getLatitude(), location.getLongitude());
		
		WebView webView = (WebView)findViewById(R.id.webView1);
		webView.loadUrl("javascript: calcRoute("+from.getLat()+","+from.getLon()+","+to.getLat()+","+to.getLon()+")");
	}

	private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 
            PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
//    @Override
//    protected void onResume()
//    {
//    	super.onResume();
//    	Log.d(LOG_TAG, "onResume started");
//    }
    
    @Override
    protected void onPause()
    {
    	super.onPause();
//    	mRecognizer.stopListening();
    }
    
    @Override
    protected void onDestroy()
    {
    	super.onDestroy();
//    	mRecognizer.destroy();
    	mTts.shutdown();
    }
    
    class AutoRecognitionListener implements RecognitionListener
    {

		@Override
		public void onBeginningOfSpeech() 
		{
			Log.d(LOG_TAG, "speech begun");
			
		}

		@Override
		public void onBufferReceived(byte[] buffer) {
			
		}

		@Override
		public void onEndOfSpeech() {
			Log.d(LOG_TAG, "speech ended");
			
		}

		@Override
		public void onError(int error) {
			Log.d(LOG_TAG, "error in speech with "+error);
			
		}

		@Override
		public void onEvent(int eventType, Bundle params) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPartialResults(Bundle partialResults) 
		{
			String[] matches = partialResults.getStringArray(RecognizerIntent.EXTRA_RESULTS);
			for (String string : matches) {
				Log.d(LOG_TAG, string+",");
			}
			
			
		}

		@Override
		public void onReadyForSpeech(Bundle params) {
			
		}

		@Override
		public void onResults(Bundle results) {
			Log.d(LOG_TAG, "onResults");
			ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		}

		@Override
		public void onRmsChanged(float rmsdB) {
			// TODO Auto-generated method stub
			
		}
    	
    }
    
    private void sayHello() {
        // Select a random hello.
        String hello = "Where do you want to go?";
//        mTts.speak(hello,
//            TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
//            null);
        
//        Intent intent = new Intent();
//        intent.putExtra("calling_package", "com.niyo.auto");
//		mRecognizer.startListening(intent);
    }
    

 // Implements TextToSpeech.OnInitListener.
    public void onInit(int status) {
        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            // Set preferred language to US english.
            // Note that a language may not be available, and the result will indicate this.
            int result = mTts.setLanguage(Locale.US);
            // Try this someday for some interesting results.
            // int result mTts.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
               // Lanuage data is missing or the language is not supported.
                Log.e(LOG_TAG, "Language is not available.");
            } else {
                // Check the documentation for other possible result codes.
                // For example, the language may be available for the locale,
                // but not for the specified country and variant.

                // The TTS engine has been successfully initialized.
                // Allow the user to press the button for the app to speak again.
                // Greet the user.
                sayHello();
            }
        } else {
            // Initialization failed.
            Log.e(LOG_TAG, "Could not initialize TextToSpeech.");
        }
    }
}