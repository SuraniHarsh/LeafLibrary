package tk.harshsurani.leaflibrary.Adepter;

import static tk.harshsurani.leaflibrary.Constants.MAX_BYTES_PDF;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import tk.harshsurani.leaflibrary.Model.ModelPdf;
import tk.harshsurani.leaflibrary.PdfViewActivity;
import tk.harshsurani.leaflibrary.databinding.RowPdfAdminBinding;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> {

//    context
    private Context context;
//    arraylist to hold lise of data of type Model
    private ArrayList<ModelPdf> pdfArrayList;

//    View binding row_pdf_admin.xml
    private RowPdfAdminBinding binding;

//    constructor
    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Bind layout using view binding
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderPdfAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfAdmin holder, int position) {
//        Get data, set Data, handle click etc.

//        get data
        ModelPdf model = pdfArrayList.get(position);
        String title = model.getTitle();
        String description = model.getDescription();

//        Set data
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);

//        Load further details like category pdf from url pdf size functions
        loadCategory(model, holder);
        loadPdfFromUrl(model, holder);
        loadPdfSize(model, holder);
    }

    private void loadPdfSize(ModelPdf model, HolderPdfAdmin holder) {
//        using url we can get file and it's metadata from firebase storage

        String pdfUrl = model.getUrl();
        long lol = model.getId();

        System.out.println("this is your id: "+lol);

        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        reference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
//                get size in bytes
                double bytes = storageMetadata.getSizeBytes();

//                convert bytes to KB,MB
                double kb = bytes/1024;
                double mb = kb/1024;

                if (mb>=1){
                    holder.sizeTv.setText(String.format("%.2f", mb)+" MB");
                }else if (kb >= 1){
                    holder.sizeTv.setText(String.format("%.2f", kb)+" KB");
                }else {
                    holder.sizeTv.setText(String.format("%.2f", bytes)+" bytes");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void loadPdfFromUrl(ModelPdf model, HolderPdfAdmin holder) {
        String pdfUrl = model.getUrl();
        System.out.println(pdfUrl);
        long id = model.getId();

        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        reference.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {

//                        set to pdfview
                        holder.pdfView.fromBytes(bytes)
                                .pages(0)
                                .spacing(0)
                                .swipeHorizontal(false)
                                .enableSwipe(false)
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        System.out.println("somethig goss wrong in pdf view : "+t.getMessage());
                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        System.out.println("it's page problam : "+t.getMessage());
                                    }
                                })
                                .load();
                        holder.progressBar.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Problem pdf load nahi horahi : "+e.getMessage());
                    }
                });
    }

    private void loadCategory(ModelPdf model, HolderPdfAdmin holder) {
//        get category using categoryId
        String categoryId = model.getCategoryId();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
        reference.child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        get category
                        String category = snapshot.child("category").getValue(String.class);

//                        set to category text view
                        holder.categoryTv.setText(category);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PdfViewActivity.class);
                String pdfUrl = model.getUrl();
                intent.putExtra("pdfUrl", pdfUrl);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size(); //return number of records | lise size
    }

    //    view Holder class for row_pdf_admin.xml
    class HolderPdfAdmin extends RecyclerView.ViewHolder{

//        UI views of row_pdf_admin.xml
        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTv, descriptionTv,categoryTv, sizeTv;
        ImageButton moreBtn;

        public HolderPdfAdmin(@NonNull View itemView) {
            super(itemView);

//            init ui views
            pdfView = binding.pdfView;
            progressBar = binding.progressBar;
            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTv;
            categoryTv = binding.categoryTv;
            sizeTv = binding.sizeTv;
            moreBtn = binding.moreBtn;

        }
    }
}
