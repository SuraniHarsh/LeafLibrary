package tk.harshsurani.leaflibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;
import tk.harshsurani.leaflibrary.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDoalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        progressDoalog = new ProgressDialog(this); // progrssDialog bar
        progressDoalog.setTitle("Please Wait...");
        progressDoalog.setCanceledOnTouchOutside(false);
        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailEt.getText().toString().trim();
                String password = binding.passwordEt.getText().toString().trim();
                if (email.isEmpty() == false){
                    if (password.isEmpty() == false){
                        progressDoalog.setMessage("Log In...");
                        progressDoalog.show();
                        firebaseAuth.signInWithEmailAndPassword(email,password)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                        //Check Use in Firebase Database
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/User");
                                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                String userType = dataSnapshot.child(firebaseUser.getUid()).child("UserType").getValue().toString();

                                                if (userType.equals("user")){
                                                    progressDoalog.dismiss();
                                                    System.out.println(userType);
                                                    startActivity(new Intent(getApplicationContext(),DashbordUserActivity.class));
                                                    finish();
                                                }else{
                                                    progressDoalog.dismiss();
                                                    startActivity(new Intent(getApplicationContext(), DashbordAdminActivity.class));
                                                    finish();
                                                }
                                                binding.passwordEt.setText("");
                                                binding.emailEt.setText("");
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toasty.error(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT, true).show();
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDoalog.dismiss();
                                        Toasty.error(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT, true).show();
                                    }
                                });
                    }else {
                        Toasty.info(getApplicationContext(), "Enter Your Password...!", Toast.LENGTH_SHORT, true).show();
                    }
                }else {
                    Toasty.info(getApplicationContext(), "Enter your Email...!", Toast.LENGTH_SHORT, true).show();
                }
            }
        });

        //handle click, go to register screen
        binding.noAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

    }
}