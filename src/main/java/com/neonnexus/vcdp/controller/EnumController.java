package com.neonnexus.vcdp.controller;

import com.neonnexus.vcdp.common.Result;
import com.neonnexus.vcdp.common.enumation.CanInterfaceTypeEnum;
import com.neonnexus.vcdp.common.enumation.EcuCanInterfaceTypeEnum;
import com.neonnexus.vcdp.common.enumation.EthPortTypeEnum;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class EnumController {

    @GetMapping("/api/enums/can-interface-types")
    public Result<List<EnumOption>> listCanInterfaceTypes() {
        return Result.success(Arrays.stream(CanInterfaceTypeEnum.values())
                .map(item -> new EnumOption(item.getCode(), item.getLabel()))
                .collect(Collectors.toList()));
    }

    @GetMapping("/api/enums/can-conn-types")
    public Result<List<EnumOption>> listCanConnTypes() {
        return Result.success(Arrays.stream(EcuCanInterfaceTypeEnum.values())
                .map(item -> new EnumOption(item.getCode(), item.getLabel()))
                .collect(Collectors.toList()));
    }

    @GetMapping("/api/enums/eth-interface-types")
    public Result<List<EnumOption>> listEthInterfaceTypes() {
        return Result.success(Arrays.stream(EthPortTypeEnum.values())
                .map(item -> new EnumOption(item.getCode(), item.getLabel()))
                .collect(Collectors.toList()));
    }

    @Data
    public static class EnumOption {
        private final Integer code;
        private final String name;
    }
}
