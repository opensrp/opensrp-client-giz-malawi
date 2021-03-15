package org.smartregister.giz.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.giz.R;
import org.smartregister.giz.fragment.EligibleChildrenReportFragment;
import org.smartregister.giz.fragment.FilterReportFragment;
import org.smartregister.giz.fragment.VillageDoseReportFragment;
import org.smartregister.giz.listener.PdfGeneratorListener;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.giz.util.PdfGeneratorHelper;
import org.smartregister.util.PermissionUtils;
import org.smartregister.view.activity.SecuredActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

public class FragmentBaseActivity extends SecuredActivity {
    protected static final String DISPLAY_FRAGMENT = "DISPLAY_FRAGMENT";
    protected static final String TITLE = "TITLE";
    protected String INDICATOR_CODE = "INDICATOR_CODE";
    private String emailSubject = "";
    private String emailAttachmentName = "";
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

    public void sendEmail() {
        try {
            if (PermissionUtils.isPermissionGranted(this
                    , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}
                    , GizConstants.RQ_CODE.STORAGE_PERMISIONS)) {
                final String[] pdfPath = {Environment.getExternalStorageDirectory() + File.separator + emailAttachmentName + ".pdf"};
                // export as image
                PdfGeneratorHelper.getBuilder()
                        .setContext(this)
                        .fromViewSource()
                        .fromView(getPDFRootView())
                        .setDefaultPageSize(PdfGeneratorHelper.PageSize.WRAP_CONTENT)
                        .setFileName(emailAttachmentName)
                        .setFolderName(GizConstants.EmailParameterHelper.REPORTS)
                        .openPDFafterGeneration(false)
                        .build(new PdfGeneratorListener() {
                            @Override
                            public void onFailure(PdfGeneratorHelper.FailureResponse failureResponse) {
                                super.onFailure(failureResponse);
                                Timber.e(failureResponse.getThrowable(),"Pdf Could not be Generated");
                            }

                            @Override
                            public void onSuccess(PdfGeneratorHelper.SuccessResponse response) {
                                super.onSuccess(response);
                                pdfPath[0] = response.getPath();
                                sendEmailWithAttachment(getEmailSubject(), pdfPath[0]);
                            }
                        });

            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public RelativeLayout getPDFRootView() {
        return findViewById(R.id.report_host);
    }

    protected String getEmailSubject() {
        return emailSubject;
    }

    public void layoutToImage(RelativeLayout relativeLayout, String fileExportPath) {
        // get view group using reference
        // convert view group to bitmap
        relativeLayout.setDrawingCacheEnabled(true);
        relativeLayout.buildDrawingCache();
        Bitmap bm = relativeLayout.getDrawingCache();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(fileExportPath);
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            Timber.e(e);
        }
    }


    private void sendEmailWithAttachment(String subject, String pathToMyAttachedFile) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        // emailIntent.putExtra(Intent.EXTRA_TEXT, " ");
        File file = new File(pathToMyAttachedFile);
        if (!file.exists() || !file.canRead()) {
            Timber.e("File does not exist");
            return;
        }
        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_base);
        Toolbar toolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);

        titleTextView = findViewById(R.id.toolbar_title);

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

            String report_date = bundle.getString(GizConstants.ReportParametersHelper.REPORT_DATE);
            String communityName = bundle.getString(GizConstants.ReportParametersHelper.COMMUNITY);
            String fragmentName = bundle.getString(DISPLAY_FRAGMENT);
            String name = getRequestedFragmentName(fragmentName);

            //Format Subject Date
            if (report_date != null) {
                DateFormat df = new SimpleDateFormat("dd MMM yyyy");
                Date date = null;
                try {
                    date = df.parse(report_date);
                } catch (Exception e) {
                    Timber.e(e,"Date could not be formatted");
                }
                emailSubject = String.format("%s - %s - %s", name, new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(date), communityName);
                emailAttachmentName = String.format("%s-%s-%s", name, new SimpleDateFormat("ddMMyyyy hh:mm:ss").format(date), communityName);
            }
            Fragment fragment = getRequestedFragment(fragmentName);
            if (fragment != null)
                switchToFragment(fragment);
        }
        onCreation();
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
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
                return getString(R.string.child_due_report_grouping_title);

            case VillageDoseReportFragment
                    .TAG:
                return getString(R.string.vaccine_doses_needed);
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

