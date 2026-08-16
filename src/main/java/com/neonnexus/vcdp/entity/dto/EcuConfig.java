package com.neonnexus.vcdp.entity.dto;

import com.neonnexus.vcdp.entity.po.interfaces.CanInterface;
import com.neonnexus.vcdp.entity.po.interfaces.EthInterface;
import com.neonnexus.vcdp.entity.po.interfaces.LinInterface;
import com.neonnexus.vcdp.entity.po.Ecu;
import com.neonnexus.vcdp.entity.po.EcuForwardInfo;
import lombok.Data;

import java.util.List;

@Data
public class EcuConfig {
    private Ecu ecu;
    private EcuForwardInfo forwardInfo;
    private List<CanInterface> canInterfaces;
    private List<LinInterface> linInterfaces;
    private List<EthInterface> ethInterfaces;
}
