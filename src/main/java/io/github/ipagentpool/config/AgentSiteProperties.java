package io.github.ipagentpool.config;

import io.github.ipagentpool.common.AcquireType;
import io.github.ipagentpool.model.IpAgentModel;
import io.github.ipagentpool.spider.fomatter.Fomatter;
import io.github.ipagentpool.spider.fomatter.StringToDateFormatter;
import io.github.ipagentpool.spider.fomatter.StringToIntegerFomatter;
import io.github.ipagentpool.spider.fomatter.StringToStringFomatter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author 翁丞健
 * @Date 2022/5/28 19:35
 * @Version 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "agent")
public class AgentSiteProperties {

    /**
     * ip代理检测次数
     */
    private Integer retryTimes;

    /**
     * siteName->IpAgent
     * 第三方免费ip代理网站
     */
    private List<IpAgent> ipAgents;

    @Data
    public static class IpAgent{

        private AcquireType acquireType = AcquireType.Active;

        private String acquireProxyUrl;

        private String username;

        private String password;

        private String host;

        private String tokenUrl;

        /**
         * 第三方免费ip代理网站名
         */
        private String siteName;

        /**
         * 解析到表格中某一行的规则，支持xpath，css，regex
         */
        private String analyzeTr;

        /**
         * 字段在表格某行的位置
         * 0->ip,1->port,2->anonymity,3->address
         */
        private Map<String,Integer> position = new ConcurrentHashMap<>(){{
            put("ip",1);
            put("port",2);
            put("anonymity",3);
            put("address",4);
            put("updateTime",5);
        }};

        /**
         * 用于捕捉下一条url的正则
         */
        private String nextLinkRegex;

        private Map<String,Rule> rules = new ConcurrentHashMap<>(){{
            put("ip",Rule.DEFAULT_STRING_RULE);
            put("port",Rule.DEFAULT_INTEGER_RULE);
            put("address",Rule.DEFAULT_STRING_RULE);
            put("updateTime",Rule.DEFAULT_DATE_RULE);
            put("anonymity",Rule.DEFAULT_STRING_RULE);}};

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Rule<T>{
            /**
             * 提取规则
             */
            private String rule = "/text()";

            /**
             * 是否多个
             */
            private boolean multi = false;

            /**
             * 在multi为true的情况下生效，用于连接多个结果
             */
            private String separator = "-";

            /**
             * 该字段的默认值
             */
            private T defaultValue;

            private Class fomatter = StringToStringFomatter.class;

            public static final Rule<String> DEFAULT_STRING_RULE = new Rule("/text()",false,null,null,StringToStringFomatter.class);

            public static final Rule<Date> DEFAULT_DATE_RULE = new Rule("/text()",false,null,new Date(), StringToDateFormatter.class);

            public static final Rule<Integer> DEFAULT_INTEGER_RULE = new Rule("/text()",false,null,80, StringToIntegerFomatter.class);
        }


    }

}
