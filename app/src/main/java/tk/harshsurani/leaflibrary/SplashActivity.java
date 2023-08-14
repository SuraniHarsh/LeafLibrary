package tk.harshsurani.leaflibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class SplashActivity extends AppCompatActivity {
    //  Firebase Auth
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        firebaseAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUser();
            }

        },2000); //2 seconds

    }

    private void checkUser() {
        //get Current User, If logged in
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null){
            //start new Activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }else {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/User");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String userType = dataSnapshot.child(firebaseUser.getUid()).child("UserType").getValue().toString();

                    if (userType.equals("user")){
                        startActivity(new Intent(getApplicationContext(),DashbordUserActivity.class));
                        finish();
                    }else if (userType.equals("admin")){
                        startActivity(new Intent(getApplicationContext(), DashbordAdminActivity.class));
                        finish();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toasty.error(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT, true).show();
                }
            });
        }
    }
}