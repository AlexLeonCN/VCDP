package com.neonnexus.vcdm.entity.po.project;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class EcuCanInterface {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long projectId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ecuId;
    private String interfaceName;
    private Integer channelId;
    private Integer port;
    /** CAN接口类型：0-CAN，1-CANFD */
    private Integer type;
    /** CAN接口连接类型：0-MCU直连CAN，1-LSW下挂CAN */
    private Integer connType;
}
