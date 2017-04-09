package dev.linhnv.fptuct.model;

import java.io.Serializable;

/**
 * Created by linhnv on 01/03/2017.
 */

public class QuizGetResultUser implements Serializable {
    public int positionAnswer;
    public String answerUser;
    public QuizGetResultUser(){

    }
    public QuizGetResultUser(int positionAnswer, String answerUser){
        this.positionAnswer = positionAnswer;
        this.answerUser = answerUser;
    }

}
