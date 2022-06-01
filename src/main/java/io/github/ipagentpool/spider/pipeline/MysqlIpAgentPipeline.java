package io.github.ipagentpool.spider.pipeline;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.ipagentpool.model.IpAgentModel;
import io.github.ipagentpool.service.IpAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author 翁丞健
 * @Date 2022/5/28 21:59
 * @Version 1.0.0
 */
@Component
@RequiredArgsConstructor
public class MysqlIpAgentPipeline implements Pipeline {

    private final IpAgentService ipAgentService;

    @Override
    public synchronized void process(ResultItems resultItems, Task task) {
        List<IpAgentModel> models = resultItems.get("models");

        Set<IpAgentModel> saved = new HashSet<>();
        Set<IpAgentModel> updated = new HashSet<>();

        for (IpAgentModel model : models) {
            if(model!=null){
                IpAgentModel one = ipAgentService.getOne(new LambdaQueryWrapper<IpAgentModel>().eq(IpAgentModel::getIp, model.getIp()).eq(IpAgentModel::getPort, model.getPort()));

                if(one!=null){
                    model.setId(one.getId());
                    updated.add(model);
                }else{
                    saved.add(model);
                }
            }
        }
        ipAgentService.saveBatch(saved);
        ipAgentService.updateBatchById(updated);
    }
}
