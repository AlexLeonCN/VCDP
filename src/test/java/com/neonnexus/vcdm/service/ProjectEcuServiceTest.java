package com.neonnexus.vcdm.service;

import com.neonnexus.vcdm.common.ErrorConstant;
import com.neonnexus.vcdm.entity.dto.EcuConfig;
import com.neonnexus.vcdm.entity.po.project.Ecu;
import com.neonnexus.vcdm.entity.po.project.EcuCanInterface;
import com.neonnexus.vcdm.entity.po.project.EcuEthInterface;
import com.neonnexus.vcdm.entity.po.project.EcuForwardInfo;
import com.neonnexus.vcdm.entity.po.project.EcuLinInterface;
import com.neonnexus.vcdm.entity.po.project.Project;
import com.neonnexus.vcdm.exception.VCDPException;
import com.neonnexus.vcdm.support.DatabaseTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class ProjectEcuServiceTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private EcuService ecuService;

    @BeforeEach
    void setUp() throws Exception {
        initDatabase();
    }

    /**
     * 清除并重新创建 H2 的 .mv 文件，并基于 schema.sql 重新创建数据库表格。
     */
    void initDatabase() throws Exception {
        DatabaseTestSupport.initDatabase(dataSource, jdbcTemplate);
    }

    @Test
    void createProjectShouldUseSnowflakeIdAndRejectDuplicateName() {
        Project project = new Project();
        project.setName("工程A");
        project.setDescription("描述A");
        Project created = projectService.createProject(project);
        assertNotNull(created.getId());
        assertTrue(created.getId().matches("\\d{15,}"));

        Project duplicated = new Project();
        duplicated.setName("工程A");
        VCDPException ex = assertThrows(VCDPException.class, () -> projectService.createProject(duplicated));
        assertEquals(ErrorConstant.Project.NAME_DUPLICATE.getKey(), ex.getCode());
    }

    @Test
    void createEcuShouldPersistAllConfigsAndNormalizeHex() {
        Project project = createProject("工程B");

        EcuConfig created = ecuService.createEcu(project.getId(), buildValidConfig());
        assertNotNull(created.getEcu().getId());
        assertEquals("AABBCCDDEEFF", created.getEcu().getMac());
        assertEquals("192.168.1.10", created.getEcu().getIp());
        assertEquals("0x1000", created.getForwardInfo().getPFlashMemoryStartAddress());
        assertEquals("0x2000", created.getForwardInfo().getPFlashMemorySizeLimit());
        assertEquals("0x3000", created.getForwardInfo().getRamMemoryStartAddress());
        assertEquals("0x4000", created.getForwardInfo().getRamMemorySizeLimit());
        assertEquals(1, created.getCanInterfaces().size());
        assertEquals(0, created.getCanInterfaces().get(0).getType());
        assertEquals(1, created.getCanInterfaces().get(0).getConnType());
        assertEquals(1, created.getLinInterfaces().size());
        assertEquals(1, created.getEthInterfaces().size());
        assertEquals(1, created.getEthInterfaces().get(0).getChannelId());
    }

    @Test
    void createEcuShouldRejectDuplicateMacWithinProject() {
        Project project = createProject("工程C");
        ecuService.createEcu(project.getId(), buildValidConfig());

        EcuConfig second = buildValidConfig();
        second.getEcu().setName("ECU-2");
        second.getEcu().setIp("192.168.1.11");
        second.getEcu().setPort(9001);
        second.getEcu().setIndex(2);

        VCDPException ex = assertThrows(VCDPException.class,
                () -> ecuService.createEcu(project.getId(), second));
        assertEquals(ErrorConstant.Ecu.MAC_DUPLICATE.getKey(), ex.getCode());
    }

    @Test
    void createEcuShouldRejectDuplicateInterfaceNameAcrossTypes() {
        Project project = createProject("工程D");
        EcuConfig config = buildValidConfig();
        config.getLinInterfaces().get(0).setInterfaceName("CAN1");

        VCDPException ex = assertThrows(VCDPException.class,
                () -> ecuService.createEcu(project.getId(), config));
        assertEquals(ErrorConstant.LinInterface.NAME_DUPLICATE.getKey(), ex.getCode());
    }

    @Test
    void deleteProjectShouldCascadeDeleteEcus() {
        Project project = createProject("工程E");
        EcuConfig created = ecuService.createEcu(project.getId(), buildValidConfig());
        assertNotNull(created.getEcu().getId());

        projectService.deleteProject(project.getId());
        VCDPException ex = assertThrows(VCDPException.class,
                () -> ecuService.getEcuConfig(project.getId(), created.getEcu().getId()));
        assertEquals(ErrorConstant.Ecu.PROJECT_NOT_FOUND.getKey(), ex.getCode());
    }

    @Test
    void createEcuShouldRejectInvalidHexAddress() {
        Project project = createProject("工程F");
        EcuConfig config = buildValidConfig();
        config.getForwardInfo().setPFlashMemoryStartAddress("XYZ");

        VCDPException ex = assertThrows(VCDPException.class,
                () -> ecuService.createEcu(project.getId(), config));
        assertEquals(ErrorConstant.EcuForward.P_FLASH_START_INVALID.getKey(), ex.getCode());
    }

    @Test
    void createEcuShouldRejectZeroHexAddress() {
        Project project = createProject("工程G");
        EcuConfig config = buildValidConfig();
        config.getForwardInfo().setRamMemorySizeLimit("0x0");

        VCDPException ex = assertThrows(VCDPException.class,
                () -> ecuService.createEcu(project.getId(), config));
        assertEquals(ErrorConstant.EcuForward.RAM_SIZE_INVALID.getKey(), ex.getCode());
    }

    private Project createProject(String name) {
        Project project = new Project();
        project.setName(name);
        project.setDescription("desc");
        return projectService.createProject(project);
    }

    private EcuConfig buildValidConfig() {
        Ecu ecu = new Ecu();
        ecu.setName("ECU-1");
        ecu.setType("GW");
        ecu.setDesc("gateway");
        ecu.setMac("AA:BB:CC:DD:EE:FF");
        ecu.setIp("192.168.1.10");
        ecu.setPort(9000);
        ecu.setIndex(1);

        EcuForwardInfo forwardInfo = new EcuForwardInfo();
        forwardInfo.setPFlashMemoryStartAddress("1000");
        forwardInfo.setPFlashMemorySizeLimit("0x2000");
        forwardInfo.setRamMemoryStartAddress("0X3000");
        forwardInfo.setRamMemorySizeLimit("4000");

        EcuCanInterface can = new EcuCanInterface();
        can.setInterfaceName("CAN1");
        can.setChannelId(0);
        can.setPort(100);
        can.setType(0);
        can.setConnType(1);

        EcuLinInterface lin = new EcuLinInterface();
        lin.setInterfaceName("LIN1");
        lin.setChannelId(0);
        lin.setPort(101);

        EcuEthInterface eth = new EcuEthInterface();
        eth.setInterfaceName("ETH1");
        eth.setChannelId(1);
        eth.setPort(102);
        eth.setType(1);

        EcuConfig config = new EcuConfig();
        config.setEcu(ecu);
        config.setForwardInfo(forwardInfo);
        config.setCanInterfaces(List.of(can));
        config.setLinInterfaces(List.of(lin));
        config.setEthInterfaces(List.of(eth));
        return config;
    }
}
