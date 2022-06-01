package io.github.ipagentpool.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.ipagentpool.model.IpAgentModel;
import io.github.ipagentpool.service.IpAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static io.github.ipagentpool.utils.IpUtil.checkProxyIp;

/**
 * @Author 翁丞健
 * @Date 2022/5/29 20:56
 * @Version 1.0.0
 */
@Slf4j
@Component
public class ValidIpTask {
    @Autowired
    private  IpAgentService ipAgentService;

    @Autowired
    @Qualifier("asyncTaskExcutor")
    private AsyncTaskExecutor asyncTaskExecutor;

    private final AtomicInteger num = new AtomicInteger(1);

    private static final Integer MAX_SCORE = 100;

    private final Map<Integer,Object> locks= new ConcurrentHashMap<>(64);

    /**
     * 每1分钟一百条的去验证
     */
    @Async
    @Scheduled(cron = "0 0/1 * * * ?")
    public void validateIp1(){
        checkIp();
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void removeIp(){
        boolean remove = ipAgentService.remove(new QueryWrapper<IpAgentModel>().lambda().le(IpAgentModel::getScore, 0));
        if(remove){
            log.info("removed a batch zero score ip success");
        }else{
            log.error("removed a batch zero score ip failed");
        }
    }

    public void checkIp(){
        int getNum = num.getAndIncrement();
        creatLock(getNum);
        synchronized (locks.get(getNum)){
            Page<IpAgentModel> ipByPage = ipAgentService.findIpByPage(getNum, 20);

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
            List<Long> needRemoved = new ArrayList<>();

            for (IpAgentModel record : records) {

                if(checkProxyIp(record.getIp(),record.getPort())){
                    // 限制最大分数
                    if(record.getScore() < MAX_SCORE){
                        valid.add(record);
                    }
                }else{
                    invalid.add(record);
                }
            }
            incrementByScore(valid);
            uncrementByScore(invalid);
            valid.addAll(invalid);
            ipAgentService.saveOrUpdateBatch(valid);
        }
    }

    public void incrementByScore(List<IpAgentModel> valid) {

        valid.forEach(item->{
            item.setScore(item.getScore()+1);
        });
    }

    public void uncrementByScore(List<IpAgentModel> invalid) {
        invalid.forEach(item->{
            item.setScore(item.getScore()-10);
        });
    }

    private final Object lock = new Object();

    private void creatLock(int num) {
        if(locks.get(num)==null){
            synchronized (lock){
                locks.computeIfAbsent(num, k -> new Object());
            }
        }
    }
}
