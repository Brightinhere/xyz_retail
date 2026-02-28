package org.brightinhere.xyz_retail.application.port;

public interface EmailSender {

    void sendOrderConfirmation(
            String customerName,
            String customerEmail,
            String orderId,
            String orderQuantity,
            String totalAmount
    );
}