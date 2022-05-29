package io.github.ipagentpool.utils;

/**
 * @Author 翁丞健
 * @Date 2022/5/29 15:38
 * @Version 1.0.0
 */
public class StringUtils {
    /**
     * 首字母大写(进行字母的ascii编码前移，效率是最高的)
     *
     * @param fieldName 需要转化的字符串
     */
    public static String initialUpper(String fieldName) {
        char[] chars = fieldName.toCharArray();
        chars[0] = toUpperCase(chars[0]);
        return String.valueOf(chars);
    }

    public static String getSetter(String field){
        return "set"+initialUpper(field);
    }


    /**
     * 字符转成大写
     *
     * @param c 需要转化的字符
     */
    public static char toUpperCase(char c) {
        if (97 <= c && c <= 122) {
            c ^= 32;
        }
        return c;
    }

}
