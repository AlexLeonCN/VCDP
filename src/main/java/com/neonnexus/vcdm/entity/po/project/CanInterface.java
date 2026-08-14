package com.neonnexus.vcdm.entity.po.project;

import lombok.Data;

@Data
public class CanInterface {
    private String id;
    private String projectId;
    private String ecuId;
    private String interfaceName;
    private Integer channelId;
    private Integer port;
    /** CAN接口类型：0-CAN，1-CANFD */
    private Integer type;
    /** CAN接口连接类型：0-MCU直连CAN，1-LSW下挂CAN */
    private Integer connType;
}
