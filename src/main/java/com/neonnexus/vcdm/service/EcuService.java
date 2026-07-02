package com.neonnexus.vcdm.service;

import com.neonnexus.vcdm.common.PageResult;
import com.neonnexus.vcdm.common.enumation.EcuCanInterfaceTypeEnum;
import com.neonnexus.vcdm.common.enumation.EthPortTypeEnum;
import com.neonnexus.vcdm.entity.dto.EcuConfig;
import com.neonnexus.vcdm.entity.po.project.Ecu;
import com.neonnexus.vcdm.entity.po.project.EcuCanInterface;
import com.neonnexus.vcdm.entity.po.project.EcuEthInterface;
import com.neonnexus.vcdm.entity.po.project.EcuForwardInfo;
import com.neonnexus.vcdm.entity.po.project.EcuLinInterface;
import com.neonnexus.vcdm.mapper.EcuConfigMapper;
import com.neonnexus.vcdm.mapper.EcuMapper;
import com.neonnexus.vcdm.util.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class EcuService {
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 12;
    private static final int MAX_SIZE = 60;
    private static final Pattern HEX_PATTERN = Pattern.compile("^[0-9a-fA-F]+$");
    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^(25[0-5]|2[0-4]\\d|1?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|1?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|1?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|1?\\d?\\d)$");

    private final EcuMapper ecuMapper;
    private final EcuConfigMapper ecuConfigMapper;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    public PageResult<Ecu> listEcus(Long projectId, Integer page, Integer size) {
        if (projectId == null) {
            return new PageResult<>(Collections.emptyList(), 0, DEFAULT_PAGE, DEFAULT_SIZE);
        }
        int safePage = page == null || page < 1 ? DEFAULT_PAGE : page;
        int safeSize = size == null || size < 1 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        int offset = (safePage - 1) * safeSize;

        List<Ecu> records = ecuMapper.findPageByProjectId(projectId, offset, safeSize);
        long total = ecuMapper.countByProjectId(projectId);
        return new PageResult<>(records, total, safePage, safeSize);
    }

    public EcuConfig getEcuConfig(Long projectId, Long ecuId) {
        Ecu ecu = ecuMapper.findById(ecuId);
        if (ecu == null || !Objects.equals(ecu.getProjectId(), projectId)) {
            return null;
        }
        return buildEcuConfig(ecu);
    }

    @Transactional
    public EcuConfig createEcu(Long projectId, EcuConfig request) {
        Ecu ecu = requireEcu(request);
        EcuForwardInfo forwardInfo = requireForwardInfo(request);

        normalizeEcu(ecu, projectId, true);
        ecuMapper.insert(ecu);

        normalizeForwardInfo(forwardInfo, projectId, ecu.getId(), true);
        ecuConfigMapper.insertForwardInfo(forwardInfo);

        List<EcuCanInterface> canInterfaces = normalizeCanInterfaces(request.getCanInterfaces(), projectId, ecu.getId());
        List<EcuLinInterface> linInterfaces = normalizeLinInterfaces(request.getLinInterfaces(), projectId, ecu.getId());
        List<EcuEthInterface> ethInterfaces = normalizeEthInterfaces(request.getEthInterfaces(), projectId, ecu.getId());

        insertInterfaces(canInterfaces, linInterfaces, ethInterfaces);
        return buildEcuConfig(ecu, forwardInfo, canInterfaces, linInterfaces, ethInterfaces);
    }

    @Transactional
    public EcuConfig updateEcu(Long projectId, Long ecuId, EcuConfig request) {
        Ecu existing = ecuMapper.findById(ecuId);
        if (existing == null || !Objects.equals(existing.getProjectId(), projectId)) {
            return null;
        }

        Ecu ecu = requireEcu(request);
        ecu.setId(ecuId);
        normalizeEcu(ecu, projectId, false);
        ecuMapper.update(ecu);

        EcuForwardInfo forwardInfo = requireForwardInfo(request);
        ecuConfigMapper.deleteForwardInfoByEcuId(ecuId);
        normalizeForwardInfo(forwardInfo, projectId, ecuId, false);
        ecuConfigMapper.insertForwardInfo(forwardInfo);

        ecuConfigMapper.deleteCanInterfacesByEcuId(ecuId);
        ecuConfigMapper.deleteLinInterfacesByEcuId(ecuId);
        ecuConfigMapper.deleteEthInterfacesByEcuId(ecuId);

        List<EcuCanInterface> canInterfaces = normalizeCanInterfaces(request.getCanInterfaces(), projectId, ecuId);
        List<EcuLinInterface> linInterfaces = normalizeLinInterfaces(request.getLinInterfaces(), projectId, ecuId);
        List<EcuEthInterface> ethInterfaces = normalizeEthInterfaces(request.getEthInterfaces(), projectId, ecuId);
        insertInterfaces(canInterfaces, linInterfaces, ethInterfaces);

        return buildEcuConfig(ecu, forwardInfo, canInterfaces, linInterfaces, ethInterfaces);
    }

    @Transactional
    public boolean deleteEcu(Long projectId, Long ecuId) {
        Ecu ecu = ecuMapper.findById(ecuId);
        if (ecu == null || !Objects.equals(ecu.getProjectId(), projectId)) {
            return false;
        }
        deleteConfigs(Collections.singletonList(ecuId));
        return ecuMapper.deleteById(ecuId) > 0;
    }

    @Transactional
    public int deleteEcus(Long projectId, List<Long> ecuIds) {
        if (projectId == null || ecuIds == null || ecuIds.isEmpty()) {
            return 0;
        }
        List<Long> validIds = ecuMapper.findIdsByProjectId(projectId, ecuIds);
        if (validIds == null || validIds.isEmpty()) {
            return 0;
        }
        deleteConfigs(validIds);
        return ecuMapper.deleteBatchByProjectId(projectId, validIds);
    }

    private void deleteConfigs(List<Long> ecuIds) {
        ecuConfigMapper.deleteForwardInfoByEcuIds(ecuIds);
        ecuConfigMapper.deleteCanInterfacesByEcuIds(ecuIds);
        ecuConfigMapper.deleteLinInterfacesByEcuIds(ecuIds);
        ecuConfigMapper.deleteEthInterfacesByEcuIds(ecuIds);
    }

    private void insertInterfaces(List<EcuCanInterface> canInterfaces,
                                  List<EcuLinInterface> linInterfaces,
                                  List<EcuEthInterface> ethInterfaces) {
        if (!canInterfaces.isEmpty()) {
            ecuConfigMapper.insertCanInterfaces(canInterfaces);
        }
        if (!linInterfaces.isEmpty()) {
            ecuConfigMapper.insertLinInterfaces(linInterfaces);
        }
        if (!ethInterfaces.isEmpty()) {
            ecuConfigMapper.insertEthInterfaces(ethInterfaces);
        }
    }

    private EcuConfig buildEcuConfig(Ecu ecu) {
        EcuForwardInfo forwardInfo = ecuConfigMapper.findForwardInfoByEcuId(ecu.getId());
        List<EcuCanInterface> canInterfaces = ecuConfigMapper.findCanInterfacesByEcuId(ecu.getId());
        List<EcuLinInterface> linInterfaces = ecuConfigMapper.findLinInterfacesByEcuId(ecu.getId());
        List<EcuEthInterface> ethInterfaces = ecuConfigMapper.findEthInterfacesByEcuId(ecu.getId());
        return buildEcuConfig(ecu, forwardInfo, canInterfaces, linInterfaces, ethInterfaces);
    }

    private EcuConfig buildEcuConfig(Ecu ecu,
                                     EcuForwardInfo forwardInfo,
                                     List<EcuCanInterface> canInterfaces,
                                     List<EcuLinInterface> linInterfaces,
                                     List<EcuEthInterface> ethInterfaces) {
        EcuConfig config = new EcuConfig();
        config.setEcu(ecu);
        config.setForwardInfo(forwardInfo);
        config.setCanInterfaces(canInterfaces == null ? Collections.emptyList() : canInterfaces);
        config.setLinInterfaces(linInterfaces == null ? Collections.emptyList() : linInterfaces);
        config.setEthInterfaces(ethInterfaces == null ? Collections.emptyList() : ethInterfaces);
        return config;
    }

    private void normalizeEcu(Ecu ecu, Long projectId, boolean generateId) {
        if (projectId == null) {
            throw new IllegalArgumentException("工程ID不能为空");
        }
        if (generateId) {
            ecu.setId(snowflakeIdGenerator.nextId());
        }
        ecu.setProjectId(projectId);
        ecu.setName(requireTrimmed(ecu.getName(), "ECU名称"));
        ecu.setType(trimToNull(ecu.getType()));
        ecu.setDesc(trimToNull(ecu.getDesc()));
        ecu.setMac(normalizeMac(ecu.getMac()));
        ecu.setIp(normalizeIp(ecu.getIp()));
        ecu.setPort(requirePositive(ecu.getPort(), "端口号"));
        ecu.setIndex(requirePositive(ecu.getIndex(), "部件索引号"));
    }

    private void normalizeForwardInfo(EcuForwardInfo forwardInfo, Long projectId, Long ecuId, boolean generateId) {
        if (generateId || forwardInfo.getId() == null) {
            forwardInfo.setId(snowflakeIdGenerator.nextId());
        }
        forwardInfo.setProjectId(projectId);
        forwardInfo.setEcuId(ecuId);
        forwardInfo.setPFlashMemoryStartAddress(normalizeHex(forwardInfo.getPFlashMemoryStartAddress(), "P Flash起始地址"));
        forwardInfo.setPFlashMemorySizeLimit(normalizeHex(forwardInfo.getPFlashMemorySizeLimit(), "P Flash大小限制"));
        forwardInfo.setRamMemoryStartAddress(normalizeHex(forwardInfo.getRamMemoryStartAddress(), "RAM起始地址"));
        forwardInfo.setRamMemorySizeLimit(normalizeHex(forwardInfo.getRamMemorySizeLimit(), "RAM大小限制"));
    }

    private List<EcuCanInterface> normalizeCanInterfaces(List<EcuCanInterface> items, Long projectId, Long ecuId) {
        if (items == null) {
            return Collections.emptyList();
        }
        List<EcuCanInterface> result = new ArrayList<>();
        for (EcuCanInterface item : items) {
            if (item == null) {
                continue;
            }
            item.setId(snowflakeIdGenerator.nextId());
            item.setProjectId(projectId);
            item.setEcuId(ecuId);
            item.setInterfaceName(requireTrimmed(item.getInterfaceName(), "CAN接口名称"));
            item.setChannelId(requirePositive(item.getChannelId(), "CAN接口ID"));
            if (!EcuCanInterfaceTypeEnum.isValid(item.getInterfaceType())) {
                throw new IllegalArgumentException("CAN接口类型不合法");
            }
            result.add(item);
        }
        return result;
    }

    private List<EcuLinInterface> normalizeLinInterfaces(List<EcuLinInterface> items, Long projectId, Long ecuId) {
        if (items == null) {
            return Collections.emptyList();
        }
        List<EcuLinInterface> result = new ArrayList<>();
        for (EcuLinInterface item : items) {
            if (item == null) {
                continue;
            }
            item.setId(snowflakeIdGenerator.nextId());
            item.setProjectId(projectId);
            item.setEcuId(ecuId);
            item.setInterfaceName(requireTrimmed(item.getInterfaceName(), "LIN接口名称"));
            item.setChannelId(requirePositive(item.getChannelId(), "LIN接口ID"));
            result.add(item);
        }
        return result;
    }

    private List<EcuEthInterface> normalizeEthInterfaces(List<EcuEthInterface> items, Long projectId, Long ecuId) {
        if (items == null) {
            return Collections.emptyList();
        }
        List<EcuEthInterface> result = new ArrayList<>();
        for (EcuEthInterface item : items) {
            if (item == null) {
                continue;
            }
            item.setId(snowflakeIdGenerator.nextId());
            item.setProjectId(projectId);
            item.setEcuId(ecuId);
            item.setInterfaceName(requireTrimmed(item.getInterfaceName(), "ETH接口名称"));
            if (!EthPortTypeEnum.isValid(item.getType())) {
                throw new IllegalArgumentException("ETH接口类型不合法");
            }
            result.add(item);
        }
        return result;
    }

    private String requireTrimmed(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + "不能为空");
        }
        return value.trim();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Integer requirePositive(Integer value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + "不能为空");
        }
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + "必须大于0");
        }
        return value;
    }

    private String normalizeMac(String mac) {
        if (mac == null || mac.trim().isEmpty()) {
            throw new IllegalArgumentException("MAC不能为空");
        }
        String normalized = mac.replace(":", "").replace("-", "").replace(" ", "").toUpperCase();
        if (normalized.length() != 12 || !HEX_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("MAC格式不正确");
        }
        return normalized;
    }

    private String normalizeIp(String ip) {
        String trimmed = requireTrimmed(ip, "IP地址");
        if (!IPV4_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("IP格式不正确");
        }
        return trimmed;
    }

    private String normalizeHex(String value, String fieldName) {
        String trimmed = requireTrimmed(value, fieldName);
        String normalized = trimmed.startsWith("0x") || trimmed.startsWith("0X")
                ? trimmed.substring(2)
                : trimmed;
        normalized = normalized.trim().toUpperCase();
        if (normalized.isEmpty() || !HEX_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(fieldName + "格式不正确");
        }
        return normalized;
    }

    private Ecu requireEcu(EcuConfig request) {
        if (request == null || request.getEcu() == null) {
            throw new IllegalArgumentException("ECU基础配置不能为空");
        }
        return request.getEcu();
    }

    private EcuForwardInfo requireForwardInfo(EcuConfig request) {
        if (request == null || request.getForwardInfo() == null) {
            throw new IllegalArgumentException("转发表通信配置不能为空");
        }
        return request.getForwardInfo();
    }
}
