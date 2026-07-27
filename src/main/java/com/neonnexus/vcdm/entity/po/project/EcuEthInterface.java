package com.neonnexus.vcdm.entity.po.project;

import lombok.Data;

@Data
public class EcuEthInterface {
    private Long id;
    private Long projectId;
    private Long ecuId;
    private String interfaceName;
    private Integer channelId;
    private Integer port;
    /** ETH接口类型：0-百兆，1-千兆 */
    private Integer type;
}
