package ru.poteriashki.laf.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.poteriashki.laf.web.config.TestMvcConfig;

import javax.annotation.Resource;


/**
 * @author Yuri Bulkin <y.bulkin@eastbanctech.ru>
 */
@WebAppConfiguration
@ContextConfiguration(classes = {TestMvcConfig.class})
public class SecurityLayerTest extends AbstractTestNGSpringContextTests {

    @Resource
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeMethod
    public void setup() {

        mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilter(springSecurityFilterChain).build();
    }

    @Test
    public void loginRequiredTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/check-auth").header("ETR-Client", "native")).andExpect(
                MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void checkAuthAfterLogin() throws Exception {

//        mockMvc.perform(
//                MockMvcRequestBuilders.post("/api/login").param("secretary", "user").param("password", "test").header(
//                        "ETR-Client", "native")).andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/check-auth").header("ETR-Client", "native")).andExpect(
                MockMvcResultMatchers.status().isUnauthorized());

    }
}
