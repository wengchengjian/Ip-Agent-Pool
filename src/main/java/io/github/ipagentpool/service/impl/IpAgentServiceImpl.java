package io.github.ipagentpool.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.ipagentpool.dto.IpAgentModelVo;
import io.github.ipagentpool.mapper.IpAgentModelMapper;
import io.github.ipagentpool.model.IpAgentModel;
import io.github.ipagentpool.service.IpAgentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author 翁丞健
 * @Date 2022/5/28 21:31
 * @Version 1.0.0
 */
@Service
public class IpAgentServiceImpl extends ServiceImpl<IpAgentModelMapper,IpAgentModel> implements IpAgentService {

    @Override
    public Page<IpAgentModelVo> findIpByPageVO(Integer pageNum, Integer pageSize) {
        Page<IpAgentModel> page = new Page<>(pageNum,pageSize);
        this.baseMapper.selectPage(page, new LambdaQueryWrapper<IpAgentModel>().ge(IpAgentModel::getScore,5).orderByDesc(IpAgentModel::getScore));

        List<IpAgentModelVo> res = new ArrayList<>();
        for (IpAgentModel record : page.getRecords()) {
            res.add(new IpAgentModelVo(record));
        }
        Page<IpAgentModelVo> ipAgentModelVoPage = new Page<>(pageNum,pageSize,page.getTotal());
        ipAgentModelVoPage.setRecords(res);
        return ipAgentModelVoPage;
    }

    @Override
    public Page<IpAgentModel> findIpByPage(Integer pageNum, Integer pageSize) {
        Page<IpAgentModel> page = new Page<>(pageNum,pageSize);
        this.baseMapper.selectPage(page, new LambdaQueryWrapper<IpAgentModel>().ge(IpAgentModel::getScore,5).orderByDesc(IpAgentModel::getScore));
        return page;
    }


    @Override
    public void incrementByScore(List<IpAgentModel> valid) {

        valid.forEach(item->{
            item.setScore(item.getScore()+1);
        });
        saveOrUpdateBatch(valid);
    }

    @Override
    public void uncrementByScore(List<IpAgentModel> invalid) {
        invalid.forEach(item->{
            item.setScore(item.getScore()-1);
        });
        saveOrUpdateBatch(invalid);
    }
}
