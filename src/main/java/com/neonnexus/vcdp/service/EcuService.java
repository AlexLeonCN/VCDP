package com.neonnexus.vcdp.service;

import com.neonnexus.vcdp.common.ErrorConstant;
import com.neonnexus.vcdp.common.PageResult;
import com.neonnexus.vcdp.common.Pair;
import com.neonnexus.vcdp.common.enumation.CanInterfaceTypeEnum;
import com.neonnexus.vcdp.common.enumation.EcuCanInterfaceTypeEnum;
import com.neonnexus.vcdp.common.enumation.EthPortTypeEnum;
import com.neonnexus.vcdp.entity.dto.EcuConfig;
import com.neonnexus.vcdp.entity.po.project.Ecu;
import com.neonnexus.vcdp.entity.po.project.CanInterface;
import com.neonnexus.vcdp.entity.po.project.EthInterface;
import com.neonnexus.vcdp.entity.po.project.EcuForwardInfo;
import com.neonnexus.vcdp.entity.po.project.LinInterface;
import com.neonnexus.vcdp.exception.VCDPException;
import com.neonnexus.vcdp.mapper.EcuConfigMapper;
import com.neonnexus.vcdp.mapper.EcuMapper;
import com.neonnexus.vcdp.mapper.ProjectMapper;
import com.neonnexus.vcdp.util.HexUtils;
import com.neonnexus.vcdp.util.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

    private final ProjectMapper projectMapper;
    private final EcuMapper ecuMapper;
    private final EcuConfigMapper ecuConfigMapper;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    public PageResult<Ecu> listEcus(String projectId, Integer page, Integer size) {
        ensureProjectExists(projectId);
        int safePage = page == null || page < 1 ? DEFAULT_PAGE : page;
        int safeSize = size == null || size < 1 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        int offset = (safePage - 1) * safeSize;

        List<Ecu> records = ecuMapper.findPageByProjectId(projectId, offset, safeSize);
        long total = ecuMapper.countByProjectId(projectId);
        return new PageResult<>(records, total, safePage, safeSize);
    }

    public EcuConfig getEcuConfig(String projectId, String ecuId) {
        ensureProjectExists(projectId);
        Ecu ecu = ecuMapper.findById(ecuId);
        if (ecu == null || !Objects.equals(ecu.getProjectId(), projectId)) {
            throw new VCDPException(ErrorConstant.Ecu.NOT_FOUND);
        }
        return buildEcuConfig(ecu);
    }

    @Transactional
    public EcuConfig createEcu(String projectId, EcuConfig request) {
        ensureProjectExists(projectId);
        Ecu ecu = requireEcu(request);
        EcuForwardInfo forwardInfo = requireForwardInfo(request);

        normalizeEcu(ecu, projectId, true);
        ensureEcuUniqueness(ecu, null);
        ecuMapper.insert(ecu);

        normalizeForwardInfo(forwardInfo, projectId, ecu.getId(), true);
        ecuConfigMapper.insertForwardInfo(forwardInfo);

        List<CanInterface> canInterfaces = normalizeCanInterfaces(request.getCanInterfaces(), projectId, ecu.getId());
        List<LinInterface> linInterfaces = normalizeLinInterfaces(request.getLinInterfaces(), projectId, ecu.getId());
        List<EthInterface> ethInterfaces = normalizeEthInterfaces(request.getEthInterfaces(), projectId, ecu.getId());
        validateInterfaceUniqueness(canInterfaces, linInterfaces, ethInterfaces);

        insertInterfaces(canInterfaces, linInterfaces, ethInterfaces);
        return buildEcuConfig(ecu, forwardInfo, canInterfaces, linInterfaces, ethInterfaces);
    }

    @Transactional
    public EcuConfig updateEcu(String projectId, String ecuId, EcuConfig request) {
        ensureProjectExists(projectId);
        Ecu existing = ecuMapper.findById(ecuId);
        if (existing == null || !Objects.equals(existing.getProjectId(), projectId)) {
            throw new VCDPException(ErrorConstant.Ecu.NOT_FOUND);
        }

        Ecu ecu = requireEcu(request);
        ecu.setId(ecuId);
        normalizeEcu(ecu, projectId, false);
        ensureEcuUniqueness(ecu, ecuId);
        ecuMapper.update(ecu);

        EcuForwardInfo forwardInfo = requireForwardInfo(request);
        ecuConfigMapper.deleteForwardInfoByEcuId(ecuId);
        normalizeForwardInfo(forwardInfo, projectId, ecuId, false);
        ecuConfigMapper.insertForwardInfo(forwardInfo);

        ecuConfigMapper.deleteCanInterfacesByEcuId(ecuId);
        ecuConfigMapper.deleteLinInterfacesByEcuId(ecuId);
        ecuConfigMapper.deleteEthInterfacesByEcuId(ecuId);

        List<CanInterface> canInterfaces = normalizeCanInterfaces(request.getCanInterfaces(), projectId, ecuId);
        List<LinInterface> linInterfaces = normalizeLinInterfaces(request.getLinInterfaces(), projectId, ecuId);
        List<EthInterface> ethInterfaces = normalizeEthInterfaces(request.getEthInterfaces(), projectId, ecuId);
        validateInterfaceUniqueness(canInterfaces, linInterfaces, ethInterfaces);
        insertInterfaces(canInterfaces, linInterfaces, ethInterfaces);

        return buildEcuConfig(ecu, forwardInfo, canInterfaces, linInterfaces, ethInterfaces);
    }

    @Transactional
    public boolean deleteEcu(String projectId, String ecuId) {
        ensureProjectExists(projectId);
        Ecu ecu = ecuMapper.findById(ecuId);
        if (ecu == null || !Objects.equals(ecu.getProjectId(), projectId)) {
            throw new VCDPException(ErrorConstant.Ecu.NOT_FOUND);
        }
        deleteConfigs(Collections.singletonList(ecuId));
        return ecuMapper.deleteById(ecuId) > 0;
    }

    @Transactional
    public int deleteEcus(String projectId, List<String> ecuIds) {
        ensureProjectExists(projectId);
        if (ecuIds == null || ecuIds.isEmpty()) {
            return 0;
        }
        List<String> validIds = ecuMapper.findIdsByProjectId(projectId, ecuIds);
        if (validIds == null || validIds.isEmpty()) {
            return 0;
        }
        deleteConfigs(validIds);
        return ecuMapper.deleteBatchByProjectId(projectId, validIds);
    }

    private void ensureProjectExists(String projectId) {
        if (projectId == null || projectId.isBlank() || projectMapper.findById(projectId) == null) {
            throw new VCDPException(ErrorConstant.Ecu.PROJECT_NOT_FOUND);
        }
    }

    private void deleteConfigs(List<String> ecuIds) {
        ecuConfigMapper.deleteForwardInfoByEcuIds(ecuIds);
        ecuConfigMapper.deleteCanInterfacesByEcuIds(ecuIds);
        ecuConfigMapper.deleteLinInterfacesByEcuIds(ecuIds);
        ecuConfigMapper.deleteEthInterfacesByEcuIds(ecuIds);
    }

    private void insertInterfaces(List<CanInterface> canInterfaces,
                                  List<LinInterface> linInterfaces,
                                  List<EthInterface> ethInterfaces) {
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
        List<CanInterface> canInterfaces = ecuConfigMapper.findCanInterfacesByEcuId(ecu.getId());
        List<LinInterface> linInterfaces = ecuConfigMapper.findLinInterfacesByEcuId(ecu.getId());
        List<EthInterface> ethInterfaces = ecuConfigMapper.findEthInterfacesByEcuId(ecu.getId());
        return buildEcuConfig(ecu, forwardInfo, canInterfaces, linInterfaces, ethInterfaces);
    }

    private EcuConfig buildEcuConfig(Ecu ecu,
                                     EcuForwardInfo forwardInfo,
                                     List<CanInterface> canInterfaces,
                                     List<LinInterface> linInterfaces,
                                     List<EthInterface> ethInterfaces) {
        EcuConfig config = new EcuConfig();
        config.setEcu(ecu);
        config.setForwardInfo(forwardInfo);
        config.setCanInterfaces(canInterfaces == null ? Collections.emptyList() : canInterfaces);
        config.setLinInterfaces(linInterfaces == null ? Collections.emptyList() : linInterfaces);
        config.setEthInterfaces(ethInterfaces == null ? Collections.emptyList() : ethInterfaces);
        return config;
    }

    private void normalizeEcu(Ecu ecu, String projectId, boolean generateId) {
        if (generateId) {
            ecu.setId(snowflakeIdGenerator.nextId());
        }
        ecu.setProjectId(projectId);
        ecu.setName(requireTrimmed(ecu.getName(), ErrorConstant.Ecu.NAME_EMPTY));
        ecu.setType(trimToNull(ecu.getType()));
        ecu.setDesc(trimToNull(ecu.getDesc()));
        ecu.setMac(normalizeMac(ecu.getMac()));
        ecu.setIp(normalizeIp(ecu.getIp()));
        ecu.setPort(requireNonNegative(ecu.getPort(), ErrorConstant.Ecu.PORT_EMPTY, ErrorConstant.Ecu.PORT_INVALID));
        ecu.setIndex(requireNonNegative(ecu.getIndex(), ErrorConstant.Ecu.INDEX_EMPTY, ErrorConstant.Ecu.INDEX_INVALID));
    }

    private void ensureEcuUniqueness(Ecu ecu, String excludeId) {
        if (ecuMapper.countByProjectIdAndName(ecu.getProjectId(), ecu.getName(), excludeId) > 0) {
            throw new VCDPException(ErrorConstant.Ecu.NAME_DUPLICATE);
        }
        if (ecuMapper.countByProjectIdAndMac(ecu.getProjectId(), ecu.getMac(), excludeId) > 0) {
            throw new VCDPException(ErrorConstant.Ecu.MAC_DUPLICATE);
        }
        if (ecuMapper.countByProjectIdAndIp(ecu.getProjectId(), ecu.getIp(), excludeId) > 0) {
            throw new VCDPException(ErrorConstant.Ecu.IP_DUPLICATE);
        }
        if (ecuMapper.countByProjectIdAndPort(ecu.getProjectId(), ecu.getPort(), excludeId) > 0) {
            throw new VCDPException(ErrorConstant.Ecu.PORT_DUPLICATE);
        }
        if (ecuMapper.countByProjectIdAndIndex(ecu.getProjectId(), ecu.getIndex(), excludeId) > 0) {
            throw new VCDPException(ErrorConstant.Ecu.INDEX_DUPLICATE);
        }
    }

    private void normalizeForwardInfo(EcuForwardInfo forwardInfo, String projectId, String ecuId, boolean generateId) {
        if (generateId || forwardInfo.getId() == null || forwardInfo.getId().isBlank()) {
            forwardInfo.setId(snowflakeIdGenerator.nextId());
        }
        forwardInfo.setProjectId(projectId);
        forwardInfo.setEcuId(ecuId);
        forwardInfo.setPFlashMemoryStartAddress(
                normalizePositiveHex(forwardInfo.getPFlashMemoryStartAddress(), ErrorConstant.EcuForward.P_FLASH_START_INVALID));
        forwardInfo.setPFlashMemorySizeLimit(
                normalizePositiveHex(forwardInfo.getPFlashMemorySizeLimit(), ErrorConstant.EcuForward.P_FLASH_SIZE_INVALID));
        forwardInfo.setRamMemoryStartAddress(
                normalizePositiveHex(forwardInfo.getRamMemoryStartAddress(), ErrorConstant.EcuForward.RAM_START_INVALID));
        forwardInfo.setRamMemorySizeLimit(
                normalizePositiveHex(forwardInfo.getRamMemorySizeLimit(), ErrorConstant.EcuForward.RAM_SIZE_INVALID));
    }

    private List<CanInterface> normalizeCanInterfaces(List<CanInterface> items, String projectId, String ecuId) {
        if (items == null) {
            return Collections.emptyList();
        }
        List<CanInterface> result = new ArrayList<>();
        for (CanInterface item : items) {
            if (item == null) {
                continue;
            }
            item.setId(snowflakeIdGenerator.nextId());
            item.setProjectId(projectId);
            item.setEcuId(ecuId);
            item.setInterfaceName(requireTrimmed(item.getInterfaceName(), ErrorConstant.CanInterface.NAME_EMPTY));
            item.setChannelId(requireNonNegative(
                    item.getChannelId(), ErrorConstant.CanInterface.CHANNEL_EMPTY, ErrorConstant.CanInterface.CHANNEL_INVALID));
            item.setPort(requireNonNegative(
                    item.getPort(), ErrorConstant.CanInterface.PORT_EMPTY, ErrorConstant.CanInterface.PORT_INVALID));
            if (!CanInterfaceTypeEnum.isValid(item.getType())) {
                throw new VCDPException(ErrorConstant.CanInterface.TYPE_INVALID);
            }
            if (!EcuCanInterfaceTypeEnum.isValid(item.getConnType())) {
                throw new VCDPException(ErrorConstant.CanInterface.CONN_TYPE_INVALID);
            }
            result.add(item);
        }
        return result;
    }

    private List<LinInterface> normalizeLinInterfaces(List<LinInterface> items, String projectId, String ecuId) {
        if (items == null) {
            return Collections.emptyList();
        }
        List<LinInterface> result = new ArrayList<>();
        for (LinInterface item : items) {
            if (item == null) {
                continue;
            }
            item.setId(snowflakeIdGenerator.nextId());
            item.setProjectId(projectId);
            item.setEcuId(ecuId);
            item.setInterfaceName(requireTrimmed(item.getInterfaceName(), ErrorConstant.LinInterface.NAME_EMPTY));
            item.setChannelId(requireNonNegative(
                    item.getChannelId(), ErrorConstant.LinInterface.CHANNEL_EMPTY, ErrorConstant.LinInterface.CHANNEL_INVALID));
            item.setPort(requireNonNegative(
                    item.getPort(), ErrorConstant.LinInterface.PORT_EMPTY, ErrorConstant.LinInterface.PORT_INVALID));
            result.add(item);
        }
        return result;
    }

    private List<EthInterface> normalizeEthInterfaces(List<EthInterface> items, String projectId, String ecuId) {
        if (items == null) {
            return Collections.emptyList();
        }
        List<EthInterface> result = new ArrayList<>();
        for (EthInterface item : items) {
            if (item == null) {
                continue;
            }
            item.setId(snowflakeIdGenerator.nextId());
            item.setProjectId(projectId);
            item.setEcuId(ecuId);
            item.setInterfaceName(requireTrimmed(item.getInterfaceName(), ErrorConstant.EthInterface.NAME_EMPTY));
            item.setChannelId(requireNonNegative(
                    item.getChannelId(), ErrorConstant.EthInterface.CHANNEL_EMPTY, ErrorConstant.EthInterface.CHANNEL_INVALID));
            item.setPort(requireNonNegative(
                    item.getPort(), ErrorConstant.EthInterface.PORT_EMPTY, ErrorConstant.EthInterface.PORT_INVALID));
            if (!EthPortTypeEnum.isValid(item.getType())) {
                throw new VCDPException(ErrorConstant.EthInterface.TYPE_INVALID);
            }
            result.add(item);
        }
        return result;
    }

    private void validateInterfaceUniqueness(List<CanInterface> canInterfaces,
                                             List<LinInterface> linInterfaces,
                                             List<EthInterface> ethInterfaces) {
        Set<String> names = new HashSet<>();
        Set<Integer> ports = new HashSet<>();
        Set<Integer> canChannels = new HashSet<>();
        Set<Integer> linChannels = new HashSet<>();
        Set<Integer> ethChannels = new HashSet<>();

        for (CanInterface item : canInterfaces) {
            if (!names.add(item.getInterfaceName())) {
                throw new VCDPException(ErrorConstant.CanInterface.NAME_DUPLICATE);
            }
            if (!ports.add(item.getPort())) {
                throw new VCDPException(ErrorConstant.CanInterface.PORT_DUPLICATE);
            }
            if (!canChannels.add(item.getChannelId())) {
                throw new VCDPException(ErrorConstant.CanInterface.CHANNEL_DUPLICATE);
            }
        }
        for (LinInterface item : linInterfaces) {
            if (!names.add(item.getInterfaceName())) {
                throw new VCDPException(ErrorConstant.LinInterface.NAME_DUPLICATE);
            }
            if (!ports.add(item.getPort())) {
                throw new VCDPException(ErrorConstant.LinInterface.PORT_DUPLICATE);
            }
            if (!linChannels.add(item.getChannelId())) {
                throw new VCDPException(ErrorConstant.LinInterface.CHANNEL_DUPLICATE);
            }
        }
        for (EthInterface item : ethInterfaces) {
            if (!names.add(item.getInterfaceName())) {
                throw new VCDPException(ErrorConstant.EthInterface.NAME_DUPLICATE);
            }
            if (!ports.add(item.getPort())) {
                throw new VCDPException(ErrorConstant.EthInterface.PORT_DUPLICATE);
            }
            if (!ethChannels.add(item.getChannelId())) {
                throw new VCDPException(ErrorConstant.EthInterface.CHANNEL_DUPLICATE);
            }
        }
    }

    private String requireTrimmed(String value, Pair<Integer, String> emptyError) {
        if (value == null || value.trim().isEmpty()) {
            throw new VCDPException(emptyError);
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

    private Integer requireNonNegative(Integer value,
                                       Pair<Integer, String> emptyError,
                                       Pair<Integer, String> invalidError) {
        if (value == null) {
            throw new VCDPException(emptyError);
        }
        if (value < 0) {
            throw new VCDPException(invalidError);
        }
        return value;
    }

    private String normalizeMac(String mac) {
        if (mac == null || mac.trim().isEmpty()) {
            throw new VCDPException(ErrorConstant.Ecu.MAC_EMPTY);
        }
        String normalized = mac.replace(":", "").replace("-", "").replace(" ", "").toUpperCase();
        if (normalized.length() != 12 || !HEX_PATTERN.matcher(normalized).matches()) {
            throw new VCDPException(ErrorConstant.Ecu.MAC_INVALID);
        }
        return normalized;
    }

    private String normalizeIp(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            throw new VCDPException(ErrorConstant.Ecu.IP_EMPTY);
        }
        String trimmed = ip.trim();
        if (!IPV4_PATTERN.matcher(trimmed).matches()) {
            throw new VCDPException(ErrorConstant.Ecu.IP_INVALID);
        }
        return trimmed;
    }

    private String normalizePositiveHex(String value, Pair<Integer, String> invalidError) {
        if (!HexUtils.isValid(value)) {
            throw new VCDPException(invalidError);
        }
        String normalized = HexUtils.normalize(value);
        if (normalized == null || !HexUtils.isPositive(normalized)) {
            throw new VCDPException(invalidError);
        }
        return normalized;
    }

    private Ecu requireEcu(EcuConfig request) {
        if (request == null || request.getEcu() == null) {
            throw new VCDPException(ErrorConstant.Ecu.CONFIG_EMPTY);
        }
        return request.getEcu();
    }

    private EcuForwardInfo requireForwardInfo(EcuConfig request) {
        if (request == null || request.getForwardInfo() == null) {
            throw new VCDPException(ErrorConstant.EcuForward.CONFIG_EMPTY);
        }
        return request.getForwardInfo();
    }
}
