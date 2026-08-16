package com.neonnexus.vcdp.mapper;

import com.neonnexus.vcdp.entity.po.project.CanInterface;
import com.neonnexus.vcdp.entity.po.project.EthInterface;
import com.neonnexus.vcdp.entity.po.project.EcuForwardInfo;
import com.neonnexus.vcdp.entity.po.project.LinInterface;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EcuConfigMapper {
    EcuForwardInfo findForwardInfoByEcuId(@Param("ecuId") String ecuId);

    int insertForwardInfo(EcuForwardInfo forwardInfo);

    int deleteForwardInfoByEcuId(@Param("ecuId") String ecuId);

    int deleteForwardInfoByEcuIds(@Param("ecuIds") List<String> ecuIds);

    List<CanInterface> findCanInterfacesByEcuId(@Param("ecuId") String ecuId);

    int insertCanInterfaces(@Param("items") List<CanInterface> items);

    int deleteCanInterfacesByEcuId(@Param("ecuId") String ecuId);

    int deleteCanInterfacesByEcuIds(@Param("ecuIds") List<String> ecuIds);

    List<LinInterface> findLinInterfacesByEcuId(@Param("ecuId") String ecuId);

    int insertLinInterfaces(@Param("items") List<LinInterface> items);

    int deleteLinInterfacesByEcuId(@Param("ecuId") String ecuId);

    int deleteLinInterfacesByEcuIds(@Param("ecuIds") List<String> ecuIds);

    List<EthInterface> findEthInterfacesByEcuId(@Param("ecuId") String ecuId);

    int insertEthInterfaces(@Param("items") List<EthInterface> items);

    int deleteEthInterfacesByEcuId(@Param("ecuId") String ecuId);

    int deleteEthInterfacesByEcuIds(@Param("ecuIds") List<String> ecuIds);
}
