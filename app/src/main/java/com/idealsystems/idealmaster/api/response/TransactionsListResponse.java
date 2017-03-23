package com.idealsystems.idealmaster.api.response;

import java.util.ArrayList;

/**
 * Created - Schumakher on 25-Aug-16.
 */
public class TransactionsListResponse {
    private int count;
    private int currentPage;
    private int limit;
    private ArrayList<TransactionResponse> objs;

    public int getCount() {
        return count;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getLimit() {
        return limit;
    }

    public ArrayList<TransactionResponse> getObjs() {
        return objs;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setObjs(ArrayList<TransactionResponse> objs) {
        this.objs = objs;
    }
}
