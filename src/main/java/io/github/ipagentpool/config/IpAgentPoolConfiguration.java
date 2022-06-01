package io.github.ipagentpool.config;

import io.github.ipagentpool.spider.processor.IpAgentPageProcessor;
import io.github.ipagentpool.spider.scheduler.IpAgentPoolScheduler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

