package com.neonnexus.vcdm.service;

import com.neonnexus.vcdm.common.PageResult;
import com.neonnexus.vcdm.entity.po.project.Project;
import com.neonnexus.vcdm.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 12;
    private static final int MAX_SIZE = 60;

    private final ProjectMapper projectMapper;

    public PageResult<Project> listProjects(Integer page, Integer size) {
        int safePage = page == null || page < 1 ? DEFAULT_PAGE : page;
        int safeSize = size == null || size < 1 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        int offset = (safePage - 1) * safeSize;

        List<Project> records = projectMapper.findPage(offset, safeSize);
        long total = projectMapper.count();
        return new PageResult<>(records, total, safePage, safeSize);
    }

    public Project getProject(Long id) {
        return projectMapper.findById(id);
    }

    @Transactional
    public Project createProject(Project project) {
        normalize(project);
        projectMapper.insert(project);
        return project;
    }

    @Transactional
    public Project updateProject(Long id, Project project) {
        project.setId(id);
        normalize(project);
        projectMapper.update(project);
        return projectMapper.findById(id);
    }

    @Transactional
    public boolean deleteProject(Long id) {
        return projectMapper.deleteById(id) > 0;
    }

    @Transactional
    public int deleteProjects(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return projectMapper.deleteBatch(ids);
    }

    private void normalize(Project project) {
        if (project.getName() == null || project.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("工程名称不能为空");
        }
        project.setName(project.getName().trim());
        if (project.getDescription() != null) {
            String description = project.getDescription().trim();
            project.setDescription(description.isEmpty() ? null : description);
        }
        if (project.getTopoType() == null) {
            project.setTopoType(0);
        }
    }
}
