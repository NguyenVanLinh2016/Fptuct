package dev.linhnv.fptuct;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dx.dxloadingbutton.lib.LoadingButton;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    EditText etTenDangNhap, etMatKhau;
    TextView tvQuenMatKhau, tvTaoTaiKhoan;
    LoadingButton btDangNhap;
    TextInputLayout tilUsername, tilPasword;
    GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions gso;
    LoginButton loginButton;
    CallbackManager callbackManager;
    Button btnLoginGoogle;
    String username, password, usernameDangKi, passwordDangKy, token, gender, email, fullname, dateOfBirth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_login);

        etTenDangNhap = (EditText) findViewById(R.id.edtTenDangNhap);
        etMatKhau = (EditText) findViewById(R.id.edtMatKhau);
        btDangNhap = (LoadingButton) findViewById(R.id.btnDangNhap);
        tvQuenMatKhau = (TextView) findViewById(R.id.tvQuenMatKhau);
        tvTaoTaiKhoan = (TextView) findViewById(R.id.tvTaoTaiKhoan);
        tilPasword = (TextInputLayout) findViewById(R.id.password_text_input_layout);
        tilUsername = (TextInputLayout) findViewById(R.id.username_text_input_layout);


        //Xử lý login Google
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //đăng kí nút đăng nhập google xác định size và chèn tiếng việt lên nút
        btnLoginGoogle = (Button) findViewById(R.id.btnLoginGoogle);
        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


        //Xử lý login Facebook
        loginButton = (LoginButton) findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile", "user_birthday"));
        loginButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest mGraphRequest = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {
                                if (response.getError() != null) {
                                    // handle error
                                } else {
                                    username = me.optString("email");
                                    password = me.optString("email");
                                    email = me.optString("email");
                                    fullname = me.optString("name");
                                    new Login().execute();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                mGraphRequest.setParameters(parameters);
                mGraphRequest.executeAsync();


            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


        btDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Set cho Button chạy
                //Thành công: btDangNhap.loadingSuccessful();
                //Lỗi: btDangNhap.loadingFailed();
                //Ngừng load và trở về trạng thái ban đầu: btDangNhap.cancelLoading();
                if (etTenDangNhap.getText().toString().length() == 0) {
                    btDangNhap.loadingFailed();
                    tilUsername.setError("Vui lòng nhập tên đăng nhập.");
                    tilUsername.requestFocus();
                }
                if (etMatKhau.getText().toString().length() == 0) {
                    tilPasword.setError("Vui lòng nhập mật khẩu.");
                    tilPasword.requestFocus();
                    btDangNhap.loadingFailed();
                } else {
                    username = etTenDangNhap.getText().toString();
                    password = etMatKhau.getText().toString();
                    new SendPostRequest().execute();
                }
            }
        });

        etTenDangNhap.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilUsername.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etMatKhau.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilPasword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        tvTaoTaiKhoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupDialog();
            }
        });

        tvQuenMatKhau.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                forgotPasswordDialog();
            }
        });
    }

    //Dialog Đăng Ký
    boolean check = false;
    AlertDialog dialog;

    public void signupDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View inflator = inflater.inflate(R.layout.login_dialog, null);
        alertDialog.setView(inflator);
        alertDialog.setCancelable(false);
        TextView tvTitle = (TextView) inflator.findViewById(R.id.tvDialogTitle);
        final EditText etTenDangNhap = (EditText) inflator.findViewById(R.id.edtDkTenDangNhap);
        final EditText etMatKhau = (EditText) inflator.findViewById(R.id.edtDkMatKhau);
        final EditText etNhapMatKhau = (EditText) inflator.findViewById(R.id.edtDkNhapLaiMatKhau);
        final TextInputLayout tilTenDangNhap = (TextInputLayout) inflator.findViewById(R.id.textInputTenDangNhap);
        final TextInputLayout tilMatKhau = (TextInputLayout) inflator.findViewById(R.id.textInputMatKhau);
        final TextInputLayout tilNhapMatKhau = (TextInputLayout) inflator.findViewById(R.id.textInputNhapMatKhau);


        alertDialog.setPositiveButton("Đăng ký", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        etNhapMatKhau.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilNhapMatKhau.setErrorEnabled(false);
                if (etMatKhau.getText().toString().equals(etNhapMatKhau.getText().toString())) {
                    tilNhapMatKhau.setErrorEnabled(false);
                    check = true;
                } else {
                    tilNhapMatKhau.setError("Mật khẩu bạn vừa nhập không khớp.");
                    check = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etTenDangNhap.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilTenDangNhap.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etMatKhau.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilMatKhau.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        alertDialog.setNegativeButton("Hủy",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


        dialog = alertDialog.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check = false;
                String tenDK = etTenDangNhap.getText().toString();
                String matkhauDK = etMatKhau.getText().toString();
                String nhapMatkhauDK = etNhapMatKhau.getText().toString();
                if (tenDK.length() == 0) {
                    tilTenDangNhap.setError("Vui lòng nhập tên đăng nhập.");
                    etTenDangNhap.requestFocus();
                    check = false;
                } else {
                    if (matkhauDK.length() == 0) {
                        tilMatKhau.setError("Vui lòng nhập mật khẩu!");
                        etMatKhau.requestFocus();
                        check = false;
                    } else {
                        if (matkhauDK.length() < 8){
                            tilMatKhau.setError("Mật khẩu phải nhiều hơn 8 kí tự!");
                            etMatKhau.requestFocus();
                            check = false;
                        }else{
                            if (!matkhauDK.matches(nhapMatkhauDK)) {
                                tilNhapMatKhau.setError("Mật khẩu nhập lại sai!");
                                etNhapMatKhau.requestFocus();
                                check = false;
                            } else {
                                check = true;
                            }
                        }
                    }
                }

                if (check) {
                    usernameDangKi = tenDK;
                    passwordDangKy = matkhauDK;
                    new SendPostUser().execute();
                    dialog.dismiss();
                    //Code xử lí khi validate xong dữ liệu
                }

            }
        });
    }
    //Dialog Quên mật khẩu
    public void forgotPasswordDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View inflator = inflater.inflate(R.layout.login_dialog, null);
        alertDialog.setView(inflator);
        alertDialog.setCancelable(false);
        TextView tvTitle = (TextView) inflator.findViewById(R.id.tvDialogTitle);
        final EditText etTenDangNhap = (EditText) inflator.findViewById(R.id.edtDkTenDangNhap);
        final EditText etMatKhau = (EditText) inflator.findViewById(R.id.edtDkMatKhau);
        final EditText etNhapMatKhau = (EditText) inflator.findViewById(R.id.edtDkNhapLaiMatKhau);
        final TextInputLayout tilTenDangNhap = (TextInputLayout) inflator.findViewById(R.id.textInputTenDangNhap);
        final TextInputLayout tilMatKhau = (TextInputLayout) inflator.findViewById(R.id.textInputMatKhau);
        final TextInputLayout tilNhapMatKhau = (TextInputLayout) inflator.findViewById(R.id.textInputNhapMatKhau);

        tvTitle.setText(getResources().getString(R.string.quenMatKhau));
        tilTenDangNhap.setHint("Nhập email");
        etMatKhau.setVisibility(View.GONE);
        etNhapMatKhau.setVisibility(View.GONE);

        alertDialog.setPositiveButton("Xác nhận",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


        alertDialog.setNegativeButton("Hủy",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        dialog = alertDialog.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tilTenDangNhap.getEditText().getText().toString().length() == 0) {
                    tilTenDangNhap.setError("Vui lòng nhập email!");
                    check = false;
                } else {
                    check = true;
                }

                if (check) {
                    dialog.dismiss();
                    //Code xử lí khi validate xong dữ liệu
                }

            }
        });

        etTenDangNhap.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = etTenDangNhap.getText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (email.length() > 0) {
                    if (email.matches(emailPattern)) {
                        tilTenDangNhap.setErrorEnabled(false);
                    } else {
                        tilTenDangNhap.setError("Email bạn vừa nhập không đúng.");
                        check = false;
                    }
                } else {
                    tilTenDangNhap.setError("Vui lòng nhập email.");
                    check = false;
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 100);
    }
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();
            username = acct.getEmail();
            password = acct.getEmail();
            fullname = acct.getDisplayName();
            email = acct.getEmail();
            new Login().execute();
        }else{
            Log.d("result", "handleSignInResult:" + result.isSuccess());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 100) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            int statusCode = result.getStatus().getStatusCode();
            Log.d("CheckEror", statusCode+"");
            handleSignInResult(result);
        }
    }

    //hàm để post data server
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
        }

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("http://api.fptuct.nks.com.vn/api/auth"); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("username", username);
                postDataParams.put("password", password);
                Log.e("params", postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                } else {
                    return new String("false");
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            try {
                final JSONObject root = new JSONObject(result);
                JSONObject json = root.getJSONObject("json");
                Log.e("Thu", json+"");
                String message = json.getString("message");
                if (message.equalsIgnoreCase("Login Successfully")) {
                    btDangNhap.startLoading();
                    token = root.getString("token");
                    btDangNhap.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btDangNhap.cancelLoading();
                            new readDataUser().execute("http://api.fptuct.nks.com.vn/api/auth/me?token=" + token);
                        }
                    }, 1000);
                } else {
                    tilPasword.setError("Sai tên đăng nhập hoặc mật khẩu");
                    tilPasword.requestFocus();
                    btDangNhap.loadingFailed();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
    //hàm để post data add user lên server
    public class SendPostUser extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
        }

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("http://api.fptuct.nks.com.vn/api/auth/add");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("username", usernameDangKi);
                postDataParams.put("Email", usernameDangKi + "@nks.vn");
                postDataParams.put("password", passwordDangKy);
                postDataParams.put("Fullname", "Admin");
                postDataParams.put("Phone", "0123456789");
                postDataParams.put("School", "Di An");
                postDataParams.put("Avatar", "default.jpg");
                postDataParams.put("Field", "Poly");
                postDataParams.put("DateOfBirth", "2017-01-01");
                postDataParams.put("Gender", "0");
                Log.e("params", postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                } else {
                    return new String("false");
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("Thu", result);
            try {
                JSONObject root = new JSONObject(result);
                JSONObject json = root.getJSONObject("json");
                String message = json.getString("message");
                if (message.equalsIgnoreCase("Add new ok")) {
                    Toast.makeText(LoginActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Lỗi đăng kí!", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private class readDataUser extends AsyncTask<String, Integer, String> {

        protected void onPreExecute() {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);//co the cancel bang phim back
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            String chuoi = docNoiDung_Tu_URL(strings[0]);
            return chuoi;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject root = new JSONObject(s);
                JSONObject json = root.getJSONObject("user");
                gender = json.getString("Gender");
                if (gender.matches("0")) {
                    Intent i = new Intent(LoginActivity.this, UpdateInfoActivity.class);
                    Bundle b = new Bundle();
                    b.putString("username",username);
                    b.putString("token", token);
                    b.putString("password", password);
                    b.putString("email", email);
                    b.putString("fullname", fullname);
                    i.putExtras(b);
                    startActivity(i);
                    progressDialog.dismiss();
                } else {
                    startActivity(new Intent(LoginActivity.this, MenuQuizActivity.class));
                    SharedPreferences prefs = getSharedPreferences("token", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("username", username);
                    editor.putString("token", token);
                    editor.putString("password", password);
                    editor.commit();
                    progressDialog.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static String docNoiDung_Tu_URL(String theUrl) {
        StringBuilder content = new StringBuilder();

        try {
            // create a url object
            URL url = new URL(theUrl);

            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();

            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;

            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
    public class Login extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);//co the cancel bang phim back
            progressDialog.show();
        }

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("http://api.fptuct.nks.com.vn/api/auth");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("username", username);
                postDataParams.put("password", password);
                Log.e("params", postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                } else {
                    return new String("false");
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            try {
                final JSONObject root = new JSONObject(result);
                JSONObject json = root.getJSONObject("json");
                String message = json.getString("message");
                if (message.equalsIgnoreCase("Login Successfully")) {
                    LoginManager.getInstance().logOut();
                    signOut();
                    token = root.getString("token");
                    new readDataUser().execute("http://api.fptuct.nks.com.vn/api/auth/me?token=" + token);
                } else {
                    new Signup().execute();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public class Signup extends AsyncTask<String, Void, String> {

            protected void onPreExecute() {
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);//co the cancel bang phim back
                progressDialog.show();
            }

            protected String doInBackground(String... arg0) {

                try {

                    URL url = new URL("http://api.fptuct.nks.com.vn/api/auth/add");

                    JSONObject postDataParams = new JSONObject();
                    postDataParams.put("username", username);
                    postDataParams.put("Email", email);
                    postDataParams.put("password", password);
                    postDataParams.put("Fullname", "Admin");
                    postDataParams.put("Phone", "0123456789");
                    postDataParams.put("Avatar", "default.jpg");
                    postDataParams.put("Field", "Poly");
                    postDataParams.put("DateOfBirth", "2017-01-01");
                    postDataParams.put("Gender", "0");
                    Log.e("params", postDataParams.toString());

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getPostDataString(postDataParams));

                    writer.flush();
                    writer.close();
                    os.close();

                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {

                        BufferedReader in = new BufferedReader(new
                                InputStreamReader(
                                conn.getInputStream()));

                        StringBuffer sb = new StringBuffer("");
                        String line = "";

                        while ((line = in.readLine()) != null) {

                            sb.append(line);
                            break;
                        }

                        in.close();
                        return sb.toString();

                    } else {
                        return new String("false");
                    }
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }

            }

            @Override
            protected void onPostExecute(String result) {
                progressDialog.dismiss();
                try {
                    JSONObject root = new JSONObject(result);
                    JSONObject json = root.getJSONObject("json");
                    String message = json.getString("message");
                    if (message.matches("Add new ok")) {
                        new Login().execute();
                    } else {
                        Toast.makeText(LoginActivity.this, "Lỗi đăng kí!", Toast.LENGTH_SHORT).show();
                        signOut();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Tài khoản đã tồn tại!", Toast.LENGTH_SHORT).show();
                    signOut();
                    LoginManager.getInstance().logOut();
                }
            }

        }
    }
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }
}
