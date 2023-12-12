package com.example.finalproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

public class ListRequestsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_requests);
        TextView messagePage = findViewById(R.id.messagePage);
        messagePage.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        getRequests();
    }

    public void getRequests() {
        String url = MainActivity.BASE_URL + "/requests/list.php?lecturer_id=" + new SharedPreferencesManager(getApplicationContext()).getUserId();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        clearRequests();
        StringRequest stringRequest = new StringRequest(url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                boolean isSuccess = jsonObject.getBoolean("success");
                String message = jsonObject.getString("message");
                if(!isSuccess) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONArray data = jsonObject.getJSONArray("data");

                for(int i = 0; i < data.length(); i++) {
                    JSONObject request = data.getJSONObject(i);
                    String requestId = request.getString("id");
                    String studentName = request.getString("student_first_name") + " " + request.getString("student_last_name");
                    String studentId = request.getString("user_id");
                    String status = request.getString("status");
                    addRequests(requestId, studentName, studentId, status);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(this, "Failed to get request list", Toast.LENGTH_SHORT).show());
        queue.add(stringRequest);
    }

    public void addRequests(String requestId, String studentName, String studentId, String status) {
        LinearLayout requestList = findViewById(R.id.requests);
        LinearLayout request = new LinearLayout(this);
        request.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(16);
        shape.setColor(Color.parseColor("#ffffff"));
        request.setPadding(20, 20, 20, 20);
        request.setBackground(shape);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 20, 0, 0);
        request.setLayoutParams(params);

        TextView studentNameTextView = new TextView(this);
        TextView statusTextView = new TextView(this);
        studentNameTextView.setText(studentName);
        studentNameTextView.setTypeface(null, Typeface.BOLD);
        statusTextView.setText(status);
        request.addView(studentNameTextView);
        request.addView(statusTextView);
        LinearLayout acceptReject = new LinearLayout(this);
        acceptReject.setOrientation(LinearLayout.HORIZONTAL);

        Button acceptBtn = new Button(this);
        acceptBtn.setText("Accept");
        acceptBtn.setTextColor(Color.parseColor("#ffffff"));
        GradientDrawable shaped = new GradientDrawable();
        shaped.setShape(GradientDrawable.RECTANGLE);
        shaped.setCornerRadius(64);
        shaped.setColor(Color.parseColor("#4DB878"));
        acceptBtn.setBackground(shaped);

        LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(0, 0, 30, 0 );
        acceptBtn.setLayoutParams(layoutParams);



        Button rejectBtn = new Button(this);
        rejectBtn.setText("Reject");
        rejectBtn.setTextColor(Color.parseColor("#ffffff"));
        GradientDrawable shapee = new GradientDrawable();
        shapee.setShape(GradientDrawable.RECTANGLE);
        shapee.setCornerRadius(64);
        shapee.setColor(Color.parseColor("#B84D4D")); // Set the same color as the background
        rejectBtn.setBackground(shapee);



        acceptBtn.setOnClickListener(v -> {
            changeStatus(requestId, "ACCEPTED", studentId);
        });
        rejectBtn.setOnClickListener(v -> {
            changeStatus(requestId, "REJECTED", studentId);
        });
        acceptReject.addView(acceptBtn);
        acceptReject.addView(rejectBtn);
        request.addView(acceptReject);
        requestList.addView(request);
    }

    public void changeStatus(String requestId, String status, String receiverId) {
        String url = MainActivity.BASE_URL + "/requests/update.php";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                boolean isSuccess = jsonObject.getBoolean("success");
                String message = jsonObject.getString("message");
                if(!isSuccess) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                if(status.equalsIgnoreCase("accepted")) {
                    addMessageToDatabase("Hi, do you need help with anything?", new SharedPreferencesManager(getApplicationContext()).getUserId(), receiverId);
                }
                getRequests();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(this, "Failed to update request status", Toast.LENGTH_SHORT).show()) {
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new java.util.HashMap<>();
                params.put("id", requestId);
                params.put("status", status.toUpperCase());
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void clearRequests() {
        LinearLayout requestList = findViewById(R.id.requests);
        requestList.removeAllViews();
    }

    public void addMessageToDatabase(String messageText, String senderId, String receiverId) {
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
