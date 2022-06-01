package io.github.ipagentpool.spider.fomatter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wengchengjian
 * @date 2022/6/1-11:13
 */
public class StringToDateFormatter implements Fomatter<String, Date> {
    protected static final String DATE_FORMAT  = "yyyy-MM-dd HH:mm:ss";

    @Override
    public Date format(String s) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getFormat());
        return simpleDateFormat.parse(s);
    }

    @Override
    public String preProcess(String s) throws Exception {
        return s;
    }

    public String getFormat(){
        return DATE_FORMAT;
    }
}
