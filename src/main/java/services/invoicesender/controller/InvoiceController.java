package services.invoicesender.controller;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import services.invoicesender.model.Invoice;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ObjectMapper mapper;
    
    @PostMapping
    void upload(@RequestBody Invoice invoice) throws JsonProcessingException, InterruptedException, ExecutionException {
        kafkaTemplate.send("invoices", invoice.getSeller(), mapper.writeValueAsString(invoice)).get();
        
        logger.info("Invoice sent to: " + invoice.getSeller() + " from controller");
    }
}
