package io.github.ipagentpool.spider.fomatter;

/**
 * @author wengchengjian
 * @date 2022/6/1-11:08
 */
public interface Fomatter<X,Y> {

    /**
     * 类型转换
     * @param x
     * @return
     */
     Y format(X x) throws Exception;

    /**
     *
     * @param x
     * @return
     * @throws Exception
     */
    default Y doFormat(X x) throws Exception{
       X val = preProcess(x);
       return format(val);
    }


    /**
     *
     * @param x
     * @return
     * @throws Exception
     */
    X preProcess(X x) throws Exception;
}
