package io.github.ipagentpool.dto;

import io.github.ipagentpool.model.IpAgentModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 翁丞健
 * @Date 2022/5/28 19:33
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IpAgentModelVo {

    private Long id;

    private String ip;

    private Integer port;

    private String anonymity;


    private Integer score;

    private String address;

    public IpAgentModelVo(IpAgentModel model){
        this.id = model.getId();
        this.ip=model.getIp();
        this.port= model.getPort();
        this.anonymity=model.getAnonymity();
        this.address=model.getAddress();
        this.score = model.getScore();
    }
}
