package dev.linhnv.fptuct.model;

/**
 * Created by linhnv on 02/03/2017.
 */

public class Gift {
    public int giftId;
    public String title;
    public int amount;
    public int percent;
    public Gift(){

    }
    public Gift(int giftId, String title, int amount, int percent){
        this.giftId = giftId;
        this.title = title;
        this.amount = amount;
        this.percent = percent;
    }
}
