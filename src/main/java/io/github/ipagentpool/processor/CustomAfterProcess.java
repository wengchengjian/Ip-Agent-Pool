package io.github.ipagentpool.processor;

import io.github.ipagentpool.model.IpAgentModel;

import java.util.List;

/**
 * @Author 翁丞健
 * @Date 2022/5/29 12:20
 * @Version 1.0.0
 */
public interface CustomAfterProcess {

    void process(List<IpAgentModel> models);
}
