package com.example.finalproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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

public class RequestMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_message);
        TextView messagePage = findViewById(R.id.messagePage);
        messagePage.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
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
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 20);
        lecturer.setOrientation(LinearLayout.HORIZONTAL);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(16);
        shape.setColor(Color.parseColor("#ffffff"));
        lecturer.setPadding(20, 20, 20, 20);
        lecturer.setBackground(shape);

        TextView lecturerNameTextView = new TextView(this);
        lecturerNameTextView.setText(lecturerName);
        lecturerNameTextView.setTypeface(null, Typeface.BOLD);
        lecturerNameTextView.setTextSize(24);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                7.0f
        );
        layoutParams.setMargins(0, 0, 20, 0);
        lecturerNameTextView.setLayoutParams(layoutParams);
        lecturer.addView(lecturerNameTextView);

        Button requestBtn = new Button(this);
        requestBtn.setText("Request");
        requestBtn.setTextColor(Color.parseColor("#ffffff"));
        GradientDrawable shaped = new GradientDrawable();
        shaped.setShape(GradientDrawable.RECTANGLE);
        shaped.setCornerRadii(new float[] {64, 64, 64, 64, 64, 64, 64, 64});
        shaped.setColor(Color.parseColor("#4DB878"));
        requestBtn.setBackground(shaped);
        requestBtn.setOnClickListener(v -> {
            addRequest(lecturerId);
        });
        layoutParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                3.0f
        );
        requestBtn.setLayoutParams(layoutParams);
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
