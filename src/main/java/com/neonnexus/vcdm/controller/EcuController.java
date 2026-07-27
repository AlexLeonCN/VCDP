package com.neonnexus.vcdm.controller;

import com.neonnexus.vcdm.common.PageResult;
import com.neonnexus.vcdm.common.Result;
import com.neonnexus.vcdm.entity.dto.EcuConfig;
import com.neonnexus.vcdm.entity.po.project.Ecu;
import com.neonnexus.vcdm.service.EcuService;
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
public class EcuController {
    private final EcuService ecuService;

    @GetMapping("/api/projects/{projectId}/ecus")
    public Result<PageResult<Ecu>> listEcus(@PathVariable Long projectId,
                                            @RequestParam(required = false) Integer page,
                                            @RequestParam(required = false) Integer size) {
        return Result.success(ecuService.listEcus(projectId, page, size));
    }

    @GetMapping("/api/projects/{projectId}/ecus/{ecuId}")
    public Result<EcuConfig> getEcu(@PathVariable Long projectId, @PathVariable Long ecuId) {
        return Result.success(ecuService.getEcuConfig(projectId, ecuId));
    }

    @PostMapping("/api/projects/{projectId}/ecus")
    public Result<EcuConfig> createEcu(@PathVariable Long projectId, @RequestBody EcuConfig request) {
        return Result.success("ECU创建成功", ecuService.createEcu(projectId, request));
    }

    @PutMapping("/api/projects/{projectId}/ecus/{ecuId}")
    public Result<EcuConfig> updateEcu(@PathVariable Long projectId,
                                       @PathVariable Long ecuId,
                                       @RequestBody EcuConfig request) {
        return Result.success("ECU更新成功", ecuService.updateEcu(projectId, ecuId, request));
    }

    @DeleteMapping("/api/projects/{projectId}/ecus/{ecuId}")
    public Result<Void> deleteEcu(@PathVariable Long projectId, @PathVariable Long ecuId) {
        ecuService.deleteEcu(projectId, ecuId);
        return Result.success("ECU删除成功", null);
    }

    @PostMapping("/api/projects/{projectId}/ecus/batch-delete")
    public Result<Integer> deleteEcus(@PathVariable Long projectId,
                                      @RequestBody BatchDeleteRequest request) {
        int deletedCount = ecuService.deleteEcus(projectId, request.getIds());
        return Result.success("批量删除完成", deletedCount);
    }

    @Data
    public static class BatchDeleteRequest {
        private List<Long> ids;
    }
}
