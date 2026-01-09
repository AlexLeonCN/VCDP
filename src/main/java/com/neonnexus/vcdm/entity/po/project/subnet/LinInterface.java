package com.neonnexus.vcdm.entity.po.project.subnet;

import lombok.Data;

@Data
public class LinInterface extends Interface{
    private Integer logicId; // 逻辑ID，用于跨版转发时ETH虚拟报文的msgId偏移
}
