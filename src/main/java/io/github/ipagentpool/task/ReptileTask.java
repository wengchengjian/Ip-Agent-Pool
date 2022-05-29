package io.github.ipagentpool.task;

import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.ipagentpool.config.AgentSiteProperties;
import io.github.ipagentpool.dto.IpAgentModelVo;
import io.github.ipagentpool.model.IpAgentModel;
import io.github.ipagentpool.pipeline.MysqlIpAgentPipeline;
import io.github.ipagentpool.processor.IpAgentPageProcessor;
import io.github.ipagentpool.service.IpAgentService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.checkerframework.common.reflection.qual.GetMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.util.concurrent.ListenableFuture;
import us.codecraft.webmagic.Spider;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static io.github.ipagentpool.utils.IpUtil.checkProxyIp;

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

        CompletableFuture.runAsync(spider,asyncTaskExecutor);

        long startTime = System.currentTimeMillis();

        while (true){
            try {
                Thread.sleep(1000);

                long spendTime = System.currentTimeMillis() -startTime;

                if(spendTime>=60*1000){
                    spider.stop();
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
    public Spider createSpider(){
        String[] collectSites = properties.getIpAgents().stream().map(AgentSiteProperties.IpAgent::getSiteName).collect(Collectors.toList()).toArray(new String[0]);

        return Spider.create(pageProcessor).addPipeline(pipeline).addUrl(collectSites).thread(DEFAULT_THREAD_NUM);
    }


}
