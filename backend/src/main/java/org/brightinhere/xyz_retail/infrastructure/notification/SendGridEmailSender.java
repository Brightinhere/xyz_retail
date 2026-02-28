package org.brightinhere.xyz_retail.infrastructure.notification;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brightinhere.xyz_retail.application.port.EmailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendGridEmailSender implements EmailSender {

    private final SendGrid sendGrid;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.template.order-confirmation}")
    private String templateId;

    @Override
    @Async
    @Retry(name = "sendgrid")
    @CircuitBreaker(name = "sendgrid", fallbackMethod = "fallback")
    public void sendOrderConfirmation(
            String customerName,
            String customerEmail,
            String orderId,
            String orderQuantity,
            String totalAmount
    ) {

        if (customerEmail == null || customerEmail.isBlank()) {
            log.warn("Skipping email, customer email missing for order {}", orderId);
            return;
        }

        try {
            send(customerName, customerEmail, orderId, orderQuantity, totalAmount);
        } catch (Exception e) {
            log.error("Email failed for order {}", orderId, e);
            throw new RuntimeException(e);
        }
    }

    private void send(
            String customerName,
            String customerEmail,
            String orderId,
            String orderQuantity,
            String totalAmount
    ) throws IOException {

        Email from = new Email(fromEmail);
        Email to = new Email(customerEmail);

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setTemplateId(templateId);

        Personalization personalization = new Personalization();
        personalization.addTo(to);

        personalization.addDynamicTemplateData(
                "sale_date",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
        );

        personalization.addDynamicTemplateData("customer_name", customerName);
        personalization.addDynamicTemplateData("customer_email", customerEmail);
        personalization.addDynamicTemplateData("order_number", orderId);
        personalization.addDynamicTemplateData("order_price", totalAmount);
        personalization.addDynamicTemplateData("order_quantity", orderQuantity);

        mail.addPersonalization(personalization);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendGrid.api(request);

        if (response.getStatusCode() >= 400) {
            throw new RuntimeException("SendGrid error: " + response.getBody());
        }

        log.info("Email sent successfully for order {}", orderId);
    }

    public void fallback(
            String customerName,
            String customerEmail,
            String orderId,
            String orderQuantity,
            String totalAmount,
            Throwable throwable
    ) {
        log.error("Email permanently failed for order {}. Reason: {}",
                orderId,
                throwable.getMessage());
    }
}