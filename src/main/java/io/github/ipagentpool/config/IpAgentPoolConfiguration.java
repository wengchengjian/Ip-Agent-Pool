package io.github.ipagentpool.config;

import io.github.ipagentpool.pipeline.MysqlIpAgentPipeline;
import io.github.ipagentpool.processor.IpAgentPageProcessor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import us.codecraft.webmagic.Spider;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author 翁丞健
 * @Date 2022/5/28 19:36
 * @Version 1.0.0
 */
@Configuration
@EnableConfigurationProperties(AgentSiteProperties.class)
public class IpAgentPoolConfiguration {


    @Bean
    public IpAgentPageProcessor ipAgentPageProcessor(AgentSiteProperties properties){
        return new IpAgentPageProcessor(properties);
    }
}

