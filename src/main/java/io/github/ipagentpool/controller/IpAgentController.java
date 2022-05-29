package io.github.ipagentpool.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.ipagentpool.common.Result;
import io.github.ipagentpool.dto.IpAgentModelVo;
import io.github.ipagentpool.service.IpAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author 翁丞健
 * @Date 2022/5/28 21:33
 * @Version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/ip")
@RequiredArgsConstructor
public class IpAgentController {

    private final IpAgentService ipAgentService;

    @GetMapping("/findIpByPage")
    public Result<Page<IpAgentModelVo>> findIpByPage(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                                     @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        try{
            return Result.Success(ipAgentService.findIpByPageVO(pageNum,pageSize));
        }catch (Exception e){
            log.error("查询代理ip失败,原因:{}",e.getMessage());
            return Result.Failure();
        }
    }
}
