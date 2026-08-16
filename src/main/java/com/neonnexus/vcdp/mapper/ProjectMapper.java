package com.neonnexus.vcdp.mapper;

import com.neonnexus.vcdp.entity.po.project.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectMapper {
    List<Project> findPage(@Param("offset") int offset, @Param("size") int size);

    long count();

    Project findById(@Param("id") String id);

    long countByName(@Param("name") String name, @Param("excludeId") String excludeId);

    int insert(Project project);

    int update(Project project);

    int deleteById(@Param("id") String id);

    int deleteByIds(@Param("ids") List<String> ids);
}
