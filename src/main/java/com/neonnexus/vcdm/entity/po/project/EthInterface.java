package com.neonnexus.vcdm.entity.po.project;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EthInterface extends EcuInterface {
    /** ETH接口类型：0-百兆，1-千兆 */
    private Integer type;
}
