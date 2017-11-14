package services.invoicereceiver.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import services.invoicereceiver.model.Invoice;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ObjectMapper mapper;
    
    @PostMapping
    void upload(@RequestBody Invoice invoice) throws JsonProcessingException, InterruptedException, ExecutionException {
        kafkaTemplate.send("invoices", invoice.getSeller(), mapper.writeValueAsString(invoice)).get();
    }
}
