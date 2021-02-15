package org.smartregister.giz.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.collection.LruCache;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.giz.R;
import org.smartregister.giz.fragment.EligibleChildrenReportFragment;
import org.smartregister.giz.fragment.FilterReportFragment;
import org.smartregister.giz.fragment.VillageDoseReportFragment;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.util.PermissionUtils;
import org.smartregister.view.activity.SecuredActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import timber.log.Timber;

public class FragmentBaseActivity extends SecuredActivity {
    protected static final String DISPLAY_FRAGMENT = "DISPLAY_FRAGMENT";
    protected static final String TITLE = "TITLE";
    public ImageView emailIcon;
    protected String INDICATOR_CODE = "INDICATOR_CODE";
    String emailSubject = "";
    private TextView titleTextView;

    public static void startMe(Activity activity, String fragmentName, String title) {
        Intent intent = new Intent(activity, FragmentBaseActivity.class);
        intent.putExtra(DISPLAY_FRAGMENT, fragmentName);
        intent.putExtra(TITLE, title);
        activity.startActivity(intent);
    }

    public static void startMe(Activity activity, String fragmentName, String title, Bundle bundle) {
        Intent intent = new Intent(activity, FragmentBaseActivity.class);
        intent.putExtras(bundle);
        intent.putExtra(DISPLAY_FRAGMENT, fragmentName);
        intent.putExtra(TITLE, title);
        activity.startActivity(intent);
    }

    private void sendEmail() {
        try {
            if (PermissionUtils.isPermissionGranted(this
                    , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}
                    , GizConstants.RQ_CODE.STORAGE_PERMISIONS)) {

                String imagePath = Environment.getExternalStorageDirectory() + File.separator + "image.jpg";
                String pdfPath = Environment.getExternalStorageDirectory() + File.separator + "report.pdf";
                // export as image
                layoutToImage(getPDFRootView(), imagePath);
                // convert to pdf
                imageToPDF(imagePath, pdfPath);
                // send as email
                sendEmailWithAttachment(getEmailSubject(), pdfPath);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public RecyclerView getPDFRootView() {
        return findViewById(R.id.recyclerView);
    }

    protected String getEmailSubject() {
        return emailSubject;
    }

    public void layoutToImage(RecyclerView relativeLayout, String fileExportPath) throws IOException {
        // get view group using reference
        // convert view group to bitmap
        /*
        relativeLayout.setDrawingCacheEnabled(true);
        relativeLayout.buildDrawingCache();

        Bitmap bm = relativeLayout.getDrawingCache();

         */
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        getScreenshotFromRecyclerView(relativeLayout).compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(fileExportPath);
        f.createNewFile();

        // f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
    }

    public void imageToPDF(String imagePath, String pdfPath) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfPath)); //  Change pdf's name.
        document.open();
        Image img = Image.getInstance(imagePath);
        float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                - document.rightMargin() - 0) / img.getWidth()) * 100;
        img.scalePercent(scaler);
        img.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
        document.add(img);
        document.close();
        Toast.makeText(this, "PDF Generated successfully!..", Toast.LENGTH_SHORT).show();
    }

    private void sendEmailWithAttachment(String subject, String pathToMyAttachedFile) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        //emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"email@example.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "body text");
        File file = new File(pathToMyAttachedFile);
        if (!file.exists() || !file.canRead()) {
            return;
        }

        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
    }


    public Bitmap getScreenshotFromRecyclerView(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {
            int size = adapter.getItemCount();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {
                    bitmaCache.put(String.valueOf(i), drawingCache);
                }
                height += holder.itemView.getMeasuredHeight();
            }
            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            bigCanvas.drawColor(Color.WHITE);
            for (int i = 0; i < size; i++) {
                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
            }
        }
        return bigBitmap;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_base);
        Toolbar toolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);

        titleTextView = findViewById(R.id.toolbar_title);
        emailIcon = findViewById(R.id.email_report_icon);
        emailIcon.setColorFilter(Color.argb(255, 255, 255, 255));

        emailIcon.setOnClickListener(v -> sendEmail());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);

            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
            actionBar.setElevation(0);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String title = bundle.getString(TITLE);
            if (StringUtils.isNotBlank(title)) {
                titleTextView.setText(title);
            }
            INDICATOR_CODE = bundle.getString(INDICATOR_CODE);

            String date = bundle.getString(GizConstants.ReportParametersHelper.REPORT_DATE);
            String communityName = bundle.getString(GizConstants.ReportParametersHelper.COMMUNITY);
            String name = getRequestedFragmentName(DISPLAY_FRAGMENT);

            emailSubject = String.format("%s - %s - %s", name, date, communityName);
            String fragmentName = bundle.getString(DISPLAY_FRAGMENT);
            Fragment fragment = getRequestedFragment(fragmentName);
            if (fragment != null)
                switchToFragment(fragment);
        }
        onCreation();
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    public void setVisibility() {
        emailIcon.setVisibility(View.VISIBLE);
    }

    public void setVisibilityGone() {
        emailIcon.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    private void switchToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, fragment)
                .commit();
    }


    private @Nullable
    String getRequestedFragmentName(@Nullable String name) {
        if (name == null || StringUtils.isBlank(name))
            return "";

        switch (name) {
            case EligibleChildrenReportFragment
                    .TAG:
                return "Children Due for vaccine";
            case VillageDoseReportFragment
                    .TAG:
                return "Village Doses";
            default:
                return "";
        }
    }

    private @Nullable
    Fragment getRequestedFragment(@Nullable String name) {
        if (name == null || StringUtils.isBlank(name))
            return null;

        Fragment fragment;
        switch (name) {
            case FilterReportFragment
                    .TAG:
                fragment = new FilterReportFragment();
                break;
            case EligibleChildrenReportFragment
                    .TAG:
                fragment = new EligibleChildrenReportFragment();
                break;
            case VillageDoseReportFragment
                    .TAG:
                fragment = new VillageDoseReportFragment();
                break;
            default:
                fragment = null;
                break;
        }

        if (fragment != null)
            fragment.setArguments(getIntent().getExtras());

        assert fragment != null;
        return fragment;
    }

    @Override
    protected void onCreation() {
        Timber.v("Empty onCreation");
    }

    @Override
    protected void onResumption() {
        Timber.v("Empty onResumption");
    }
}

