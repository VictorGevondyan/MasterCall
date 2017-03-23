package com.idealsystems.idealmaster.api.response;

import java.util.ArrayList;

/**
 * Created by acerkinght on 8/22/16.
 */
public class OrdersListResponse {
    private int count;
    private int currentPage;
    private int limit;
    private ArrayList<OrderResponse> objs;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public ArrayList<OrderResponse> getObjs() {
        return objs;
    }

    public void setObjs(ArrayList<OrderResponse> objs) {
        this.objs = objs;
    }
}
