package com.example.retrofitexample.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.util.Log;
import android.widget.Toast;

import com.example.retrofitexample.R;
import com.example.retrofitexample.adapters.CustomListViewAdapter;
import com.example.retrofitexample.model.User;
import com.example.retrofitexample.network.RetrofitClientInstance;
import com.example.retrofitexample.services.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    public static ApiInterface api;
    private ArrayList<User> users;
    private ListView listView;
    private View emptyView;
    private View progressBar;
    public static CustomListViewAdapter listViewAdapter;

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("MainActivity", "onCreate");
        // find empty/progress views early
        emptyView = findViewById(R.id.empty_view);
        progressBar = findViewById(R.id.progress_bar);

        initialize();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity", "onResume");
        fillArrayList();
    }

    private void initialize() {
        users = new ArrayList<>();
        btn = findViewById(R.id.add_random_user);
        listView = findViewById(R.id.user_list_view);
        listViewAdapter = new CustomListViewAdapter(MainActivity.this, users);
        listView.setAdapter(listViewAdapter);
        // connect empty view so ListView shows it when adapter is empty
        if (emptyView != null) {
            listView.setEmptyView(emptyView);
        }
        // initialize API client early so button clicks don't encounter a null `api`
        api = RetrofitClientInstance.getClient().create(ApiInterface.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                User user = users.get(position);
                Intent i = new Intent(MainActivity.this, UserEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fillArrayList() {
        listViewAdapter.clear();
        // ensure the backing list is cleared to avoid duplicates
        users.clear();
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        Call<List<User>> listCall = api.getAllUsers();

        listCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response != null && response.isSuccessful() && response.body() != null) {
                    List<User> userList = response.body();
                    Log.i("MainActivity", "getAllUsers: received=" + userList.size());
                    if (userList.isEmpty()) {
                        Toast.makeText(MainActivity.this, "No users found", Toast.LENGTH_SHORT).show();
                    }
                    for (User u : userList) {
                        users.add(u);
                        Log.d("MainActivity", "user: " + u.toString());
                    }
                    listViewAdapter.notifyDataSetChanged();
                } else {
                    Log.w("MainActivity", "getAllUsers unsuccessful or empty response: code=" + (response != null ? response.code() : "null"));
                    Toast.makeText(MainActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Log.e("MainActivity", "getAllUsers failure", t);
                Toast.makeText(MainActivity.this, "Failed to load users: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}
