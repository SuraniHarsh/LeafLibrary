package tk.harshsurani.leaflibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import tk.harshsurani.leaflibrary.Adepter.AdapterPdfAdmin;
import tk.harshsurani.leaflibrary.Model.ModelPdf;
import tk.harshsurani.leaflibrary.databinding.ActivityPdfListAdminBinding;

public class PdfListAdminActivity extends AppCompatActivity {

    //    view binding
    private ActivityPdfListAdminBinding binding;
//    arraylist to hold list of data of type ModelPdf
    private ArrayList<ModelPdf> pdfArrayList;
//    adapter
    private AdapterPdfAdmin adapterPdfAdmin;

    String categoryId, categoryTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfListAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");
        categoryTitle = intent.getStringExtra("categoryTitle");

//        init list before adding data
        pdfArrayList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
        reference.orderByChild("categoryId").equalTo(categoryId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot != null){
                                    pdfArrayList.clear();
                                    for (DataSnapshot ds: snapshot.getChildren()){
                                        ModelPdf model = new ModelPdf();
                                        model.setCategoryId(ds.child("categoryId").getValue(String.class));
                                        model.setDescription(ds.child("description").getValue(String.class));
                                        model.setId(ds.child("id").getValue(long.class));
                                        model.setTimestamp(ds.child("timestamp").getValue(long.class));
                                        model.setTitle(ds.child("title").getValue(String.class));
                                        model.setUid(ds.child("uid").getValue(String.class));
                                        model.setUrl(ds.child("url").getValue(String.class));
                                        pdfArrayList.add(model);
                                    }
                                    adapterPdfAdmin = new AdapterPdfAdmin(PdfListAdminActivity.this,pdfArrayList);
                                    binding.bookRv.setAdapter(adapterPdfAdmin);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

}