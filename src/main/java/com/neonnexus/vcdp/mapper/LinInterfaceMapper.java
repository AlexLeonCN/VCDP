package com.neonnexus.vcdp.mapper;

import com.neonnexus.vcdp.entity.po.project.LinInterface;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LinInterfaceMapper {
    LinInterface findById(@Param("id") String id);

    List<LinInterface> findByProjectId(@Param("projectId") String projectId);

    List<LinInterface> findByEcuId(@Param("ecuId") String ecuId);

    int insertBatch(@Param("items") List<LinInterface> items);

    int deleteById(@Param("id") String id);

    int deleteByIds(@Param("ids") List<String> ids);

    int deleteByProjectId(@Param("projectId") String projectId);

    int deleteByProjectIds(@Param("projectIds") List<String> projectIds);

    int deleteByEcuId(@Param("ecuId") String ecuId);

    int deleteByEcuIds(@Param("ecuIds") List<String> ecuIds);
}
