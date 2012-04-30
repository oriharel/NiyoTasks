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
import com.niyo.auto.map.AutoMapActivity;

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
        
        
        
        
    }
    
    public void navigateTo(View view) 
	{
    	String locationStr = (String)view.getTag();
    	String[] coordinsatesStrArray = locationStr.split(",");
    	AutoPoint to = new AutoPoint(Double.parseDouble(coordinsatesStrArray[0]), Double.parseDouble(coordinsatesStrArray[1]));
    	Intent intent = AutoMapActivity.getCreationIntent(this, to);
    	startActivity(intent);
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