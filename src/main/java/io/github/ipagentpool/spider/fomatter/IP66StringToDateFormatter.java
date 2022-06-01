package io.github.ipagentpool.spider.fomatter;

/**
 * @author wengchengjian
 * @date 2022/6/1-13:28
 */
public class IP66StringToDateFormatter extends StringToDateFormatter{
    @Override
    public String getFormat() {
        return "yyyy年MM月dd日HH时 验证";
    }
}
