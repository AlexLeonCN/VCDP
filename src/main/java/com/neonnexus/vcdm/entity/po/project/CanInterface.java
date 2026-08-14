package com.neonnexus.vcdm.entity.po.project;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CanInterface extends EcuInterface {
    /** CAN接口类型：0-CAN，1-CANFD */
    private Integer type;
    /** CAN接口连接类型：0-MCU直连CAN，1-LSW下挂CAN */
    private Integer connType;
}
