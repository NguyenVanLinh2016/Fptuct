package dev.linhnv.fptuct;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import dev.linhnv.fptuct.model.HttpHandler;

import static android.content.ContentValues.TAG;

public class MenuQuizActivity extends AppCompatActivity implements View.OnClickListener {

    //url doc du lieu tu table user de lay userId
    String token;
    String urlUser;
    int userId;
    private ProgressDialog progressDialog;
    //check user da nhan qua chua
    String urlCheckUserGift;
    String messageCheckGift;
    //khai bao bien de nhan biet user da nhan qua chua
    private int checkUser = 0;

    private Toolbar toolbar;
    Button btn_math, btn_phycicals, btn_chemicals, btn_english, btn_sport;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_quiz);

        toolbar = (Toolbar)findViewById(R.id.menu_quiz);
        if(toolbar != null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.quiz_test);
        }

        //get id user
        SharedPreferences sharedPreferences = this.getSharedPreferences("token", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        urlUser =  "http://api.fptuct.nks.com.vn/api/auth/me?token=" + token;
        new GetUserId().execute();
        btn_math = (Button) findViewById(R.id.btn_math);
        btn_phycicals = (Button) findViewById(R.id.btn_physical);
        btn_chemicals = (Button) findViewById(R.id.btn_chemicals);
        btn_english = (Button) findViewById(R.id.btn_english);
        btn_sport = (Button) findViewById(R.id.btn_sport);

        btn_math.setOnClickListener(this);
        btn_phycicals.setOnClickListener(this);
        btn_chemicals.setOnClickListener(this);
        btn_english.setOnClickListener(this);
        btn_sport.setOnClickListener(this);

        //tam thoi cho qua ben Admin activity de dung map
        startActivity(new Intent(MenuQuizActivity.this, AdminActivity.class));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_math:
                id = 3;
                sendId(id);
                break;
            case R.id.btn_physical:
                id = 4;
                sendId(id);
                break;
            case R.id.btn_chemicals:
                id = 5;
                sendId(id);
                break;
            case R.id.btn_english:
                id = 7;
                sendId(id);
                break;
            case R.id.btn_sport:
                id = 8;
                sendId(id);
                break;
        }
    }
    public void sendId(int x){
        Intent intent = new Intent(MenuQuizActivity.this, QuizActivity.class);
        Bundle b = new Bundle();
        b.putInt("id", x);
        b.putInt("checkUser", checkUser);
        b.putInt("userId", userId);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }
    //getUserID
    class GetUserId extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*progressDialog = new ProgressDialog(MenuQuizActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();*/
        }
        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(urlUser);
            Log.e(TAG, "Response from url: " + jsonStr);
            if(jsonStr != null){
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    //Getting Json user
                    JSONObject profile = jsonObject.getJSONObject("user");
                    userId = profile.getInt("id");
                    SharedPreferences prefs = getSharedPreferences("infoUser", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("userId", userId);
                    editor.commit();
                    Log.e(TAG, "UserId: " + userId);

                } catch (JSONException ex) {
                    Log.e(TAG, "Json parsing error: " + ex.getMessage());
                }
            }else{
                Log.e(TAG, "Couldn't get json from server.");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Dismiss the progress dialog
            /*if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }*/
            //check user da nhan qua chua
            urlCheckUserGift = "http://api.fptuct.nks.com.vn/api/usergift/check?token=" +token +"&id="+ userId;
            new CheckUserAlreadyGift().execute();
        }
    }
    //check user da nhan qua chua
    class CheckUserAlreadyGift extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MenuQuizActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(urlCheckUserGift);
            Log.e(TAG, "Response from url: " + jsonStr);
            if(jsonStr != null){
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    //Getting Json user
                    JSONObject checkUserGift = jsonObject.getJSONObject("json");
                    messageCheckGift = checkUserGift.getString("Message");
                    //Log.e(TAG, "Message: " + messageCheckGift);

                } catch (JSONException ex) {
                    Log.e(TAG, "Json parsing error: " + ex.getMessage());
                }
            }else{
                Log.e(TAG, "Couldn't get json from server.");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Dismiss the progress dialog
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            //Neu tra ve la Exits thi user da nhan qua roi
            if(messageCheckGift.equalsIgnoreCase("Exits")){
                checkUser = 1;
            }else{ //user chua nhan qua
                checkUser = 0;
            }
        }
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(R.string.dialog_exit_quiz);
        alertDialog.setTitle(R.string.titleDialog);
        alertDialog.setPositiveButton(R.string.setNegativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(MenuQuizActivity.this, MainActivity.class));
                finish();
            }
        });
        alertDialog.setNegativeButton(R.string.setPositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }
}
