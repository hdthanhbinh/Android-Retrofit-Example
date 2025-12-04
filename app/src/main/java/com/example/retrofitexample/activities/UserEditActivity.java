package com.example.retrofitexample.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.retrofitexample.R;
import com.example.retrofitexample.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserEditActivity extends AppCompatActivity {
    User user;
    EditText name;
    EditText email;
    EditText phone;
    // show id but not editable
    TextView userIdText;

    Button edit, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        // bật nút back (X) trên thanh trên cùng
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }

        initialize();
        onClickListeners();
    }

    @Override
    public boolean onSupportNavigateUp() {
        // xử lý khi bấm nút X: quay lại màn hình trước
        finish();
        return true;
    }

    private void onClickListeners() {
        edit = findViewById(R.id.edit);
        delete = findViewById(R.id.delete);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = name.getText().toString();
                String newEmail = email.getText().toString();
                String newPhone = phone.getText().toString();

                // update all editable fields on user before calling API
                user.setName(newName);
                user.setEmail(newEmail);
                try {
                    user.setPhone(Integer.parseInt(newPhone));
                } catch (NumberFormatException e) {
                    // keep old phone if parsing fails
                }

                Call<User> userCall = MainActivity.api.setUserById(user.getId(), user);
                userCall.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                        MainActivity.listViewAdapter.notifyDataSetChanged();
                        UserEditActivity.this.finish();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Update failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<User> call = MainActivity.api.deleteUserById(user.getId());
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                        MainActivity.listViewAdapter.notifyDataSetChanged();
                        UserEditActivity.this.finish();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Delete failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    private void initialize() {
        name = findViewById(R.id.user_name_edit);
        email = findViewById(R.id.email_edit);
        phone = findViewById(R.id.phone_edit);
        userIdText = findViewById(R.id.user_id_text);

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        if (bundle != null) {
            Object obj = bundle.get("user");
            if (obj instanceof User) {
                user = (User) obj;
            }
        }

        if (user != null) {
            name.setText(user.getName());
            email.setText(user.getEmail());
            phone.setText(String.valueOf(user.getPhone()));
            if (userIdText != null) {
                userIdText.setText(String.valueOf(user.getId()));
            }
        }
    }
}
