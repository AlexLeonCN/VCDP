package com.neonnexus.vcdp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neonnexus.vcdp.support.DatabaseTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProjectIdSerializationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseTestSupport.initDatabase(dataSource, jdbcTemplate);
    }

    @Test
    void createAndListProjectShouldReturnIdAsStringMatchingDatabase() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"工程ID测试\",\"description\":\"用于校验雪花ID序列化\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").isString())
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        JsonNode createdIdNode = created.path("data").path("id");
        assertTrue(createdIdNode.isTextual());
        String createdId = createdIdNode.asText();
        assertTrue(createdId.matches("\\d{15,}"));

        String dbId = jdbcTemplate.queryForObject(
                "SELECT CAST(id AS VARCHAR) FROM project WHERE name = ?",
                String.class,
                "工程ID测试");
        assertEquals(dbId, createdId);

        MvcResult listResult = mockMvc.perform(get("/api/projects").param("page", "1").param("size", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[0].id").isString())
                .andExpect(jsonPath("$.data.records[0].id").value(createdId))
                .andExpect(jsonPath("$.data.total").isNumber())
                .andReturn();

        JsonNode listedId = objectMapper.readTree(listResult.getResponse().getContentAsString())
                .path("data").path("records").get(0).path("id");
        assertTrue(listedId.isTextual());
        assertEquals(createdId, listedId.asText());

        mockMvc.perform(get("/api/projects/{id}", createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").isString())
                .andExpect(jsonPath("$.data.id").value(createdId));
    }

    @Test
    void createEcuShouldReturnSnowflakeIdsAsStringsMatchingDatabase() throws Exception {
        MvcResult projectResult = mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"ECU工程\",\"description\":\"ecu\"}"))
                .andExpect(status().isOk())
                .andReturn();
        String projectId = objectMapper.readTree(projectResult.getResponse().getContentAsString())
                .path("data").path("id").asText();

        String ecuPayload = """
                {
                  "ecu": {
                    "name": "ECU-1",
                    "type": "GW",
                    "desc": "gateway",
                    "mac": "AA:BB:CC:DD:EE:FF",
                    "ip": "192.168.1.10",
                    "port": 9000,
                    "index": 1
                  },
                  "forwardInfo": {
                    "pFlashMemoryStartAddress": "1000",
                    "pFlashMemorySizeLimit": "0x2000",
                    "ramMemoryStartAddress": "0X3000",
                    "ramMemorySizeLimit": "4000"
                  },
                  "canInterfaces": [{
                    "interfaceName": "CAN1",
                    "channelId": 0,
                    "port": 100,
                    "type": 0,
                    "connType": 1
                  }],
                  "linInterfaces": [{
                    "interfaceName": "LIN1",
                    "channelId": 0,
                    "port": 101
                  }],
                  "ethInterfaces": [{
                    "interfaceName": "ETH1",
                    "channelId": 1,
                    "port": 102,
                    "type": 1
                  }]
                }
                """;

        MvcResult ecuResult = mockMvc.perform(post("/api/projects/{projectId}/ecus", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ecuPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ecu.id").isString())
                .andExpect(jsonPath("$.data.ecu.projectId").isString())
                .andExpect(jsonPath("$.data.ecu.projectId").value(projectId))
                .andExpect(jsonPath("$.data.forwardInfo.id").isString())
                .andExpect(jsonPath("$.data.forwardInfo.pFlashMemoryStartAddress").value("0x1000"))
                .andExpect(jsonPath("$.data.forwardInfo.pFlashMemorySizeLimit").value("0x2000"))
                .andExpect(jsonPath("$.data.forwardInfo.ramMemoryStartAddress").value("0x3000"))
                .andExpect(jsonPath("$.data.forwardInfo.ramMemorySizeLimit").value("0x4000"))
                .andExpect(jsonPath("$.data.canInterfaces[0].id").isString())
                .andExpect(jsonPath("$.data.linInterfaces[0].id").isString())
                .andExpect(jsonPath("$.data.ethInterfaces[0].id").isString())
                .andReturn();

        JsonNode ecu = objectMapper.readTree(ecuResult.getResponse().getContentAsString()).path("data");
        String ecuId = ecu.path("ecu").path("id").asText();
        assertTrue(ecu.path("ecu").path("id").isTextual());

        String dbEcuId = jdbcTemplate.queryForObject(
                "SELECT CAST(id AS VARCHAR) FROM ecu WHERE project_id = ? AND name = ?",
                String.class,
                projectId,
                "ECU-1");
        assertEquals(dbEcuId, ecuId);

        mockMvc.perform(get("/api/projects/{projectId}/ecus/{ecuId}", projectId, ecuId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ecu.id").isString())
                .andExpect(jsonPath("$.data.ecu.id").value(ecuId));
    }
}
