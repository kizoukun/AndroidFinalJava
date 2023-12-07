package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button redirectToLogin = findViewById(R.id.redirectToLogin);
        redirectToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        Button register = findViewById(R.id.registerBtn);
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);
        EditText firstNameEditText = findViewById(R.id.firstName);
        EditText lastNameEditText = findViewById(R.id.lastName);
        EditText studentIdEditText = findViewById(R.id.studentId);
        register.setOnClickListener(view -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String studentId = studentIdEditText.getText().toString();
            if (email.isEmpty()) {
                emailEditText.setError("Email is required");
                return;
            }
            if (password.isEmpty()) {
                passwordEditText.setError("Password is required");
                return;
            }
            if (firstName.isEmpty()) {
                firstNameEditText.setError("First name is required");
                return;
            }
            if (lastName.isEmpty()) {
                lastNameEditText.setError("Last name is required");
                return;
            }
            if (studentId.isEmpty()) {
                studentIdEditText.setError("Student ID is required");
                return;
            }
            String url = MainActivity.BASE_URL + "/auth/register.php";
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
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("password", password);
                    params.put("first_name", firstName);
                    params.put("last_name", lastName);
                    params.put("student_id", studentId);
                    return params;
                }
            };
            queue.add(stringRequest);
        });
    }
}