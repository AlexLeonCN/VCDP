package com.neonnexus.vcdp.mapper;

import com.neonnexus.vcdp.entity.po.project.LinInterface;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LinInterfaceMapper {
    List<LinInterface> findByEcuId(@Param("ecuId") String ecuId);

    int insertBatch(@Param("items") List<LinInterface> items);

    int deleteByEcuId(@Param("ecuId") String ecuId);

    int deleteByEcuIds(@Param("ecuIds") List<String> ecuIds);
}
