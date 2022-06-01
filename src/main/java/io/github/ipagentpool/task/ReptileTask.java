package io.github.ipagentpool.task;

import io.github.ipagentpool.config.AgentSiteProperties;
import io.github.ipagentpool.spider.pipeline.MysqlIpAgentPipeline;
import io.github.ipagentpool.spider.processor.IpAgentPageProcessor;
import io.github.ipagentpool.spider.scheduler.IpAgentPoolScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

import java.util.stream.Collectors;

/**
 * @Author 翁丞健
 * @Date 2022/5/28 22:02
 * @Version 1.0.0
 */
@Component
@RequiredArgsConstructor
public class ReptileTask {

    private final AgentSiteProperties properties;

    private final IpAgentPageProcessor pageProcessor;

    private final MysqlIpAgentPipeline pipeline;

    private static final Integer DEFAULT_THREAD_NUM = 5;

    @Autowired
    @Qualifier("asyncTaskExcutor")
    private  AsyncTaskExecutor asyncTaskExecutor;

    private Integer num = 0;

    @Scheduled(cron = "0 0/2 * * * ?")
    public void crawlSite(){
        Spider spider = createSpider();
        spider.run();
    }
    public Spider createSpider(){
        String[] collectSites = properties.getIpAgents().stream().map(AgentSiteProperties.IpAgent::getSiteName).collect(Collectors.toList()).toArray(new String[0]);

        return Spider.create(pageProcessor).addPipeline(pipeline).setScheduler(new IpAgentPoolScheduler()).addUrl(collectSites).thread(DEFAULT_THREAD_NUM);
    }


}
