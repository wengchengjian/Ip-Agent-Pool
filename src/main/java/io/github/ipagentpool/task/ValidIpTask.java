package io.github.ipagentpool.task;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.ipagentpool.model.IpAgentModel;
import io.github.ipagentpool.service.IpAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static io.github.ipagentpool.utils.IpUtil.checkProxyIp;

/**
 * @Author 翁丞健
 * @Date 2022/5/29 20:56
 * @Version 1.0.0
 */
@Component
@RequiredArgsConstructor
public class ValidIpTask {
    private final IpAgentService ipAgentService;

    @Autowired
    @Qualifier("asyncTaskExcutor")
    private AsyncTaskExecutor asyncTaskExecutor;

    private volatile AtomicInteger num = new AtomicInteger(1);

    /**
     * 每1分钟一百条的去验证
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void validateIp1(){
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                checkIp();
            }
        },asyncTaskExecutor);
    }

    public void checkIp(){
        Page<IpAgentModel> ipByPage = ipAgentService.findIpByPage(num.getAndIncrement(), 100);

        if(!ipByPage.hasNext()){
            num.compareAndSet(num.intValue(),1);
        }

        List<IpAgentModel> records = ipByPage.getRecords();
        // 没有数据直接返回
        if(records==null){
            return;
        }
        List<IpAgentModel> valid = new ArrayList<>();
        List<IpAgentModel> invalid = new ArrayList<>();
        List<IpAgentModel> needRemoved = new ArrayList<>();

        for (IpAgentModel record : records) {

            if(checkProxyIp(record.getIp(),record.getPort())){
                // 限制最大分数
                if(record.getScore()<10){
                    valid.add(record);
                }
            }else{
                if(record.getScore()<=0){
                    needRemoved.add(record);
                }else{
                    invalid.add(record);
                }
            }
        }
        ipAgentService.incrementByScore(valid);
        ipAgentService.uncrementByScore(invalid);
        ipAgentService.removeByIds(needRemoved.stream().map(IpAgentModel::getId).collect(Collectors.toList()));
    }
}
