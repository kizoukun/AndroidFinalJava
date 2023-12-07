package com.example.finalproject;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "MyAppPreferences";
    private static final String KEY_AUTH_STATUS = "isLoggedIn";
    private static final String KEY_STUDENT_ID = "studentId";
    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USER_ID = "userId";

    private final SharedPreferences preferences;

    public SharedPreferencesManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUserDetails(boolean authStatus, String userId, String studentId, String firstName, String lastName, String email) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_AUTH_STATUS, authStatus);
        editor.putString(KEY_STUDENT_ID, studentId);
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.putString(KEY_LAST_NAME, lastName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }

    public boolean getIsLoggedIn() {
        return preferences.getBoolean(KEY_AUTH_STATUS, false);
    }

    public String getStudentId() {
        return preferences.getString(KEY_STUDENT_ID, null);
    }

    public String getFirstName() {
        return preferences.getString(KEY_FIRST_NAME, null);
    }

    public String getLastName() {
        return preferences.getString(KEY_LAST_NAME, null);
    }

    public String getEmail() {
        return preferences.getString(KEY_EMAIL, null);
    }

    public String getUserId() {
        return preferences.getString(KEY_USER_ID, null);
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_AUTH_STATUS, isLoggedIn);
        editor.apply();
    }

    public void setStudentId(String studentId) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_STUDENT_ID, studentId);
        editor.apply();
    }

    public void setFirstName(String firstName) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.apply();
    }

    public void setLastName(String lastName) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_LAST_NAME, lastName);
        editor.apply();
    }

    public void setEmail(String email) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public void setUserId(String userId) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }

    public void clearUserData() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_AUTH_STATUS);
        editor.remove(KEY_STUDENT_ID);
        editor.remove(KEY_FIRST_NAME);
        editor.remove(KEY_LAST_NAME);
        editor.remove(KEY_EMAIL);
        editor.apply();
    }
}
