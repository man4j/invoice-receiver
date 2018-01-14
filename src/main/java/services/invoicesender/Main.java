package services.invoicesender;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.NewTopic;
import org.protobeans.kafka.annotation.EnableKafkaMessaging;
import org.protobeans.mvc.MvcEntryPoint;
import org.protobeans.mvc.annotation.EnableMvc;
import org.protobeans.undertow.annotation.EnableUndertow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import services.invoicesender.controller.InvoiceController;
import services.invoicesender.model.Invoice;

@EnableUndertow
@EnableMvc
@EnableKafkaMessaging(brokerList = "s:brokerList")
@ComponentScan(basePackageClasses = {InvoiceController.class})
public class Main {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ObjectMapper mapper;

    @Bean
    public NewTopic invoicesTopic() {
        return new NewTopic("invoices", 24, (short) 2);
    }
    
    @EventListener(ContextRefreshedEvent.class)
    void sendInvoices() throws JsonProcessingException, InterruptedException, ExecutionException {
        while (true) {
            Invoice invoice = new Invoice("seller_" + UUID.randomUUID().toString().substring(0, 8).replaceAll("-", ""), "customer_" + UUID.randomUUID().toString().substring(0, 8).replaceAll("-", ""));
        
            kafkaTemplate.send("invoices", invoice.getSeller(), mapper.writeValueAsString(invoice)).get();
            
            Thread.sleep(100);
        }
    }
    
    public static void main(String[] args) {
        MvcEntryPoint.run(Main.class);
    }
}
