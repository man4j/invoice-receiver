package services.invoicesender;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.clients.admin.NewTopic;
import org.protobeans.kafka.annotation.EnableKafkaMessaging;
import org.protobeans.mvc.MvcEntryPoint;
import org.protobeans.mvc.annotation.EnableMvc;
import org.protobeans.undertow.annotation.EnableUndertow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.core.KafkaTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import services.invoicesender.controller.InvoiceController;
import services.invoicesender.model.Invoice;

@EnableUndertow
@EnableMvc
@EnableKafkaMessaging(brokerList = "s:brokerList")
@ComponentScan(basePackageClasses = {InvoiceController.class})
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    private Thread sendThread;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ObjectMapper mapper;
    
    @Bean
    public NewTopic invoicesTopic() {
        return new NewTopic("invoices", 24, (short) 2);
    }
    
    @PostConstruct
    void sendInvoices() {
        sendThread = new Thread() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(100);
                        
                        Invoice invoice = new Invoice("seller_" + UUID.randomUUID().toString().substring(0, 8).replaceAll("-", ""), "customer_" + UUID.randomUUID().toString().substring(0, 8).replaceAll("-", ""));
                    
                        kafkaTemplate.send("invoices", invoice.getSeller(), mapper.writeValueAsString(invoice)).get();
                        
                        logger.info("Invoice sent to: " + invoice.getSeller());
                    } catch (@SuppressWarnings("unused") InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                }
            }
        };
    
        sendThread.start();
    }
    
    @PreDestroy
    void stop() throws InterruptedException {
        sendThread.interrupt();
        sendThread.join();
    }
    
    
    public static void main(String[] args) {
        MvcEntryPoint.run(Main.class);
    }
}
