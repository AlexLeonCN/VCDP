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
    EcuForwardInfo findForwardInfoByEcuId(@Param("ecuId") String ecuId);

    int insertForwardInfo(EcuForwardInfo forwardInfo);

    int deleteForwardInfoByEcuId(@Param("ecuId") String ecuId);

    int deleteForwardInfoByEcuIds(@Param("ecuIds") List<String> ecuIds);

    List<EcuCanInterface> findCanInterfacesByEcuId(@Param("ecuId") String ecuId);

    int insertCanInterfaces(@Param("items") List<EcuCanInterface> items);

    int deleteCanInterfacesByEcuId(@Param("ecuId") String ecuId);

    int deleteCanInterfacesByEcuIds(@Param("ecuIds") List<String> ecuIds);

    List<EcuLinInterface> findLinInterfacesByEcuId(@Param("ecuId") String ecuId);

    int insertLinInterfaces(@Param("items") List<EcuLinInterface> items);

    int deleteLinInterfacesByEcuId(@Param("ecuId") String ecuId);

    int deleteLinInterfacesByEcuIds(@Param("ecuIds") List<String> ecuIds);

    List<EcuEthInterface> findEthInterfacesByEcuId(@Param("ecuId") String ecuId);

    int insertEthInterfaces(@Param("items") List<EcuEthInterface> items);

    int deleteEthInterfacesByEcuId(@Param("ecuId") String ecuId);

    int deleteEthInterfacesByEcuIds(@Param("ecuIds") List<String> ecuIds);
}
