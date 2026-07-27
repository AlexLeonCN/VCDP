package com.neonnexus.vcdm.mapper;

import com.neonnexus.vcdm.entity.po.project.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectMapper {
    List<Project> findPage(@Param("offset") int offset, @Param("size") int size);

    long count();

    Project findById(@Param("id") Long id);

    long countByName(@Param("name") String name, @Param("excludeId") Long excludeId);

    int insert(Project project);

    int update(Project project);

    int deleteById(@Param("id") Long id);

    int deleteBatch(@Param("ids") List<Long> ids);
}
