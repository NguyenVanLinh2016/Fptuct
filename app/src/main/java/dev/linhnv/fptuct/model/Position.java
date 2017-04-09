package dev.linhnv.fptuct.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by linhnv on 21/03/2017.
 */

public class Position {
    public double latitude;
    public double longtitude;

    public Position(){}

    public Position(double latitude, double longtitude){
        this.latitude = latitude;
        this.longtitude = longtitude;
    }
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("latitude", latitude);
        result.put("longtitude", longtitude);
        return result;
    }
}
