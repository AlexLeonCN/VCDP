package com.neonnexus.vcdm.controller;

import com.neonnexus.vcdm.common.PageResult;
import com.neonnexus.vcdm.common.Result;
import com.neonnexus.vcdm.entity.po.project.Project;
import com.neonnexus.vcdm.service.ProjectService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/api/projects")
    public Result<PageResult<Project>> listProjects(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return Result.success(projectService.listProjects(page, size));
    }

    @GetMapping("/api/projects/{id}")
    public Result<Project> getProject(@PathVariable Long id) {
        return Result.success(projectService.getProject(id));
    }

    @PostMapping("/api/projects")
    public Result<Project> createProject(@RequestBody Project project) {
        return Result.success("工程创建成功", projectService.createProject(project));
    }

    @PutMapping("/api/projects/{id}")
    public Result<Project> updateProject(@PathVariable Long id, @RequestBody Project project) {
        return Result.success("工程更新成功", projectService.updateProject(id, project));
    }

    @DeleteMapping("/api/projects/{id}")
    public Result<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return Result.success("工程删除成功", null);
    }

    @PostMapping("/api/projects/batch-delete")
    public Result<Integer> deleteProjects(@RequestBody BatchDeleteRequest request) {
        int deletedCount = projectService.deleteProjects(request.getIds());
        return Result.success("批量删除完成", deletedCount);
    }

    @Data
    public static class BatchDeleteRequest {
        private List<Long> ids;
    }
}
