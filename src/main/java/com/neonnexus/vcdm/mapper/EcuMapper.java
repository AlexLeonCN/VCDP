package com.neonnexus.vcdm.mapper;

import com.neonnexus.vcdm.entity.po.project.Ecu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EcuMapper {
    List<Ecu> findPageByProjectId(@Param("projectId") Long projectId,
                                  @Param("offset") int offset,
                                  @Param("size") int size);

    long countByProjectId(@Param("projectId") Long projectId);

    Ecu findById(@Param("id") Long id);

    int insert(Ecu ecu);

    int update(Ecu ecu);

    int deleteById(@Param("id") Long id);

    int deleteBatchByProjectId(@Param("projectId") Long projectId, @Param("ids") List<Long> ids);

    List<Long> findIdsByProjectId(@Param("projectId") Long projectId, @Param("ids") List<Long> ids);
}
