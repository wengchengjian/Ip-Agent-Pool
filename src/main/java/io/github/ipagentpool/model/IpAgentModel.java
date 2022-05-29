package io.github.ipagentpool.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import us.codecraft.webmagic.model.annotation.TargetUrl;

import java.util.Date;

/**
 * @Author 翁丞健
 * @Date 2022/5/28 19:33
 * @Version 1.0.0
 */
@Data
@TableName("ip_agent")
@NoArgsConstructor
@AllArgsConstructor
public class IpAgentModel {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    public IpAgentModel(String ip, Integer port, String anonymity, String address) {
        this.ip = ip;
        this.port = port;
        this.anonymity = anonymity;
        this.address = address;
    }

    private String ip;

    private Integer port;

    private String anonymity;

    private String address;

    private Integer score;


    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @TableLogic
    private Long deleted;

}
