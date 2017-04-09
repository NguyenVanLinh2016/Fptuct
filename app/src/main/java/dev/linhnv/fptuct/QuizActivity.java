package dev.linhnv.fptuct;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dev.linhnv.fptuct.model.HttpHandler;
import dev.linhnv.fptuct.model.Question;
import dev.linhnv.fptuct.model.QuizGetResultUser;

import static android.R.id.message;
import static android.content.ContentValues.TAG;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener, Html.ImageGetter {


    private Toolbar toolbar;
    private TextView tv_question;
    private Button btn_a, btn_b, btn_c, btn_d;
    String token;
    String url_listQuiz;
    private ProgressDialog progressDialog;
    private List<Question> list_question;
    //list nhận giá trị người dùng chọn
    private List<QuizGetResultUser> nav_item_list;
    private TextView tv_next_personality;
    //khai báo các biến answer
    private String answer;
    //nhan id de lay chu de quiz
    private int id;
    private int checkUser;
    private int userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        toolbar = (Toolbar) findViewById(R.id.abcd);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.quiz_test));
        }

        SharedPreferences sharedPreferences = this.getSharedPreferences("token", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");


        id = getIntent().getExtras().getInt("id");
        checkUser = getIntent().getExtras().getInt("checkUser");
        userId = getIntent().getExtras().getInt("userId");
        url_listQuiz = "http://api.fptuct.nks.com.vn/api/question/list/?token=" +token +"&id="+ id;
        new GetListQuiz().execute();

        tv_question = (TextView) findViewById(R.id.tv_question);
        btn_a = (Button)findViewById(R.id.btn_a);
        btn_b = (Button) findViewById(R.id.btn_b);
        btn_c = (Button) findViewById(R.id.btn_c);
        btn_d = (Button) findViewById(R.id.btn_d);
        btn_a.setTransformationMethod(null);
        btn_b.setTransformationMethod(null);
        btn_c.setTransformationMethod(null);
        btn_d.setTransformationMethod(null);
        tv_next_personality = (TextView) findViewById(R.id.tv_next_personality);

        tv_next_personality.setOnClickListener(this);
        btn_a.setOnClickListener(this);
        btn_b.setOnClickListener(this);
        btn_c.setOnClickListener(this);
        btn_d.setOnClickListener(this);
        //list nhận giá trị người dùng chọn
        nav_item_list = new ArrayList<QuizGetResultUser>();
        for(int i=0; i<3; i++){
            nav_item_list.add(new QuizGetResultUser(i,""));
        }
    }

    @Override
    public Drawable getDrawable(String source) {
        return null;
    }

    //get List Quiz
    class GetListQuiz extends AsyncTask<Void, Void, Void> implements Html.ImageGetter {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(QuizActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url_listQuiz);
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    //Getting Json
                    JSONArray listQuizArray = jsonObject.getJSONArray("question");
                    list_question = new ArrayList<Question>();
                    for (int i = 0; i < listQuizArray.length(); i++) {
                        JSONObject list = listQuizArray.getJSONObject(i);
                        int questionId = list.getInt("QuestionId");
                        String title = list.getString("Title");
                        String ansA = list.getString("AnsA");
                        String ansB = list.getString("AnsB");
                        String ansC = list.getString("AnsC");
                        String ansD = list.getString("AnsD");
                        String right_answer = list.getString("RightAns");

                        Question q = new Question();
                        q.question_id = questionId;
                        q.title = title;
                        q.ans_a = ansA;
                        q.ans_b = ansB;
                        q.ans_c = ansC;
                        q.ans_d = ansD;
                        q.right_answer = right_answer;
                        list_question.add(q);
                    }
                    //Toast.makeText(QuizActivity.this, ""+list_question.get(0).title, Toast.LENGTH_SHORT).show();
                    //Log.d("thu", ""+list_question.get(0).title);

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
            //setText câu hỏi và câu trả lời lên
            setQuestion(0);
        }

        @Override
        public Drawable getDrawable(String source) {
            return null;
        }
    }
    int i = 0;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_next_personality:
                i++;
                if(i < 3){
                    setQuestion(i);
                    //Toast.makeText(this, ""+ list_question.get(i).right_answer, Toast.LENGTH_SHORT).show();
                    saveStateAnswer(i);
                    //saveStateRightAnswer(i);
                }else{
                    if (checkUser == 0){
                        Log.d("Thu", "0");
                        Intent intent = new Intent(QuizActivity.this, GiftActivity.class);
                        Bundle b = new Bundle();
                        b.putInt("checkUser", checkUser);
                        intent.putExtras(b);
                        startActivity(intent);
                        startActivity(new Intent(QuizActivity.this, GiftActivity.class));
                        finish();
                    }else{
                        Log.d("Thu", "1");
                        startActivity(new Intent(QuizActivity.this, MainActivity.class));
                        finish();
                    }

                }
                break;
            case R.id.btn_a:
                btn_a.setBackgroundResource(R.drawable.choose_button);
                answer = "A";
                nav_item_list.get(i).answerUser = answer;
                //Toast.makeText(this, ""+nav_item_list.get(i).answerUser + "--"+ nav_item_list.get(i).positionAnswer, Toast.LENGTH_SHORT).show();
                rightAnswer(nav_item_list.get(i).positionAnswer);

                //disableButton(nav_item_list.get(i).positionAnswer);
                //saveStateRightAnswer(nav_item_list.get(i).positionAnswer);
                break;
            case R.id.btn_b:
                reset0();
                btn_b.setBackgroundResource(R.drawable.choose_button);
                answer = "B";
                nav_item_list.get(i).answerUser = answer;
                rightAnswer(nav_item_list.get(i).positionAnswer);
                //disableButton(nav_item_list.get(i).positionAnswer);
                //saveStateRightAnswer(nav_item_list.get(i).positionAnswer);
                break;
            case R.id.btn_c:
                reset0();
                btn_c.setBackgroundResource(R.drawable.choose_button);
                answer = "C";
                nav_item_list.get(i).answerUser = answer;
                rightAnswer(nav_item_list.get(i).positionAnswer);
                //disableButton(nav_item_list.get(i).positionAnswer);
                //saveStateRightAnswer(nav_item_list.get(i).positionAnswer);
                break;
            case R.id.btn_d:
                reset0();
                btn_d.setBackgroundResource(R.drawable.choose_button);
                answer = "D";
                nav_item_list.get(i).answerUser = answer;
                rightAnswer(nav_item_list.get(i).positionAnswer);
                //disableButton(nav_item_list.get(i).positionAnswer);
                //saveStateRightAnswer(nav_item_list.get(i).positionAnswer);
                break;
        }
    }
    //settext giá trị lên Edittext
    public void setQuestion(int i){
        Spanned question = Html.fromHtml(list_question.get(i).title);
        Spanned answer_a = Html.fromHtml(list_question.get(i).ans_a);
        Spanned answer_b = Html.fromHtml(list_question.get(i).ans_b);
        Spanned answer_c = Html.fromHtml(list_question.get(i).ans_c);
        Spanned answer_d = Html.fromHtml(list_question.get(i).ans_d);

        tv_question.setText(question);
        btn_a.setText(answer_a);
        btn_b.setText(answer_b);
        btn_c.setText(answer_c);
        btn_d.setText(answer_d);
    }

    //settext giá trị lên Edittext
    public void rightAnswer(int i){
        if(nav_item_list.get(i).answerUser.equalsIgnoreCase(list_question.get(i).right_answer)){
            Toast.makeText(this, getString(R.string.rightAnswer), Toast.LENGTH_SHORT).show();
        }else{
            failAnswerUser(i);
            Toast.makeText(this, getString(R.string.failAnswer), Toast.LENGTH_SHORT).show();
        }
        switch (list_question.get(i).right_answer){
            case "A":
                btn_a.setBackgroundResource(R.drawable.right_answer_button);
                break;
            case "B":
                btn_b.setBackgroundResource(R.drawable.right_answer_button);
                break;
            case "C":
                btn_c.setBackgroundResource(R.drawable.right_answer_button);
                break;
            case "D":
                btn_d.setBackgroundResource(R.drawable.right_answer_button);
                break;
        }
    }
    //viết hàm để lưu lại trạng thái của người dùng khi họ click next và previous
    //dựa vào kết quả được chọn từ người dùng khi back lại mình lưu lại trạng thái bằng việc lấy giá trị và set background button theo như lúc đầu được chọn
    public void saveStateAnswer(int i){
        switch (nav_item_list.get(i).answerUser){
            case "A":
                reset0();
                btn_a.setBackgroundResource(R.drawable.choose_button);
                break;
            case "B":
                reset0();
                btn_b.setBackgroundResource(R.drawable.choose_button);
                break;
            case "C":
                reset0();
                btn_c.setBackgroundResource(R.drawable.choose_button);
                break;
            case "D":
                reset0();
                btn_d.setBackgroundResource(R.drawable.choose_button);
                break;
            default:
                reset0();
                break;
        }
    }
    //viet ham de chuyen button thanh mau do khi nguoi dung chon sai
    public void failAnswerUser(int i){
        switch (nav_item_list.get(i).answerUser){
            case "A":
                btn_a.setBackgroundResource(R.drawable.fail_answer_button);
                break;
            case "B":
                btn_b.setBackgroundResource(R.drawable.fail_answer_button);
                break;
            case "C":
                btn_c.setBackgroundResource(R.drawable.fail_answer_button);
                break;
            case "D":
                btn_d.setBackgroundResource(R.drawable.fail_answer_button);
                break;
            default:
                //reset0();
                break;
        }
    }
    //Reset lại button khi thay đổi click
    public void reset0(){
        btn_a.setBackgroundResource(R.drawable.design_button);
        btn_b.setBackgroundResource(R.drawable.design_button);
        btn_c.setBackgroundResource(R.drawable.design_button);
        btn_d.setBackgroundResource(R.drawable.design_button);
    }
    //chi cho chon 1 lan
    public void disableButton(int i){
        btn_a.setEnabled(false);
        btn_b.setEnabled(false);
        btn_c.setEnabled(false);
        btn_d.setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(R.string.dialog_exit_quiz);
        alertDialog.setTitle(R.string.titleDialog);
        alertDialog.setPositiveButton(R.string.setNegativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(QuizActivity.this, MainActivity.class));
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
