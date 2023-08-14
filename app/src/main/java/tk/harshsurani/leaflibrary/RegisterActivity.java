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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import tk.harshsurani.leaflibrary.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    ProgressDialog progressDoalog;
    //firebase auth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        progressDoalog = new ProgressDialog(this); // progrssDialog bar
        progressDoalog.setTitle("Please Wait...");
        progressDoalog.setCanceledOnTouchOutside(false);
        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //handle click go back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });

        //handle click, begin register
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.nameEt.getText().toString().trim();
                String email = binding.emailEt.getText().toString().trim();
                String password = binding.passwordEt.getText().toString().trim();
                String cPassword = binding.cPasswordEt.getText().toString().trim();
                if (name.isEmpty() == false){
                    if (email.isEmpty() == false){
                        if (password.isEmpty() == false){
                            if (isValid(password)){
                                if (password.compareTo(cPassword)==0){
                                    progressDoalog.setMessage("Creating a user Account.");
                                    progressDoalog.show();
                                    firebaseAuth.createUserWithEmailAndPassword(email,password) // Create user in Firebase Auth
                                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                @Override
                                                public void onSuccess(AuthResult authResult) {
                                                    //Account Creation Success
                                                    long timestamp = System.currentTimeMillis(); // timestamp
                                                    String uid = firebaseAuth.getUid(); //get current user uid

                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                    hashMap.put("UID",uid);
                                                    hashMap.put("Email",email);
                                                    hashMap.put("Name",name);
                                                    hashMap.put("ProfileImage","");
                                                    hashMap.put("UserType","user");
                                                    hashMap.put("Timestamp",timestamp);

                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
                                                    ref.child(uid)
                                                            .setValue(hashMap)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    progressDoalog.dismiss();
                                                                    Toasty.success(getApplicationContext(), "Account Created...", Toast.LENGTH_SHORT, true).show();
                                                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    progressDoalog.dismiss();
                                                                    Toasty.error(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT, true).show();
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    //Account Creation Failed
                                                    progressDoalog.dismiss();
                                                    Toasty.error(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT, true).show();
                                                }
                                            });
                                    binding.nameEt.setText("");
                                    binding.emailEt.setText("");
                                    binding.passwordEt.setText("");
                                    binding.cPasswordEt.setText("");
                                }else {
                                    Toasty.info(getApplicationContext(), "Password does not match...!", Toast.LENGTH_SHORT, true).show();
                                }
                            }else {
                                Toasty.info(getApplicationContext(), "Invalid Password...!", Toast.LENGTH_SHORT, true).show();
                            }
                        }else {
                            Toasty.info(getApplicationContext(), "Enter your Password...!", Toast.LENGTH_SHORT, true).show();
                        }
                    }else {
                        Toasty.info(getApplicationContext(), "Enter Your Email...!", Toast.LENGTH_SHORT, true).show();
                    }
                }else {
                    Toasty.info(getApplicationContext(), "Enter your Name...!", Toast.LENGTH_SHORT, true).show();
                }
            }
        });

    }
    public static boolean isValid(String Passwordhere) {
        int f1=0,f2=0,f3=0;
        if (Passwordhere.length()<8) {
            return false;
        } else {
            for(int p = 0; p < Passwordhere.length(); p++){
                if(Character.isLetter(Passwordhere.charAt(p))){
                    f1=1;
                }
            }
            for(int r = 0; r < Passwordhere.length(); r++){
                if(Character.isDigit(Passwordhere.charAt(r))){
                    f2=1;
                }
            }
            for(int s = 0; s < Passwordhere.length(); s++){
                char c = Passwordhere.charAt(s);
                if(c>=33&&c<=46||c==64){
                    f3=1;
                }
            }
            if(f1==1&&f2==1&&f3==1)
                return true;
            return false;
        }
    }
}
