package dev.linhnv.fptuct.model;

import java.io.Serializable;

/**
 * Created by linhnv on 01/03/2017.
 */

public class Question implements Serializable {
    public int question_id;
    public String title;
    public String ans_a;
    public String ans_b;
    public String ans_c;
    public String ans_d;
    public String ans_e;
    public String ans_f;
    public String ans_g;
    public String ans_h;
    public String right_answer;
    public Question(){

    }

    public Question(int question_id, String title, String ans_a, String ans_b, String ans_c, String ans_d, String ans_e,
                    String ans_f, String ans_g, String ans_h, String right_answer) {
        this.question_id = question_id;
        this.title = title;
        this.ans_a = ans_a;
        this.ans_b = ans_b;
        this.ans_c = ans_c;
        this.ans_d = ans_d;
        this.ans_e = ans_e;
        this.ans_f = ans_f;
        this.ans_g = ans_g;
        this.ans_h = ans_h;
        this.right_answer = right_answer;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAns_a() {
        return ans_a;
    }

    public void setAns_a(String ans_a) {
        this.ans_a = ans_a;
    }

    public String getAns_b() {
        return ans_b;
    }

    public void setAns_b(String ans_b) {
        this.ans_b = ans_b;
    }

    public String getAns_c() {
        return ans_c;
    }

    public void setAns_c(String ans_c) {
        this.ans_c = ans_c;
    }

    public String getAns_d() {
        return ans_d;
    }

    public void setAns_d(String ans_d) {
        this.ans_d = ans_d;
    }

    public String getAns_e() {
        return ans_e;
    }

    public void setAns_e(String ans_e) {
        this.ans_e = ans_e;
    }

    public String getAns_f() {
        return ans_f;
    }

    public void setAns_f(String ans_f) {
        this.ans_f = ans_f;
    }

    public String getAns_g() {
        return ans_g;
    }

    public void setAns_g(String ans_g) {
        this.ans_g = ans_g;
    }

    public String getAns_h() {
        return ans_h;
    }

    public void setAns_h(String ans_h) {
        this.ans_h = ans_h;
    }

    public String getRight_answer() {
        return right_answer;
    }

    public void setRight_answer(String right_answer) {
        this.right_answer = right_answer;
    }
}