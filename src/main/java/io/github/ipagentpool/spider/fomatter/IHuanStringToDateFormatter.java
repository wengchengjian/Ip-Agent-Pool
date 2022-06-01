package io.github.ipagentpool.spider.fomatter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

/**
 * @author wengchengjian
 * @date 2022/6/1-11:27
 */
public class IHuanStringToDateFormatter extends StringToDateFormatter{

    @Override
    public String preProcess(String s) throws Exception {

        ChronoUnit unit = getUnit(s);

        long val = compute(s);

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime minus = now.minus(val, unit);

        return minus.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    private long compute(String s) {
        if(s.contains("秒")){
            return Integer.parseInt(s.substring(0,s.indexOf("秒")));
        }else if(s.contains("分钟")){
            return Integer.parseInt(s.substring(0,s.indexOf("分钟")));
        }else if(s.contains("小时")){
            return Integer.parseInt(s.substring(0,s.indexOf("小时")));
        }else if(s.contains("天")){
            return Integer.parseInt(s.substring(0,s.indexOf("天")));
        }else if(s.contains("个月")){
            return Integer.parseInt(s.substring(0,s.indexOf("个月")));
        }else {
            return Integer.parseInt(s.substring(0,s.indexOf("年")));
        }
    }

    private ChronoUnit getUnit(String s) {
        if(s.contains("秒")){
            return ChronoUnit.SECONDS;
        }else if(s.contains("分钟")){
            return ChronoUnit.MINUTES;
        }else if(s.contains("小时")){
            return ChronoUnit.HOURS;
        }else if(s.contains("天")){
            return ChronoUnit.DAYS;
        }else if(s.contains("个月")){
            return ChronoUnit.MONTHS;
        }else {
            return ChronoUnit.YEARS;
        }

    }

}
