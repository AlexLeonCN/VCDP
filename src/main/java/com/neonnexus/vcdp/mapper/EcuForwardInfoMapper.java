package com.neonnexus.vcdp.mapper;

import com.neonnexus.vcdp.entity.po.EcuForwardInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EcuForwardInfoMapper {
    EcuForwardInfo findById(@Param("id") String id);

    List<EcuForwardInfo> findByProjectId(@Param("projectId") String projectId);

    EcuForwardInfo findByEcuId(@Param("ecuId") String ecuId);

    int insert(EcuForwardInfo forwardInfo);

    int deleteById(@Param("id") String id);

    int deleteByIds(@Param("ids") List<String> ids);

    int deleteByProjectId(@Param("projectId") String projectId);

    int deleteByProjectIds(@Param("projectIds") List<String> projectIds);

    int deleteByEcuId(@Param("ecuId") String ecuId);

    int deleteByEcuIds(@Param("ecuIds") List<String> ecuIds);
}
