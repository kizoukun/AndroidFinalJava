package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL = "http://192.168.1.6/final";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());
        if(!sharedPreferencesManager.getIsLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;

        }

        TextView logout = findViewById(R.id.logoutBtn);
        logout.setOnClickListener(v -> {
            sharedPreferencesManager.setIsLoggedIn(false);
            sharedPreferencesManager.clearUserData();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        Button assistantAiBtn = findViewById(R.id.assistantAiBtn);
        assistantAiBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AssistantAiActivity.class);
            startActivity(intent);
            finish();
        });

        TextView requestMessage = findViewById(R.id.requestMessageBtn);
        if(sharedPreferencesManager.getUserRole() != null && sharedPreferencesManager.getUserRole().equalsIgnoreCase("lecturer")) {
            requestMessage.setText("Requests");
            requestMessage.setOnClickListener(v -> {
                Intent intent = new Intent(this, ListRequestsActivity.class);
                startActivity(intent);
                finish();
            });
        } else {
            requestMessage.setOnClickListener(v -> {
                Intent intent = new Intent(this, RequestMessageActivity.class);
                startActivity(intent);
                finish();
            });
        }

        String url = BASE_URL + "/list-chat.php?user=" + sharedPreferencesManager.getUserId();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                boolean isSuccess = jsonObject.getBoolean("success");
                if(!isSuccess) {
                    System.out.println("Failed to get chat list");
                    return;
                }
                JSONArray data = jsonObject.getJSONArray("data");

                for(int i = 0; i < data.length(); i++) {
                    JSONObject chat = data.getJSONObject(i);
                    String receiverFirstName = chat.getString("receiver_first_name");
                    String receiverLastName = chat.getString("receiver_last_name");
                    String receiverName = receiverFirstName + " " + receiverLastName;
                    String receiverId = chat.getString("receiver_id");
                    String message = "";
                    String lastMessageSender = chat.getString("last_message_sender");
                    if(lastMessageSender.equals(sharedPreferencesManager.getUserId())) {
                        message = "You: ";
                    }
                    message += chat.getString("last_message");
                    addChat(sharedPreferencesManager.getUserId(), receiverId,receiverName, message);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(this, "Failed to get chat list", Toast.LENGTH_SHORT).show());
        queue.add(stringRequest);


    }


    public void addChat(String senderId, String receiverId, String receiverName, String lastMessage) {
        LinearLayout linearLayout = findViewById(R.id.chatList);

        LinearLayout newChat = new LinearLayout(this);
        newChat.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(16);
        shape.setColor(Color.parseColor("#ffffff"));
        newChat.setPadding(20, 20, 20, 20);
        newChat.setBackground(shape);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 20, 0, 0);
        newChat.setLayoutParams(params);

        TextView receiver = new TextView(this);
        receiver.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        receiver.setText(receiverName);
        receiver.setTypeface(null, Typeface.BOLD);
        receiver.setTextSize(20);

        TextView message = new TextView(this);
        message.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        message.setText(lastMessage);
        message.setPadding(30, 10, 0, 0);

        newChat.addView(receiver);
        newChat.addView(message);

        newChat.setOnClickListener(v -> {
            Intent intent = new Intent(this, MessageActivity.class);
            intent.putExtra("senderId", senderId);
            intent.putExtra("receiverId", receiverId);
            intent.putExtra("receiverName", receiverName);
            startActivity(intent);
            finish();
        });

        linearLayout.addView(newChat);
    }



}