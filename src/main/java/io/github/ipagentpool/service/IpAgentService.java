package io.github.ipagentpool.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.ipagentpool.dto.IpAgentModelVo;
import io.github.ipagentpool.model.IpAgentModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author 翁丞健
 * @Date 2022/5/28 21:16
 * @Version 1.0.0
 */
public interface IpAgentService extends IService<IpAgentModel> {
    Page<IpAgentModelVo> findIpByPageVO(Integer pageNum, Integer pageSize);

    Page<IpAgentModel> findIpByPage(Integer pageNum, Integer pageSize);

}
