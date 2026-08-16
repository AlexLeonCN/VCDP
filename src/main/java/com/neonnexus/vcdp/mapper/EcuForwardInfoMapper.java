package com.neonnexus.vcdp.mapper;

import com.neonnexus.vcdp.entity.po.project.EcuForwardInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EcuForwardInfoMapper {
    EcuForwardInfo findByEcuId(@Param("ecuId") String ecuId);

    int insert(EcuForwardInfo forwardInfo);

    int deleteByEcuId(@Param("ecuId") String ecuId);

    int deleteByEcuIds(@Param("ecuIds") List<String> ecuIds);
}
