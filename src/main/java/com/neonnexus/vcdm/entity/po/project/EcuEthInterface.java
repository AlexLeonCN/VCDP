package com.neonnexus.vcdm.entity.po.project;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class EcuEthInterface {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long projectId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ecuId;
    private String interfaceName;
    private Integer channelId;
    private Integer port;
    /** ETH接口类型：0-百兆，1-千兆 */
    private Integer type;
}
