package io.github.ipagentpool.spider.fomatter;

/**
 * @author wengchengjian
 * @date 2022/6/1-11:13
 */
public class StringToIntegerFomatter implements Fomatter<String,Integer>{
    @Override
    public Integer format(String s) {
        return Integer.parseInt(s);
    }

    @Override
    public String preProcess(String s) throws Exception {
        return s;
    }
}
