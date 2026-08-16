package com.neonnexus.vcdp.entity.dto;

import com.neonnexus.vcdp.entity.po.project.Ecu;
import com.neonnexus.vcdp.entity.po.project.CanInterface;
import com.neonnexus.vcdp.entity.po.project.EthInterface;
import com.neonnexus.vcdp.entity.po.project.EcuForwardInfo;
import com.neonnexus.vcdp.entity.po.project.LinInterface;
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
