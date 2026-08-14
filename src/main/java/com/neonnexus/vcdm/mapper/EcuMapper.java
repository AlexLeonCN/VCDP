package com.neonnexus.vcdm.mapper;

import com.neonnexus.vcdm.entity.po.project.Ecu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EcuMapper {
    List<Ecu> findPageByProjectId(@Param("projectId") String projectId,
                                  @Param("offset") int offset,
                                  @Param("size") int size);

    long countByProjectId(@Param("projectId") String projectId);

    Ecu findById(@Param("id") String id);

    List<String> findIdsByProjectIds(@Param("projectIds") List<String> projectIds);

    long countByProjectIdAndName(@Param("projectId") String projectId,
                                 @Param("name") String name,
                                 @Param("excludeId") String excludeId);

    long countByProjectIdAndMac(@Param("projectId") String projectId,
                                @Param("mac") String mac,
                                @Param("excludeId") String excludeId);

    long countByProjectIdAndIp(@Param("projectId") String projectId,
                               @Param("ip") String ip,
                               @Param("excludeId") String excludeId);

    long countByProjectIdAndPort(@Param("projectId") String projectId,
                                 @Param("port") Integer port,
                                 @Param("excludeId") String excludeId);

    long countByProjectIdAndIndex(@Param("projectId") String projectId,
                                  @Param("index") Integer index,
                                  @Param("excludeId") String excludeId);

    int insert(Ecu ecu);

    int update(Ecu ecu);

    int deleteById(@Param("id") String id);

    int deleteBatchByProjectId(@Param("projectId") String projectId, @Param("ids") List<String> ids);

    int deleteByProjectIds(@Param("projectIds") List<String> projectIds);

    List<String> findIdsByProjectId(@Param("projectId") String projectId, @Param("ids") List<String> ids);
}
