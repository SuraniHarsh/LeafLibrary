package tk.harshsurani.leaflibrary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import tk.harshsurani.leaflibrary.databinding.ActivityPdfAddBinding;

public class PdfAddActivity extends AppCompatActivity {

    private ActivityPdfAddBinding binding;

//    Firebase auth
    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDoalog;
    //arraylist to hold pdf categories
    private ArrayList<String> categoryTitleArrayList, categoryIdArrayList;

    private Uri pdfUri;

private static  final int PDF_PICK_CODE = 1;
//  TAG for debugging
private static final String TAG = "ADD_PDF_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        String a;
//        init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        loadPdfCategories();
        progressDoalog = new ProgressDialog(this); // progrssDialog bar
        progressDoalog.setTitle("Please Wait...");
        progressDoalog.setCanceledOnTouchOutside(false);

//        handle click, go to previous Activity
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });

//        handel click, attach pdf
        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "pdfPickIntent: starting pdf pick intent");

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(Intent.createChooser(intent,"Select PDF"),1);

            }
        });

//    handle click pick category

        binding.categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               categoryPickDialog();
            }
        });

        //handle click, upload pdf
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title,description;
                title = binding.titleEt.getText().toString().trim();
                description = binding.descriptionEt.getText().toString().trim();

                if (title.isEmpty()==false){
                    if (description.isEmpty() == false){
                        if (selectedCategoryTitle.isEmpty() == false){
                            if (pdfUri != null){
                                progressDoalog.setMessage("Uploading PDF..");
                                progressDoalog.show();
                                Log.d(TAG, "uploadPdfToStorage: uploading to storage");
                                long timestamp = System.currentTimeMillis();
//                                path of pdf in firebase Storage
                                String filePathAndName = "Books/"+timestamp;
//                                Storage reference
                                StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
                                storageReference.putFile(pdfUri)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                Log.d(TAG, "onSuccess: PDF uploaded to Storage...");
                                                Log.d(TAG, "onSuccess: getting pdf url");

                                                //get pdf url
                                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                                while (!uriTask.isSuccessful());
                                                String uploadedPdfUrl = ""+uriTask.getResult();

                                                //upload to firebase database
                                                Log.d(TAG, "uploadPdfToStorage: uploading Pdf Info to Firebase Database");
                                                String uid = firebaseAuth.getUid();
//                                                Setup data to upload
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("uid", uid);
                                                hashMap.put("id", timestamp);
                                                hashMap.put("title", title);
                                                hashMap.put("description", description);
                                                hashMap.put("categoryId", selectedCategoryId);
                                                hashMap.put("url", uploadedPdfUrl);
                                                hashMap.put("timestamp",timestamp);

                                                //db ref DB > Books
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
                                                ref.child(""+timestamp)
                                                        .setValue(hashMap)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                progressDoalog.dismiss();
                                                                Log.d(TAG,"onSuccess: Successfully uploaded...");
                                                                Toasty.success(getApplicationContext(),"Successfully uploaded...",Toasty.LENGTH_SHORT,true).show();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                progressDoalog.dismiss();
                                                                Log.d(TAG, "onFailure: Faile to upload to db due to"+e.getMessage());
                                                                Toasty.error(getApplicationContext(),""+e.getMessage(),Toasty.LENGTH_SHORT,true).show();
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDoalog.dismiss();
                                                Log.d(TAG, "onFailure: PDF upload failed due to "+e.getMessage());
                                                Toasty.error(getApplicationContext(),""+e.getMessage(),Toasty.LENGTH_SHORT,true).show();
                                            }
                                        });
                                binding.titleEt.setText("");
                                binding.descriptionEt.setText("");
                                binding.categoryTv.setText("");
                                pdfUri = null;
                            }else {
                                Toasty.info(getApplicationContext(),"Pick PDF...",Toasty.LENGTH_SHORT,true).show();
                            }
                        }else {
                            Toasty.info(getApplicationContext(),"Select Category...",Toasty.LENGTH_SHORT,true).show();
                        }
                    }else {
                        Toasty.info(getApplicationContext(),"Enter Description...",Toasty.LENGTH_SHORT,true).show();
                    }
                }else {
                    Toasty.info(getApplicationContext(),"Enter Title...",Toasty.LENGTH_SHORT,true).show();
                }
            }
        });

    }

    private String selectedCategoryTitle, selectedCategoryId;
    private void categoryPickDialog() {
        Log.d(TAG, "categoryPickDialog: Showing Category pick dialog");

//                get String array of categories from arraylist
        String[] categoriesArray = new String[categoryTitleArrayList.size()];
        for (int i = 0; i< categoryTitleArrayList.size(); i++){
            categoriesArray[i] = categoryTitleArrayList.get(i);
        }

//                alart dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(PdfAddActivity.this);
        builder.setTitle("Pick Category")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get clicked item from list
                        selectedCategoryTitle = categoryTitleArrayList.get(which);
                        selectedCategoryId = categoryIdArrayList.get(which);
                        //set to category textview
                        binding.categoryTv.setText(selectedCategoryTitle);

                        Log.d(TAG, "onClick: Selected Category:"+ selectedCategoryId+" "+selectedCategoryTitle);
                    }
                }).show();
    }

    private void loadPdfCategories() {
        Log.d(TAG, "loadPdfCategories: Loading pdf categories...");
        categoryTitleArrayList = new ArrayList<>();
        categoryIdArrayList = new ArrayList<>();
//        db ref to load categories db > Categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear();
                categoryIdArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    String categoryId = ds.child("id").getValue().toString();
                    String categoryTitle = ds.child("category").getValue().toString();

                    categoryTitleArrayList.add(categoryTitle);
                    categoryIdArrayList.add(categoryId);
                }
            }
 
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PDF_PICK_CODE && resultCode == RESULT_OK && data != null){
                Log.d(TAG, "onActiviResult: PDF Picked");

                pdfUri = data.getData();
                Log.d(TAG, "onActivityResult: URI: "+pdfUri);
        }else {
            Log.d(TAG, "onActivityResult: canceled picking pdf");
            Toasty.info(getApplicationContext(),"canceled picking pdf", Toasty.LENGTH_SHORT, true).show();
        }
    }
}