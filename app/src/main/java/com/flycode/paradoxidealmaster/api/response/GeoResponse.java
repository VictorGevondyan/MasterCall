package com.flycode.paradoxidealmaster.api.response;

import java.util.ArrayList;

/**
 * Created by acerkinght on 8/22/16.
 */
public class GeoResponse {
    private String name;
    private ArrayList<Double> geo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Double> getGeo() {
        return geo;
    }

    public void setGeo(ArrayList<Double> geo) {
        this.geo = geo;
    }
}
