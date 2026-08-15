package com.neonnexus.vcdm.entity.po.project.subnet;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SubnetLinInterface extends SubnetInterface {
    private Integer logicId; // 逻辑ID，用于跨版转发时ETH虚拟报文的msgId偏移
}
