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

    List<Long> findIdsByProjectIds(@Param("projectIds") List<Long> projectIds);

    long countByProjectIdAndName(@Param("projectId") Long projectId,
                                 @Param("name") String name,
                                 @Param("excludeId") Long excludeId);

    long countByProjectIdAndMac(@Param("projectId") Long projectId,
                                @Param("mac") String mac,
                                @Param("excludeId") Long excludeId);

    long countByProjectIdAndIp(@Param("projectId") Long projectId,
                               @Param("ip") String ip,
                               @Param("excludeId") Long excludeId);

    long countByProjectIdAndPort(@Param("projectId") Long projectId,
                                 @Param("port") Integer port,
                                 @Param("excludeId") Long excludeId);

    long countByProjectIdAndIndex(@Param("projectId") Long projectId,
                                  @Param("index") Integer index,
                                  @Param("excludeId") Long excludeId);

    int insert(Ecu ecu);

    int update(Ecu ecu);

    int deleteById(@Param("id") Long id);

    int deleteBatchByProjectId(@Param("projectId") Long projectId, @Param("ids") List<Long> ids);

    int deleteByProjectIds(@Param("projectIds") List<Long> projectIds);

    List<Long> findIdsByProjectId(@Param("projectId") Long projectId, @Param("ids") List<Long> ids);
}
