package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button redirectToRegister = findViewById(R.id.redirectToRegister);
        redirectToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
        Button login = findViewById(R.id.loginBtn);
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);
        login.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if (email.isEmpty()) {
                Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
                return;
            }
            String url = MainActivity.BASE_URL + "/auth/login.php";
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean isSuccess = jsonObject.getBoolean("success");
                    String message = jsonObject.getString("message");
                    if(!isSuccess) {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONObject data = jsonObject.getJSONObject("data");
                    SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());
                    sharedPreferencesManager.setIsLoggedIn(true);
                    sharedPreferencesManager.setEmail(data.getString("email"));
                    sharedPreferencesManager.setStudentId(data.getString("student_id"));
                    sharedPreferencesManager.setFirstName(data.getString("first_name"));
                    sharedPreferencesManager.setLastName(data.getString("last_name"));
                    sharedPreferencesManager.setUserId(data.getString("id"));
                    sharedPreferencesManager.setUserRole(data.getString("roles"));
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }, error -> {
                Log.e("error", Objects.requireNonNull(error.getLocalizedMessage()));
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> params = new java.util.HashMap<>();
                    params.put("email", email);
                    params.put("password", password);
                    return params;
                }
            };

            queue.add(stringRequest);

        });

    }
}