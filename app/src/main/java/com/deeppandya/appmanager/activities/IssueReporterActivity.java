package com.deeppandya.appmanager.activities;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.model.DeviceInfo;
import com.deeppandya.appmanager.util.FirebaseUtil;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import static android.util.Patterns.EMAIL_ADDRESS;

/**
 * Created by deeppandya on 2017-07-27.
 */

public class IssueReporterActivity extends AppCompatActivity {
    private static final String TAG = IssueReporterActivity.class.getSimpleName();

    public static final int FEEDBACK = 0;
    public static final int FEATUREREQUEST = 1;
    public static final int BUGREPORT = 2;

    private TextInputEditText inputTitle;
    private TextInputEditText inputDescription;
    private TextInputEditText inputEmail;
    private TextView textDeviceInfo;
    private ImageButton buttonDeviceInfo;
    private ExpandableRelativeLayout layoutDeviceInfo;
    private FloatingActionButton buttonSend;
    private DeviceInfo deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (TextUtils.isEmpty(getTitle()))
            setTitle(R.string.air_title_report_issue);

        setContentView(R.layout.activity_issue_reporter);
        findViews();

        initViews();

        deviceInfo = new DeviceInfo(this);
        textDeviceInfo.setText(deviceInfo.toString());
    }

    private void findViews() {

        inputTitle = findViewById(R.id.air_inputTitle);
        inputDescription = findViewById(R.id.air_inputDescription);
        inputEmail = findViewById(R.id.air_inputEmail);
        textDeviceInfo = findViewById(R.id.air_textDeviceInfo);
        buttonDeviceInfo = findViewById(R.id.air_buttonDeviceInfo);
        layoutDeviceInfo = findViewById(R.id.air_layoutDeviceInfo);

        buttonSend = findViewById(R.id.air_buttonSend);
    }

    private void initViews() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        buttonDeviceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutDeviceInfo.toggle();
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportIssue();
            }
        });

        if (getIntent() != null && getIntent().getIntExtra("action", -1) != -1) {
            if (getIntent().getIntExtra("action", -1) == FEEDBACK) {
                getSupportActionBar().setTitle(getResources().getString(R.string.title_send_feedback));
            } else if (getIntent().getIntExtra("action", -1) == FEATUREREQUEST) {
                getSupportActionBar().setTitle(getResources().getString(R.string.title_send_feature_request));
            } else if (getIntent().getIntExtra("action", -1) == BUGREPORT) {
                getSupportActionBar().setTitle(getResources().getString(R.string.title_send_bug_report));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void reportIssue() {

        if (!validateInput()) return;

        String databaseName = "";
        if (getIntent() != null && getIntent().getIntExtra("action", -1) != -1) {
            if (getIntent().getIntExtra("action", -1) == FEEDBACK) {
                databaseName = "user_feedback";
            } else if (getIntent().getIntExtra("action", -1) == FEATUREREQUEST) {
                databaseName = "user_feature_request";
            } else if (getIntent().getIntExtra("action", -1) == BUGREPORT) {
                databaseName = "user_bug_report";
            }
        }

        FirebaseUtil.saveBugReportToFirebase(databaseName, inputTitle.getText().toString(), inputDescription.getText().toString(), inputEmail.getText().toString(), deviceInfo.toMarkdown());

        finish();

    }

    private boolean validateInput() {
        boolean hasErrors = false;

        if (TextUtils.isEmpty(inputTitle.getText())) {
            setError(inputTitle, R.string.air_error_no_title);
            hasErrors = true;
        } else {
            removeError(inputTitle);
        }

        if (TextUtils.isEmpty(inputDescription.getText())) {
            setError(inputDescription, R.string.air_error_no_description);
            hasErrors = true;
        } else {
            int bodyMinChar = 0;
            if (inputDescription.getText().toString().length() < bodyMinChar) {
                setError(inputDescription, getResources().getQuantityString(R.plurals.air_error_short_description, bodyMinChar, bodyMinChar));
                hasErrors = true;
            } else {
                removeError(inputDescription);
            }
        }

        if (!TextUtils.isEmpty(inputEmail.getText()) &&
                !EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
            setError(inputEmail, R.string.air_error_email_format);
            hasErrors = true;
        } else {
            removeError(inputEmail);
        }

        return !hasErrors;
    }

    private void setError(TextInputEditText editText, @StringRes int errorRes) {
        try {
            View layout = (View) editText.getParent();
            while (!layout.getClass().getSimpleName().equals(TextInputLayout.class.getSimpleName()))
                layout = (View) layout.getParent();
            TextInputLayout realLayout = (TextInputLayout) layout;
            realLayout.setError(getString(errorRes));
        } catch (ClassCastException | NullPointerException e) {
            Log.e(TAG, "Issue while setting error UI.", e);
        }
    }

    private void setError(TextInputEditText editText, String error) {
        try {
            View layout = (View) editText.getParent();
            while (!layout.getClass().getSimpleName().equals(TextInputLayout.class.getSimpleName()))
                layout = (View) layout.getParent();
            TextInputLayout realLayout = (TextInputLayout) layout;
            realLayout.setError(error);
        } catch (ClassCastException | NullPointerException e) {
            Log.e(TAG, "Issue while setting error UI.", e);
        }
    }

    private void removeError(TextInputEditText editText) {
        try {
            View layout = (View) editText.getParent();
            while (!layout.getClass().getSimpleName().equals(TextInputLayout.class.getSimpleName()))
                layout = (View) layout.getParent();
            TextInputLayout realLayout = (TextInputLayout) layout;
            realLayout.setError(null);
        } catch (ClassCastException | NullPointerException e) {
            Log.e(TAG, "Issue while removing error UI.", e);
        }
    }
}
