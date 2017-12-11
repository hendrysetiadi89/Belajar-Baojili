package com.combintech.baojili.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 10/10/2017.
 */

public class CodeAndSizeList {

    @SerializedName("code")
    @Expose
    private ArrayList<String> code = null;
    @SerializedName("size")
    @Expose
    private ArrayList<String> size = null;

    public ArrayList<String> getCode() {
        return code;
    }

    public ArrayList<String> getSize() {
        return size;
    }

}
