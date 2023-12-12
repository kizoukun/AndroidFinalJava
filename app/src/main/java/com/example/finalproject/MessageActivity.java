package com.example.finalproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    public String senderId;
    public String receiverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Intent intent = getIntent();
        senderId = intent.getStringExtra("senderId");
        receiverId = intent.getStringExtra("receiverId");
        String receiverName = intent.getStringExtra("receiverName");
        TextView receiverNameTextView = findViewById(R.id.receiverName);
        receiverNameTextView.setText(receiverName);

        Button sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(v -> {
            TextView messageTextView = findViewById(R.id.message);
            String message = messageTextView.getText().toString();
            if(message.isEmpty()) {
                Toast.makeText(this, "Message is required", Toast.LENGTH_SHORT).show();
                return;
            }

            messageTextView.setText("");
            addMessage(true, message);
            addMessageToDatabase(message);
        });
        Button homeBtn = findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, MainActivity.class);
            startActivity(homeIntent);
            finish();
        });
        getMessages();

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

    public void clearMessages() {
        LinearLayout messageContainer = findViewById(R.id.chatList);
        messageContainer.removeAllViews();
    }

    public void getMessages() {
        String url = MainActivity.BASE_URL + "/get-chat.php?sender=" + senderId + "&receiver=" + receiverId;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                boolean isSuccess = jsonObject.getBoolean("success");
                String message = jsonObject.getString("message");
                if(!isSuccess) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    return;
                }
                clearMessages();
                JSONArray data = jsonObject.getJSONArray("data");
                for(int i = 0; i < data.length(); i++) {
                    JSONObject messageData = data.getJSONObject(i);
                    String messageText = messageData.getString("message_text");
                    String senderId = messageData.getString("sender_id");
                    addMessage(senderId.equals(this.senderId), messageText);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(this, "Failed to get chat list", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }

    public void addMessageToDatabase(String messageText) {
        String url = MainActivity.BASE_URL + "/send-chat.php";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                boolean isSuccess = jsonObject.getBoolean("success");
                String message = jsonObject.getString("message");
                if(!isSuccess) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
                getMessages();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new java.util.HashMap<>();
                params.put("sender_id", senderId);
                params.put("receiver_id", receiverId);
                params.put("message", messageText);
                return params;
            }
        };
        queue.add(request);
    }
}
