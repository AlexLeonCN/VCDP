package com.neonnexus.vcdp.mapper;

import com.neonnexus.vcdp.entity.po.project.EthInterface;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EthInterfaceMapper {
    List<EthInterface> findByEcuId(@Param("ecuId") String ecuId);

    int insertBatch(@Param("items") List<EthInterface> items);

    int deleteByEcuId(@Param("ecuId") String ecuId);

    int deleteByEcuIds(@Param("ecuIds") List<String> ecuIds);
}
