package com.neonnexus.vcdp.service;

import com.neonnexus.vcdp.common.ErrorConstant;
import com.neonnexus.vcdp.common.PageResult;
import com.neonnexus.vcdp.entity.po.project.Project;
import com.neonnexus.vcdp.exception.VCDPException;
import com.neonnexus.vcdp.mapper.CanInterfaceMapper;
import com.neonnexus.vcdp.mapper.EcuForwardInfoMapper;
import com.neonnexus.vcdp.mapper.EcuMapper;
import com.neonnexus.vcdp.mapper.EthInterfaceMapper;
import com.neonnexus.vcdp.mapper.LinInterfaceMapper;
import com.neonnexus.vcdp.mapper.ProjectMapper;
import com.neonnexus.vcdp.util.SnowflakeIdGenerator;
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
    private final EcuMapper ecuMapper;
    private final EcuForwardInfoMapper ecuForwardInfoMapper;
    private final CanInterfaceMapper canInterfaceMapper;
    private final LinInterfaceMapper linInterfaceMapper;
    private final EthInterfaceMapper ethInterfaceMapper;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    public PageResult<Project> listProjects(Integer page, Integer size) {
        int safePage = page == null || page < 1 ? DEFAULT_PAGE : page;
        int safeSize = size == null || size < 1 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        int offset = (safePage - 1) * safeSize;

        List<Project> records = projectMapper.findPage(offset, safeSize);
        long total = projectMapper.count();
        return new PageResult<>(records, total, safePage, safeSize);
    }

    public Project getProject(String id) {
        Project project = projectMapper.findById(id);
        if (project == null) {
            throw new VCDPException(ErrorConstant.Project.NOT_FOUND);
        }
        return project;
    }

    @Transactional
    public Project createProject(Project project) {
        normalize(project, null);
        project.setId(snowflakeIdGenerator.nextId());
        projectMapper.insert(project);
        return projectMapper.findById(project.getId());
    }

    @Transactional
    public Project updateProject(String id, Project project) {
        Project existing = projectMapper.findById(id);
        if (existing == null) {
            throw new VCDPException(ErrorConstant.Project.NOT_FOUND);
        }
        project.setId(id);
        normalize(project, id);
        projectMapper.update(project);
        return projectMapper.findById(id);
    }

    @Transactional
    public boolean deleteProject(String id) {
        Project existing = projectMapper.findById(id);
        if (existing == null) {
            throw new VCDPException(ErrorConstant.Project.NOT_FOUND);
        }
        cascadeDeleteEcus(Collections.singletonList(id));
        return projectMapper.deleteById(id) > 0;
    }

    @Transactional
    public int deleteProjects(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        cascadeDeleteEcus(ids);
        return projectMapper.deleteByIds(ids);
    }

    private void cascadeDeleteEcus(List<String> projectIds) {
        ecuForwardInfoMapper.deleteByProjectIds(projectIds);
        canInterfaceMapper.deleteByProjectIds(projectIds);
        linInterfaceMapper.deleteByProjectIds(projectIds);
        ethInterfaceMapper.deleteByProjectIds(projectIds);
        ecuMapper.deleteByProjectIds(projectIds);
    }

    private void normalize(Project project, String excludeId) {
        if (project == null || project.getName() == null || project.getName().trim().isEmpty()) {
            throw new VCDPException(ErrorConstant.Project.NAME_EMPTY);
        }
        project.setName(project.getName().trim());
        if (projectMapper.countByName(project.getName(), excludeId) > 0) {
            throw new VCDPException(ErrorConstant.Project.NAME_DUPLICATE);
        }
        if (project.getDescription() != null) {
            String description = project.getDescription().trim();
            project.setDescription(description.isEmpty() ? null : description);
        }
    }
}
