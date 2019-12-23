package com.example.botdialogflow;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonElement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIContext;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;


public class MainActivity extends AppCompatActivity implements AIListener, View.OnClickListener {

    private AIService mAIService;
    TextView txtResponse;
    private AIRequest mAIRequest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final AIConfiguration config = new AIConfiguration("ab4ca8b0b2b84e31a578970a4e24badd",
                AIConfiguration.SupportedLanguages.Spanish,
                AIConfiguration.RecognitionEngine.System);

        txtResponse = findViewById(R.id.txtRespuesta);

        final int PERMISSION_REQUEST_CODE = 1;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to RECORD_AUDIO - requesting it");
                String[] permissions = {
                        Manifest.permission.RECORD_AUDIO}; requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }

        AIRequest request;

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                AIRequest request = new AIRequest("Hola");
                try {
                    mAIService.textRequest(request);

                } catch (AIServiceException e) {
                    e.printStackTrace();
                }
            }
        });


        FloatingActionButton btnSend = findViewById(R.id.fab);

        mAIService = AIService.getService(this, config);
        mAIService.setListener(this);

        btnSend.setOnClickListener(this);

    }

    @Override
    public void onResult(AIResponse response) {
        Result r = response.getResult();
        String parametersString = "";


        /*try {
            mAIService.textRequest(request);
        } catch (AIServiceException e) {
            e.printStackTrace();
        }*/


        Log.e("Response", r.toString());
        if(r.getParameters() != null && !r.getParameters().isEmpty()){
            for (final Map.Entry<String, JsonElement> entry: r.getParameters().entrySet()){
                parametersString += "(" + entry.getKey() + ", " + entry.getValue()+ ")";
                Log.e("Respuesta: ",parametersString);
            }
        }

        txtResponse.setText("Mensaje: "+ r.getResolvedQuery() +
                " /n Action: "+ r.getAction()+
                "/n Respuesta: " + r.getFulfillment().getSpeech()
        );
    }

    @Override
    public void onError(AIError error) {
        Toast.makeText(this, "No entendió", Toast.LENGTH_LONG);
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.fab:
                mAIService.startListening();
                break;
        }

    }


}
