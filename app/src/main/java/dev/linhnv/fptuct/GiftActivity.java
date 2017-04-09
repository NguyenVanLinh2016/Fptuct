package dev.linhnv.fptuct;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import dev.linhnv.fptuct.model.Gift;
import dev.linhnv.fptuct.model.HttpHandler;

import static android.content.ContentValues.TAG;

public class GiftActivity extends AppCompatActivity {

    private String token;
    public ImageView imageView;
    private RotateAnimation rotate;
    public Button button;
    private HashMap<Integer, String> presents;
    private int getRandom;
    private int imageGift;
    private String bonus = "";
    private ImageView img_done, img_nut;
    //url doc du lieu tu list gift
    String urlListGift;
    //doi tuong gift
    List<Gift> listGift;
    //check qua con hay khong
    String urlCheckGiftIsStock;
    String messageCheckGiftIsStock;
    int idGift;
    private int userId;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift);

        SharedPreferences pre = this.getSharedPreferences("token", MODE_PRIVATE);
        token = pre.getString("token", "");
        SharedPreferences pre1 = this.getSharedPreferences("infoUser", MODE_PRIVATE);
        userId = pre1.getInt("userId", -1);
        urlListGift = "http://api.fptuct.nks.com.vn/api/gift/list?token=" + token;
        new GetListGift().execute();

        imageView = (ImageView) findViewById(R.id.image);
        img_done = (ImageView) findViewById(R.id.image_done);
        img_nut = (ImageView) findViewById(R.id.imageNut);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startImageRotate();
                Random random = new Random();
                getRandom = random.nextInt(100);
                setImageResource(getRandom);
                button.setEnabled(false);

            }
        });
    }
    private void startImageRotate() {
        rotate = new RotateAnimation(0.0f, 2400.0f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotate.setFillAfter(true);
        rotate.setFillEnabled(true);
        rotate.setDuration(3000);
        imageView.startAnimation(rotate);
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                img_done.setVisibility(View.VISIBLE);
                //lấy quà và kiểm tra xem trên database còn có hok
                urlCheckGiftIsStock = "http://api.fptuct.nks.com.vn/api/gift/empty?token=" +token +"&id="+ idGift;
                new CheckListGiftIsStock().execute();
                button.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    //nhan gia tri de hi thi len dialog cho qua duoc quay thuong
    public void setImageResource(int x) {
        if (x <= 55) {
            imageGift = R.drawable.butbi;
            bonus = listGift.get(0).title;
            //idGift = 0; //hien tai khong trung thi  khong co tren database
        } else if (x <= 75) {
            imageGift = R.drawable.keychain;
            bonus = listGift.get(1).title;
            //idGift = 4;
        } else if (x <= 84) {
            imageGift = R.drawable.vongdeotay;
            bonus = listGift.get(2).title;
            //idGift = 3;
        } else if (x <= 88) {
            imageGift = R.drawable.balo;
            bonus = listGift.get(3).title;
            //idGift = 2;
        }else if (x <= 89){
            imageGift = R.drawable.monqua;
            bonus = listGift.get(6).title; //giai dat biet
            //idGift = 1;
        }else if(x <= 91){
            imageGift = R.drawable.miband;
            bonus = listGift.get(5).title; //Dây đeo Miband 2;
            //idGift = 1;
        }else {
            imageGift = R.drawable.aothun;
            bonus = listGift.get(5).title; //Áo thun FPTU";
            //idGift = 1;
        }
    }
    //get List Gift
    class GetListGift extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GiftActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(urlListGift);
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    //Getting Json
                    JSONArray giftArray = jsonObject.getJSONArray("gift");
                    listGift = new ArrayList<Gift>();
                    for (int i = 0; i < giftArray.length(); i++) {
                        JSONObject list = giftArray.getJSONObject(i);
                        int giftId = list.getInt("GiftId");
                        String title = list.getString("Title");
                        int amount = list.getInt("Amount");
                        int percent = list.getInt("Percent");
                        Gift gift = new Gift();
                        gift.giftId = giftId;
                        gift.title = title;
                        gift.amount = amount;
                        gift.percent = percent;
                        listGift.add(gift);
                    }


                } catch (JSONException ex) {
                    Log.e(TAG, "Json parsing error: " + ex.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Dismiss the progress dialog
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
    //check list gift con qua hay khong
    class CheckListGiftIsStock extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GiftActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(urlCheckGiftIsStock);
            Log.e(TAG, "Response from url: " + jsonStr);
            if(jsonStr != null){
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    //Getting Json user
                    JSONObject checkIsStockGift = jsonObject.getJSONObject("json");
                    messageCheckGiftIsStock = checkIsStockGift.getString("Message");
                    Log.e(TAG, "Message Check Gift is Stock: " + messageCheckGiftIsStock);

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
            if (messageCheckGiftIsStock.equalsIgnoreCase("Instock")){ //con qua
                inStock();
            }else{
                stock();
            }
        }
    }
    public void inStock(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GiftActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View inflator = inflater.inflate(R.layout.gift_dialog, null);
        alertDialog.setView(inflator);
        alertDialog.setCancelable(false);
        ImageView iv = (ImageView) inflator.findViewById(R.id.imageDialogQua);
        TextView tv = (TextView) inflator.findViewById(R.id.tvThongtinQua);
        iv.setImageResource(imageGift);

        tv.setText("Bạn đã trúng " + bonus + ".\nNhấn Ok để nhận quà.");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                img_done.setVisibility(View.VISIBLE);
                new SaveGiftData().execute();
                dialog.cancel();
            }
        });
        alertDialog.show();


    }
    public void stock(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GiftActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View inflator = inflater.inflate(R.layout.gift_dialog, null);
        alertDialog.setView(inflator);
        alertDialog.setCancelable(false);
        ImageView iv = (ImageView) inflator.findViewById(R.id.imageDialogQua);
        TextView tv = (TextView) inflator.findViewById(R.id.tvThongtinQua);
        iv.setImageResource(imageGift);
        tv.setText("Bạn đã trúng " + bonus + ".\nNhưng hiện tại hệ thống đã hết quà");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                startActivity(getIntent());
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
    //Save data gift
    class SaveGiftData extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GiftActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);//co the cancel bang phim back
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("http://api.fptuct.nks.com.vn/api/usergift/add"); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("UserId", userId);
                postDataParams.put("GiftId", 22);
                postDataParams.put("DateCreated", getToday());
                postDataParams.put("Promoter", 1);
                postDataParams.put("token", token);
                Log.e("params",postDataParams.toString());


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

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            startActivity(new Intent(GiftActivity.this, MainActivity.class));
            finish();
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
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
    public String getToday() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return date;
    }
}
