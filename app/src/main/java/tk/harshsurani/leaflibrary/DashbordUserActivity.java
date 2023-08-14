package tk.harshsurani.leaflibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import tk.harshsurani.leaflibrary.Adepter.AdapterCategory;
import tk.harshsurani.leaflibrary.Model.ModelCategory;
import tk.harshsurani.leaflibrary.databinding.ActivityDashbordUserBinding;

public class DashbordUserActivity extends AppCompatActivity {

    //view binding
    private @NonNull ActivityDashbordUserBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //    Arraylist to Store Category
    private ArrayList<ModelCategory> categoryArrayList;
    //adapter
    private AdapterCategory adapterCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashbordUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        loadCategories();

        //handle click, Logout
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });
    }

    private void loadCategories() {
        //        init ataylist
        categoryArrayList = new ArrayList<>();
        //get all categories from firebase > Categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                clear arraylist before adding data into it
                categoryArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    ModelCategory model = ds.getValue(ModelCategory.class);

//                    add to arraylist
                    categoryArrayList.add(model);
                }
//                setup adapter
                adapterCategory = new AdapterCategory(DashbordUserActivity.this,categoryArrayList);
//                set adepter to recyclerview
                binding.categoriesRv.setAdapter(adapterCategory);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUser() {
        //get Current User, If logged in
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null){
            //start new Activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }else {
            //logged in, get user info
            String email = firebaseUser.getEmail();
            //set in textView of toolbar
            binding.subTitleTv.setText(email);
        }
    }
}
