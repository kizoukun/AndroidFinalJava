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

public class RequestMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_message);
        getLecturers();
    }

    public void getLecturers() {
        String url = MainActivity.BASE_URL + "/get-lecturer.php";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
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
                    JSONObject lecturer = data.getJSONObject(i);
                    System.out.println(lecturer);
                    String lecturerId = lecturer.getString("id");
                    String lecturerName = lecturer.getString("first_name") + " " + lecturer.getString("last_name");
                    addLecturer(lecturerId, lecturerName);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(this, "Failed to get lecturer list", Toast.LENGTH_SHORT).show());
        queue.add(stringRequest);
    }

    public void addLecturer(String lecturerId, String lecturerName) {
        LinearLayout lecturerList = findViewById(R.id.lecturers);
        LinearLayout lecturer = new LinearLayout(this);
        lecturer.setOrientation(LinearLayout.HORIZONTAL);

        TextView lecturerNameTextView = new TextView(this);
        lecturerNameTextView.setText(lecturerName);
        lecturer.addView(lecturerNameTextView);

        Button requestBtn = new Button(this);
        requestBtn.setText("Request");
        requestBtn.setOnClickListener(v -> {
            addRequest(lecturerId);
        });
        lecturer.addView(requestBtn);

        lecturerList.addView(lecturer);
    }

    public void addRequest(String lecturerId) {
        String url = MainActivity.BASE_URL + "/requests/create.php";
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(this, "Failed to get lecturer list", Toast.LENGTH_SHORT).show()) {
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new java.util.HashMap<>();
                params.put("user_id", new SharedPreferencesManager(getApplicationContext()).getUserId());
                params.put("lecturer_id", lecturerId);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}
