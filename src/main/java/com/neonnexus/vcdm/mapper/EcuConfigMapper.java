package com.neonnexus.vcdm.mapper;

import com.neonnexus.vcdm.entity.po.project.EcuCanInterface;
import com.neonnexus.vcdm.entity.po.project.EcuEthInterface;
import com.neonnexus.vcdm.entity.po.project.EcuForwardInfo;
import com.neonnexus.vcdm.entity.po.project.EcuLinInterface;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EcuConfigMapper {
    EcuForwardInfo findForwardInfoByEcuId(@Param("ecuId") Long ecuId);

    int insertForwardInfo(EcuForwardInfo forwardInfo);

    int deleteForwardInfoByEcuId(@Param("ecuId") Long ecuId);

    int deleteForwardInfoByEcuIds(@Param("ecuIds") List<Long> ecuIds);

    List<EcuCanInterface> findCanInterfacesByEcuId(@Param("ecuId") Long ecuId);

    int insertCanInterfaces(@Param("items") List<EcuCanInterface> items);

    int deleteCanInterfacesByEcuId(@Param("ecuId") Long ecuId);

    int deleteCanInterfacesByEcuIds(@Param("ecuIds") List<Long> ecuIds);

    List<EcuLinInterface> findLinInterfacesByEcuId(@Param("ecuId") Long ecuId);

    int insertLinInterfaces(@Param("items") List<EcuLinInterface> items);

    int deleteLinInterfacesByEcuId(@Param("ecuId") Long ecuId);

    int deleteLinInterfacesByEcuIds(@Param("ecuIds") List<Long> ecuIds);

    List<EcuEthInterface> findEthInterfacesByEcuId(@Param("ecuId") Long ecuId);

    int insertEthInterfaces(@Param("items") List<EcuEthInterface> items);

    int deleteEthInterfacesByEcuId(@Param("ecuId") Long ecuId);

    int deleteEthInterfacesByEcuIds(@Param("ecuIds") List<Long> ecuIds);
}
