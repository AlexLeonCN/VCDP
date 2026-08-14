package com.neonnexus.vcdm.entity.dto;

import com.neonnexus.vcdm.entity.po.project.Ecu;
import com.neonnexus.vcdm.entity.po.project.CanInterface;
import com.neonnexus.vcdm.entity.po.project.EthInterface;
import com.neonnexus.vcdm.entity.po.project.EcuForwardInfo;
import com.neonnexus.vcdm.entity.po.project.LinInterface;
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
