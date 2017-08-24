package com.example.maola.degradotourmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.maola.degradotourmap.User.NewUserActivity;
import com.example.maola.degradotourmap.User.UserPageActivity;
import com.example.maola.degradotourmap.User.UserSettingsPageActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private Button button;
    private EditText mEdtTest;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent i = new Intent(getBaseContext(), MapsActivity.class);
                    startActivity(i);
                    return true;
                case R.id.navigation_dashboard:
                    Intent loginIntent = new Intent(getBaseContext(), UserPageActivity.class);
                    startActivity(loginIntent);
                    return true;
                case R.id.navigation_notifications:
                    Intent signUpIntent = new Intent(getBaseContext(), NewUserActivity.class);
                    startActivity(signUpIntent);

            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Write a message to the database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("utenti");

//        myRef.setValue("Hello, World!");

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String messegeId = myRef.push().getKey();
//                User user = new User("mao","meox92@gmail.com","prato","via de andrè", "italy","reports", 0);
//                myRef.child(messegeId).setValue(user);
//            }
//        });

    }

}
