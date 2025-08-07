package org.openapitools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yjailbir.payment_service.controllers.PaymentsController;
import ru.yjailbir.payment_service.dto.PaymentRequest;

@WebFluxTest(controllers = PaymentsController.class)
@Import(PaymentsController.class)
class PaymentsControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PaymentsController controller;

    @BeforeEach
    void resetBalance() {
        controller.setBalance(100_000L);
    }

    @Test
    void getBalance_returnsDefaultBalance() {
        webTestClient.get()
                .uri("/payments/balance/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.userId").isEqualTo("-1")
                .jsonPath("$.balance").isEqualTo(100_000);
    }

    @Test
    void makePayment_deductsAmountFromBalance() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(10_000L);
        paymentRequest.setUserId("1");

        webTestClient.post()
                .uri("/payments/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(paymentRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.remainingBalance").isEqualTo(90_000);
    }

    @Test
    void makePayment_multipleCallsUpdateBalance() {
        PaymentRequest request1 = new PaymentRequest();
        request1.setUserId("1");
        request1.setAmount(30_000L);

        PaymentRequest request2 = new PaymentRequest();
        request2.setUserId("1");
        request2.setAmount(20_000L);

        webTestClient.post()
                .uri("/payments/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.remainingBalance").isEqualTo(70_000);

        webTestClient.post()
                .uri("/payments/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request2)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.remainingBalance").isEqualTo(50_000);
    }

}