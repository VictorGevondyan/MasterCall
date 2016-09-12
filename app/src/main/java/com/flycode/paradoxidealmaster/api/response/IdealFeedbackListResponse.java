package com.flycode.paradoxidealmaster.api.response;

import java.util.ArrayList;

/**
 * Created by acerkinght on 9/7/16.
 */
public class IdealFeedbackListResponse {
    private ArrayList<IdealFeedbackResponse> objs;

    public ArrayList<IdealFeedbackResponse> getObjs() {
        return objs;
    }

    public void setObjs(ArrayList<IdealFeedbackResponse> objs) {
        this.objs = objs;
    }
}