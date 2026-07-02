package com.neonnexus.vcdm.entity.dto;

import com.neonnexus.vcdm.entity.po.project.Ecu;
import com.neonnexus.vcdm.entity.po.project.EcuCanInterface;
import com.neonnexus.vcdm.entity.po.project.EcuEthInterface;
import com.neonnexus.vcdm.entity.po.project.EcuForwardInfo;
import com.neonnexus.vcdm.entity.po.project.EcuLinInterface;
import lombok.Data;

import java.util.List;

@Data
public class EcuConfig {
    private Ecu ecu;
    private EcuForwardInfo forwardInfo;
    private List<EcuCanInterface> canInterfaces;
    private List<EcuLinInterface> linInterfaces;
    private List<EcuEthInterface> ethInterfaces;
}
