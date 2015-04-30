package com.alex.shoplist.data.gson.results;

public class GsonResult<Result> {

    private Result result;

    public Result getResult() {
        return result;
    }

    @Override
    public String toString() {
        return result.toString();
    }
}
