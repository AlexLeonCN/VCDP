package com.neonnexus.vcdp.mapper;

import com.neonnexus.vcdp.entity.po.project.CanInterface;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CanInterfaceMapper {
    List<CanInterface> findByEcuId(@Param("ecuId") String ecuId);

    int insertBatch(@Param("items") List<CanInterface> items);

    int deleteByEcuId(@Param("ecuId") String ecuId);

    int deleteByEcuIds(@Param("ecuIds") List<String> ecuIds);
}
