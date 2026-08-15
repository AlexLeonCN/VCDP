package com.neonnexus.vcdm.entity.po.project.subnet;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SubnetCanInterface extends SubnetInterface {
    private Integer type; // 0-CAN 1-CANFD
    private Integer connectType; // 0-MCU直连CAN 1-LSW下挂CAN
    private Integer logicId; // 逻辑ID，用于跨版转发时ETH虚拟报文的msgId偏移
}
