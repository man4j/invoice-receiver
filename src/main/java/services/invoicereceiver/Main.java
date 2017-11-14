package services.invoicereceiver;

import org.apache.kafka.clients.admin.NewTopic;
import org.protobeans.kafka.annotation.EnableKafkaMessaging;
import org.protobeans.mvc.MvcEntryPoint;
import org.protobeans.mvc.annotation.EnableMvc;
import org.protobeans.undertow.annotation.EnableUndertow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import services.invoicereceiver.controller.InvoiceController;

@EnableUndertow
@EnableMvc
@EnableKafkaMessaging(brokerList = "s:brokerList")
@ComponentScan(basePackageClasses = {InvoiceController.class})
public class Main {
    @Bean
    public NewTopic invoicesTopic() {
        return new NewTopic("invoices", 12, (short) 2);
    }
    
    public static void main(String[] args) {        
        MvcEntryPoint.run(Main.class);
    }
}
