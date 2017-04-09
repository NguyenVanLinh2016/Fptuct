package dev.linhnv.fptuct;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.thomashaertel.widget.MultiSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;


public class UpdateInfoActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edtEmail, edtDateOfBirth, edtFullname, edtPhone, edtSchool, edtFieldOther;
    TextInputLayout tilFullname, tilPhone, tilEmail, tilField, tilGender, tilDateOfBirth, tilField_Other;
    RadioButton radioNam, radioNu;
    RadioGroup radioGroupGender;
    ImageView imgCalendar;
    Button btnHoanTat;
    String dateOfBirth, email, token, fullname, phone, school, field, other,gender, password, username;
    MultiSpinner multiSpiner;
    private ProgressDialog progressDialog;
    ArrayAdapter<String> adapter;
    String getAgeFromEdt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        edtFullname = (EditText) findViewById(R.id.edtFullname_UpdateInfor);
        edtEmail = (EditText) findViewById(R.id.edtEmailUpDateInfor);
        imgCalendar = (ImageView) findViewById(R.id.imgCalendar);
        edtDateOfBirth = (EditText) findViewById(R.id.edtDateOfBirthUpdateInfor);
        btnHoanTat = (Button) findViewById(R.id.btnHoanTat);
        //edtDateOfBirth.setEnabled(false);
        edtPhone = (EditText) findViewById(R.id.edtPhone_UpdateInfor);
        edtSchool = (EditText) findViewById(R.id.edtField_UpdadteInfor);
        edtFieldOther = (EditText) findViewById(R.id.edtField_UpdadteOther);
        tilFullname = (TextInputLayout) findViewById(R.id.tilFullname_UpdateInfor);
        tilEmail = (TextInputLayout) findViewById(R.id.tilEmail_UpdateInfor);
        tilPhone = (TextInputLayout) findViewById(R.id.tilPhone_UpdateInfor);
        tilField = (TextInputLayout) findViewById(R.id.tilField_UpdateInfor);
        tilField_Other = (TextInputLayout) findViewById(R.id.tilField_Other);
        tilGender = (TextInputLayout) findViewById(R.id.tilGender_UpdateInfor);
        tilDateOfBirth = (TextInputLayout) findViewById(R.id.tilDateOfBirth_UpdateInfor);
        radioNam = (RadioButton) findViewById(R.id.radioNam);
        radioNu = (RadioButton) findViewById(R.id.radioNu);
        radioGroupGender = (RadioGroup) findViewById(R.id.RadioGroupGender);
        multiSpiner = (MultiSpinner) findViewById(R.id.spinnerMulti);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.add("Công nghệ thông tin");
        adapter.add("Kinh tế");
        adapter.add("Ngôn ngữ");
        adapter.add("Thiết kế đồ họa");
        multiSpiner.setAdapter(adapter, false, onSelectedListener);
        // set initial selection
        boolean[] selectedItems = new boolean[adapter.getCount()];
        selectedItems[0] = true; //chonj item dau tien
        multiSpiner.setSelected(selectedItems);

        edtDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar lich = Calendar.getInstance();
                int ngay = lich.get(Calendar.DAY_OF_MONTH);
                int thang = lich.get(Calendar.MONTH);
                int nam = lich.get(Calendar.YEAR);
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment();
                cdp.setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                    @Override
                    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                        edtDateOfBirth.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        dateOfBirth = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    }
                });
                cdp.setFirstDayOfWeek(Calendar.SUNDAY);
                cdp.setPreselectedDate(nam, thang, ngay);
                cdp.setDateRange(null, null);
                cdp.setThemeLight();
                cdp.show(getSupportFragmentManager(), "tag1");
            }
        });

        imgCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar lich = Calendar.getInstance();
                int ngay = lich.get(Calendar.DAY_OF_MONTH);
                int thang = lich.get(Calendar.MONTH);
                int nam = lich.get(Calendar.YEAR);
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment();
                cdp.setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                    @Override
                    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                        edtDateOfBirth.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        dateOfBirth = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    }
                });
                cdp.setFirstDayOfWeek(Calendar.SUNDAY);
                cdp.setPreselectedDate(nam, thang, ngay);
                cdp.setDateRange(null, null);
                cdp.setThemeLight();
                cdp.show(getSupportFragmentManager(), "tag1");

            }
        });

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            token = extras.getString("token");
            password = extras.getString("password");
            edtEmail.setText(extras.getString("email"));
            edtFullname.setText(extras.getString("fullname"));
            username = extras.getString("username");
        }

        //Set cho text input layout không hiển thị lỗi khi nhập dữ liệu
        edtFullname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilFullname.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilPhone.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edtPhone.getText().length() < 10 || edtPhone.getText().length() > 11) {
                    tilPhone.setError("Số điện thoại bạn vừa nhập chưa đúng!");
                } else {
                    tilPhone.setErrorEnabled(false);
                }
            }
        });

        edtSchool.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilField.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilEmail.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (EMAIL_ADDRESS_PATTERN.matcher(edtEmail.getText().toString()).matches()) {
                    tilEmail.setErrorEnabled(false);
                } else {
                    tilEmail.setError("Email bạn vừa nhập không đúng!");
                }
            }
        });
        edtDateOfBirth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edtDateOfBirth.getText().toString().length() != 0) {
                    tilDateOfBirth.setErrorEnabled(false);
                }
            }
        });
        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                tilGender.setErrorEnabled(false);
            }
        });

        btnHoanTat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check = false;

                if (edtFullname.getText().length() == 0) {
                    tilFullname.setError("Vui lòng nhập họ và tên!");
                    tilFullname.requestFocus();
                } else if (edtPhone.getText().length() == 0) {
                    tilPhone.setError("Vui lòng nhập số điện thoại!");
                    tilPhone.requestFocus();
                } else if (edtEmail.getText().toString().length() == 0) {
                    tilEmail.setError("Vui lòng nhập email!");
                    tilEmail.requestFocus();
                } else if (edtDateOfBirth.getText().length() == 0) {
                    tilDateOfBirth.setError("Bạn chưa chọn ngày tháng năm sinh!");
                    tilDateOfBirth.requestFocus();
                } else {
                    //lay gia tri cua o ngay thang nam
                    getAgeFromEdt = edtDateOfBirth.getText().toString();
                    String []getDate = getAgeFromEdt.split("/", -1);
                    int day = Integer.parseInt(getDate[0]);
                    int month = Integer.parseInt(getDate[1]);
                    int year = Integer.parseInt(getDate[2]);
                    //goi ham getAge de tuyen nam thang ngay vao va tra ve so tuoi
                    if(getAge(year, month, day) < 15){ //validation tuoi phai lon hon 15
                        tilDateOfBirth.setError("Bạn phải từ 15 tuổi trở lên");
                        tilDateOfBirth.requestFocus();
                    }else if (edtSchool.getText().toString().length() == 0) {
                        tilField.setError("Vui lòng nhập trường!");
                        tilField.requestFocus();
                    } else if (!radioNam.isChecked() && !radioNu.isChecked()) {
                        tilGender.setError("Vui lòng chọn giới tính!");
                    } else {
                        check = true;
                    }
                }
                if (check) {
                    fullname = edtFullname.getText().toString();
                    phone = edtPhone.getText().toString();
                    email = edtEmail.getText().toString();
                    school = edtSchool.getText().toString();
                    other = edtFieldOther.getText().toString();
                    if (radioNam.isChecked()) {
                        gender = "1";
                    }
                    if (radioNu.isChecked()) {
                        gender = "2";
                    }
                    new SendPostRequestEditUser().execute();
                }
            }
        });
    }
    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    builder.append(adapter.getItem(i)).append(",");
                }
            }
            field = builder.toString();
        }
    };
    @Override
    public void onClick(View v) {

    }

    //hàm để post data server
    public class SendPostRequestEditUser extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            progressDialog = new ProgressDialog(UpdateInfoActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);//co the cancel bang phim back
            progressDialog.show();
        }

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("http://api.fptuct.nks.com.vn/api/auth/edit");
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("username", username);
                postDataParams.put("token", token);
                postDataParams.put("Fullname", fullname);
                postDataParams.put("Phone", phone);
                postDataParams.put("School", school);
                postDataParams.put("Email", email);
                postDataParams.put("DateOfBirth", dateOfBirth);
                postDataParams.put("Field", field + other);
                postDataParams.put("Gender", gender);
                Log.e("params", postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(150000 /* milliseconds */);
                conn.setConnectTimeout(150000 /* milliseconds */);
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
                if (message.matches("Edit ok")) {
                    Toast.makeText(UpdateInfoActivity.this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                    SharedPreferences prefs = getSharedPreferences("token", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("token", token);
                    editor.putString("fullname", fullname);
                    editor.putString("password", password);
                    editor.commit();
                    startActivity(new Intent(UpdateInfoActivity.this, MenuQuizActivity.class));
                    finish();
                } else {
                    Toast.makeText(UpdateInfoActivity.this, "Email này đã được đăng ký!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(UpdateInfoActivity.this, "Email này đã được đăng ký!", Toast.LENGTH_SHORT).show();
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

    //phương thức check email
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );
    public String getToday() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return date;
    }
    private int getAge(int year, int month, int day){
        int a;
        //lay ngay thang nam hien tai
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        //cat chuoi bang spilt de lay ra ngay thang nam
        String [] today = date.split("-", -1);
        int yearNow = Integer.parseInt(today[0]);
        int monthNow = Integer.parseInt(today[1]);
        int dayNow = Integer.parseInt(today[2]);
        a = yearNow - year;
        //neu thang < thang hien tai, hoac bang thang hien tai thi xet tiep ngay, neu ngay nho hon thi tru di 1 nam
        if((month < monthNow || month == monthNow) && (day < dayNow)){
            a--;
        }
       return a;
    }
}
