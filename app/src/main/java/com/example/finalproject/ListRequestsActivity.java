package com.example.finalproject;

import android.os.Bundle;
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
import java.util.Objects;

public class ListRequestsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_requests);
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
        TextView studentNameTextView = new TextView(this);
        TextView statusTextView = new TextView(this);
        studentNameTextView.setText(studentName);
        statusTextView.setText(status);
        request.addView(studentNameTextView);
        request.addView(statusTextView);
        LinearLayout acceptReject = new LinearLayout(this);
        acceptReject.setOrientation(LinearLayout.HORIZONTAL);
        Button acceptBtn = new Button(this);
        acceptBtn.setText("Accept");
        Button rejectBtn = new Button(this);
        rejectBtn.setText("Reject");
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
