package com.example.thirdeye;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;
import java.util.Locale;
import java.util.UUID;

public class SpeechTool extends Activity implements TextToSpeech.OnInitListener {
    TextToSpeech voice = null;
    String text = "";
    private final int ACT_CHECK_TTS_DATA = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Checking if we have TTS voice data
        Intent ttsIntent = new Intent();
        ttsIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(ttsIntent, ACT_CHECK_TTS_DATA);
        text = getIntent().getStringExtra("text");
    }

    private void saySomething(String text, int qmode) {
        if (qmode == 1)
            voice.speak(text, TextToSpeech.QUEUE_ADD, null, ""+UUID.randomUUID());
        else
            voice.speak(text, TextToSpeech.QUEUE_FLUSH, null, ""+UUID.randomUUID());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == ACT_CHECK_TTS_DATA) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // instantiating the TTS engine
                voice = new TextToSpeech(this, this);
            } else {
                // Data is missing, so we start the TTS installation process
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            if (voice != null) {
                int result = voice.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this,"TTS language is not supported", Toast.LENGTH_LONG).show();
                } else {
                    saySomething(text, 1);
                    finish();
                }
            }
        } else {
            Toast.makeText(this, "TTS initialization failed",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (voice != null) {
            voice.stop();
            voice.shutdown();
        }
        super.onDestroy();
    }
}
