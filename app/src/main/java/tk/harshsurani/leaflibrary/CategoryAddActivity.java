package tk.harshsurani.leaflibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import tk.harshsurani.leaflibrary.databinding.ActivityCategoryAddBinding;

public class CategoryAddActivity extends AppCompatActivity {

    //view binding
    private ActivityCategoryAddBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progress Dialog
    ProgressDialog progressDoalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //Configure Progress Dialog
        progressDoalog = new ProgressDialog(this); // progrssDialog bar
        progressDoalog.setTitle("Please Wait...");
        progressDoalog.setCanceledOnTouchOutside(false);

        //handle click, begin upload category
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = binding.categoryEt.getText().toString().trim();
                if (category.isEmpty() == false){
                    progressDoalog.setMessage("Adding Category");
                    progressDoalog.show();

                    //get timestamp
                    long timestamp = System.currentTimeMillis();

                    //setup info to add in Firebase Database
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id",""+timestamp);
                    hashMap.put("category",""+category);
                    hashMap.put("timeStamp",timestamp);
                    hashMap.put("uid", ""+firebaseAuth.getUid());
                    System.out.println(hashMap);

                    //add to firebase database.....  Database Root > Categories > categoryID > Category info
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/Categories");
                    ref.child(""+timestamp)
                            .setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDoalog.dismiss();
                                    Toasty.success(getApplicationContext(),"Category Added",Toasty.LENGTH_SHORT,true).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDoalog.dismiss();
                                    Toasty.error(getApplicationContext(),""+e.getMessage(),Toasty.LENGTH_SHORT,true).show();
                                }
                            });
                    binding.categoryEt.setText("");

                }else {
                    Toasty.info(getApplicationContext(),"Please enter category...!", Toasty.LENGTH_SHORT,true).show();
                }
            }
        });
    }
}