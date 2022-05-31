package io.github.ipagentpool.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author 翁丞健
 * @Date 2022/5/28 21:36
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
public class Result<T> {
    private Integer code;

    private Boolean success;

    private String msg;

    private T data;

    public static <T> Result<T> Success(T data){
        return new Result<>(0,true,"调用成功",data);
    }

    public static <T> Result<T> Success(){
        return Success(null);
    }

    public static <T> Result<T> Failure(){
        return new Result<>(-1,false,"调用失败",null);
    }
}
