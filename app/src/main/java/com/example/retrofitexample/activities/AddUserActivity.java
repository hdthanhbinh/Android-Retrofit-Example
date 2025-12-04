package com.example.retrofitexample.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.retrofitexample.R;
import com.example.retrofitexample.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUserActivity extends AppCompatActivity {

    private EditText nameEdit;
    private EditText phoneEdit;
    private EditText emailEdit;
    private Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
            getSupportActionBar().setTitle("Add user");
        }

        nameEdit = findViewById(R.id.add_name);
        phoneEdit = findViewById(R.id.add_phone);
        emailEdit = findViewById(R.id.add_email);
        saveBtn = findViewById(R.id.add_save);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEdit.getText().toString().trim();
                String phoneStr = phoneEdit.getText().toString().trim();
                String email = emailEdit.getText().toString().trim();

                if (name.isEmpty()) {
                    nameEdit.setError("Enter name");
                    return;
                }
                Integer phone = null;
                if (!phoneStr.isEmpty()) {
                    try {
                        phone = Integer.parseInt(phoneStr);
                    } catch (NumberFormatException e) {
                        phoneEdit.setError("Phone must be number");
                        return;
                    }
                }

                // create a User object; id and createdAt/avatar will be handled by server or ignored
                User newUser = new User(null, null, name, null, email, phone);

                Call<User> call = MainActivity.api.addUser(newUser);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(AddUserActivity.this, "User added", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddUserActivity.this, "Add failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(AddUserActivity.this, "Add failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

