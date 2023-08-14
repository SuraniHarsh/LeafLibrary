package tk.harshsurani.leaflibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button LoginBtn,SkipBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        LoginBtn = findViewById(R.id.loginBtn);
        SkipBtn = findViewById(R.id.skipBtn);

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //For Login Btn
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        SkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //For Skip Btn
                startActivity(new Intent(getApplicationContext(), DashbordUserActivity.class));
            }
        });
    }
}