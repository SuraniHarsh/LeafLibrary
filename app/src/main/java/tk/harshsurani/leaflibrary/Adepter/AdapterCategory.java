package tk.harshsurani.leaflibrary.Adepter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import tk.harshsurani.leaflibrary.Model.ModelCategory;
import tk.harshsurani.leaflibrary.PdfListAdminActivity;
import tk.harshsurani.leaflibrary.databinding.RowCategoryBinding;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.HolderCategory> {

    private Context context;
    private ArrayList<ModelCategory> categoryArrayList;

//    view binding
    private RowCategoryBinding binding;

    public AdapterCategory(Context context, ArrayList<ModelCategory> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
    }

    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind row_category.xml
        binding = RowCategoryBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HolderCategory(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, int position) {
//        get data
        ModelCategory model = categoryArrayList.get(position);
        String id = model.getId();
        String category = model.getCategory();
        String uid = model.getUid();
        long timestamp = model.getTimeStamp();

        //handle click, delete category
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//               Confirm delete dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you Sure you want to delete this Category?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                begin delete
                                    //get Id of Category to delete
                                String id = model.getId();
                                    //Firebase Db > Categories > CategotyID
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
                                ref.child(id)
                                        .removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toasty.success(context,"SuccessFully Deleted...",Toasty.LENGTH_SHORT,true).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toasty.error(context,""+e.getMessage(),Toasty.LENGTH_SHORT,true).show();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PdfListAdminActivity.class);
                intent.putExtra("categoryId",id);
                intent.putExtra("categoryTitle",category);
                context.startActivity(intent);
            }
        });
        //        set data
        holder.categoryTv.setText(category);

    }


    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    //    view holder class to hold UI views for row_category.xml
    public class HolderCategory extends RecyclerView.ViewHolder{

        //ui views of row_category.xml
        TextView categoryTv;
        ImageButton deleteBtn;

    public HolderCategory(@NonNull View itemView) {
        super(itemView);
//        init ui views
        categoryTv = binding.cateforyTv;
        deleteBtn = binding.deleteBtn;
    }
}
}
