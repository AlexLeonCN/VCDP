package com.neonnexus.vcdp.controller;

import com.neonnexus.vcdp.common.PageResult;
import com.neonnexus.vcdp.common.Result;
import com.neonnexus.vcdp.entity.po.project.Project;
import com.neonnexus.vcdp.service.ProjectService;
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
    public Result<Project> getProject(@PathVariable String id) {
        return Result.success(projectService.getProject(id));
    }

    @PostMapping("/api/projects")
    public Result<Project> createProject(@RequestBody Project project) {
        return Result.success("工程创建成功", projectService.createProject(project));
    }

    @PutMapping("/api/projects/{id}")
    public Result<Project> updateProject(@PathVariable String id, @RequestBody Project project) {
        return Result.success("工程更新成功", projectService.updateProject(id, project));
    }

    @DeleteMapping("/api/projects/{id}")
    public Result<Void> deleteProject(@PathVariable String id) {
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
        private List<String> ids;
    }
}
