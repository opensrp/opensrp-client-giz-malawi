package org.smartregister.giz.presenter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.child.util.Constants;
import org.smartregister.configurableviews.model.LoginConfiguration;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.domain.Setting;
import org.smartregister.giz.R;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.interactor.LoginInteractor;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.giz.util.ImageLoaderRequest;
import org.smartregister.login.model.BaseLoginModel;
import org.smartregister.login.presenter.BaseLoginPresenter;
import org.smartregister.view.contract.BaseLoginContract;

import java.lang.ref.WeakReference;

import timber.log.Timber;

public class LoginPresenter extends BaseLoginPresenter implements BaseLoginContract.Presenter {

    public LoginPresenter(BaseLoginContract.View loginView) {
        mLoginView = new WeakReference<>(loginView);
        mLoginInteractor = new LoginInteractor(this);
        mLoginModel = new BaseLoginModel();

    }

    @Override
    public void canvasGlobalLayoutListenerProcessor(ScrollView canvasSV,
                                                    ViewTreeObserver.OnGlobalLayoutListener layoutListener) {
        // do nothing
    }

    @Override
    public void processViewCustomizations() {
        try {
            String jsonString = getJsonViewFromPreference(
                    GizConstants.KEY.VIEW_CONFIGURATION_PREFIX + GizConstants.CONFIGURATION.LOGIN);
            if (jsonString == null) {
                return;
            }

            ViewConfiguration loginView = GizMalawiApplication.getJsonSpecHelper().getConfigurableView(jsonString);
            LoginConfiguration metadata = (LoginConfiguration) loginView.getMetadata();
            LoginConfiguration.Background background = metadata.getBackground();

            CheckBox showPasswordCheckBox = getLoginView().getActivityContext()
                    .findViewById(R.id.login_show_password_checkbox);
            TextView showPasswordTextView = getLoginView().getActivityContext()
                    .findViewById(R.id.login_show_password_text_view);
            if (!metadata.getShowPasswordCheckbox()) {
                showPasswordCheckBox.setVisibility(View.GONE);
                showPasswordTextView.setVisibility(View.GONE);
            } else {
                showPasswordCheckBox.setVisibility(View.VISIBLE);
                showPasswordTextView.setVisibility(View.VISIBLE);
            }

            if (background.getOrientation() != null && background.getStartColor() != null && background
                    .getEndColor() != null) {
                View loginLayout = getLoginView().getActivityContext().findViewById(R.id.login_layout);
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                gradientDrawable.setOrientation(
                        GradientDrawable.Orientation.valueOf(background.getOrientation()));
                gradientDrawable.setColors(new int[]{Color.parseColor(background.getStartColor()),
                        Color.parseColor(background.getEndColor())});
                loginLayout.setBackground(gradientDrawable);
            }

            if (metadata.getLogoUrl() != null) {
                ImageView imageView = getLoginView().getActivityContext().findViewById(R.id.login_logo);
                ImageLoaderRequest.getInstance(getLoginView().getActivityContext()).getImageLoader()
                        .get(metadata.getLogoUrl(), ImageLoader.getImageListener(imageView,
                                R.drawable.ic_who_logo, R.drawable.ic_who_logo)).getBitmap();
            }

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public boolean isServerSettingsSet() {
        try {
            Setting setting = GizMalawiApplication.getInstance().getContext().allSettings().getSetting(GizConstants.KEY.SITE_CHARACTERISTICS);
            JSONObject jsonObject = setting != null ? new JSONObject(setting.getValue()) : null;
            JSONArray settingArray = jsonObject != null && jsonObject.has(AllConstants.SETTINGS) ?
                    jsonObject.getJSONArray(AllConstants.SETTINGS) : null;

            if (settingArray != null && settingArray.length() > 0) {
                JSONObject settingObject = settingArray.getJSONObject(0);
                return !settingObject.isNull(Constants.KEY.VALUE);

            }
            return false;
        } catch (JSONException e) {
            Timber.e(e);
            return false;
        }
    }
}
