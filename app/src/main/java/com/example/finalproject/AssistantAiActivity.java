package com.example.finalproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class AssistantAiActivity extends AppCompatActivity {
    private final String AI_URL = "http://192.168.1.6:3000/ai";

    private String threadId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant_ai);
        addMessage(false, "Hi there, I'm Assistant AI. How can I help you?");
        Button sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(v -> {
            TextView messageTextView = findViewById(R.id.message);
            String message = messageTextView.getText().toString();
            if(message.isEmpty()) {
                return;
            }

            messageTextView.setText("");
            addMessage(true, message);
            getMessageFromAssistant(message);
        });
        Button homeBtn = findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, MainActivity.class);
            startActivity(homeIntent);
            finish();
        });
    }

    public void addMessage(boolean right, String message) {
        LinearLayout messageContainer = findViewById(R.id.chatList);
        LinearLayout messageRow = new LinearLayout(this);
        TextView messageTextView = new TextView(this);
        messageTextView.setText(message);
        messageTextView.setTextSize(20);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        if(right) {
            shape.setCornerRadii(new float[] {64, 64, 64, 64, 0, 0, 64, 64});
            shape.setColor(Color.parseColor("#B7EEFF"));
            messageRow.setGravity(Gravity.END);
        } else {
            shape.setCornerRadii(new float[] {64, 64, 64, 64, 64, 64, 0, 0});
            shape.setColor(Color.parseColor("#ffffff"));
            messageRow.setGravity(Gravity.START);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(20, 20, 20, 20);
        messageTextView.setPadding(30, 30, 30, 30);
        messageTextView.setLayoutParams(params);
        messageTextView.setBackground(shape);

        messageRow.addView(messageTextView);
        messageContainer.addView(messageRow);
    }


    public void getMessageFromAssistant(String msg) {
        JSONObject json = new JSONObject();
        try {
            json.put("message", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = AI_URL;
        if(threadId != null) {
            url = AI_URL + "/" + threadId;
        }
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        addMessage(false, "Assistant AI is typing...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                System.out.println(response);
                JSONObject data = new JSONObject(response);
                JSONArray choices = data.getJSONArray("choices");
                threadId = data.getString("threadId");
                JSONObject choice = choices.getJSONObject(0);
                String message = choice.getString("content");
                LinearLayout messageContainer = findViewById(R.id.chatList);
                int lastIndex = messageContainer.getChildCount() - 1;
                if (lastIndex >= 0) {
                    messageContainer.removeViewAt(lastIndex);
                }
                addMessage(false, message);
                ScrollView scrollView = findViewById(R.id.scrollView);
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Log.e("error", error.toString());
            LinearLayout messageContainer = findViewById(R.id.chatList);
            int lastIndex = messageContainer.getChildCount() - 1;
            if (lastIndex >= 0) {
                messageContainer.removeViewAt(lastIndex);
            }
        }) {

            public Map<String, String> getHeaders() {
                Map<String, String> params = new java.util.HashMap<>();
                params.put("Content-Type", "application/json");
                return params;
            }
            public byte[] getBody() {
                return json.toString().getBytes();
            }

            public String getBodyContentType() {
                return "application/json";
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(stringRequest);
    }
}
