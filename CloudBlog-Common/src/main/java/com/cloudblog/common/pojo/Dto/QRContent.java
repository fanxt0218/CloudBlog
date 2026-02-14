package com.cloudblog.common.pojo.Dto;

import lombok.Data;

@Data
public class QRContent {

    private String Q;

    private String R;

    public String toString() {
        String str =  "问=" + Q;
        if (R != null && !R.isEmpty()) {
            str += ",'\n'答=" + R;
        }
        return str;
    }
}
