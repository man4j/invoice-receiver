package services.invoicereceiver.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.protobeans.mvc.config.MvcInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import services.invoicereceiver.Main;
import services.invoicereceiver.model.Invoice;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=Main.class)
@WebAppConfiguration
public class InvoiceControllerTest {
    @Autowired
    private ObjectMapper mapper;
    
    private MockMvc mockMvc;
    
    @Before
    public void initMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(MvcInitializer.rootApplicationContext).build();
    }

    @Test
    public void shouldWork() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/invoice")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(mapper.writeValueAsString(new Invoice("seller1", "customer1"))))
                                              .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
