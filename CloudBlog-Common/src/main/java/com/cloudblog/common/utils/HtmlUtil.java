package com.cloudblog.common.utils;


import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class HtmlUtil {

    /**
     * 删除HTML标签
     *
     * @param inputString
     * @return
     */
    public static String removeHtmlTag(String inputString) {
        if (inputString == null) {
            return null;
        }
        return Jsoup.parse(inputString).text();
    }
}
