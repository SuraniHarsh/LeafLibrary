package tk.harshsurani.leaflibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import es.dmoral.toasty.Toasty;
import tk.harshsurani.leaflibrary.Constants;
import tk.harshsurani.leaflibrary.databinding.ActivityPdfViewBinding;

public class PdfViewActivity extends AppCompatActivity {

    private ActivityPdfViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        Intent intent = getIntent();

        String lol = intent.getStringExtra("pdfUrl");
        binding.progressBar.setVisibility(View.VISIBLE);

        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(lol);
        reference.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.pdfView.fromBytes(bytes)
                                .swipeHorizontal(false)
                                .onPageChange(new OnPageChangeListener() {
                                    @Override
                                    public void onPageChanged(int page, int pageCount) {
                                        int currentPage = (page+1);
                                        binding.toolbarSubtitleTV.setText(currentPage +"/"+pageCount);
                                    }
                                })
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {

                                    }
                                }).onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {

                                    }
                                }).load();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toasty.error(getApplicationContext(),""+e.getMessage(),Toasty.LENGTH_SHORT,true).show();
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