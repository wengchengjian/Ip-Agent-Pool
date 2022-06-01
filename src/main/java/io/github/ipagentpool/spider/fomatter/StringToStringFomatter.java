package io.github.ipagentpool.spider.fomatter;

/**
 * @author wengchengjian
 * @date 2022/6/1-11:10
 */
public class StringToStringFomatter implements Fomatter<String,String>{
    @Override
    public String format(String s) {
        return s;
    }

    @Override
    public String preProcess(String s) throws Exception {
        return s;
    }
}
