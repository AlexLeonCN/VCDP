package com.neonnexus.vcdp.controller;

import com.neonnexus.vcdp.support.DatabaseTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EnumControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseTestSupport.initDatabase(dataSource, jdbcTemplate);
    }

    @Test
    void shouldReturnCanInterfaceTypes() throws Exception {
        mockMvc.perform(get("/api/enums/can-interface-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].code").value(0))
                .andExpect(jsonPath("$.data[0].name").value("CAN"))
                .andExpect(jsonPath("$.data[1].code").value(1))
                .andExpect(jsonPath("$.data[1].name").value("CANFD"));
    }

    @Test
    void shouldReturnCanConnTypes() throws Exception {
        mockMvc.perform(get("/api/enums/can-conn-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("MCU直连CAN"))
                .andExpect(jsonPath("$.data[1].name").value("LSW下挂CAN"));
    }

    @Test
    void shouldReturnEthInterfaceTypes() throws Exception {
        mockMvc.perform(get("/api/enums/eth-interface-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("百兆"))
                .andExpect(jsonPath("$.data[1].name").value("千兆"));
    }
}
