package ru.yjailbir.payment_service.controllers;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.yjailbir.payment_service.dto.BalanceResponse;
import ru.yjailbir.payment_service.dto.PaymentRequest;
import ru.yjailbir.payment_service.dto.PaymentResponse;


import org.springframework.http.ResponseEntity;

import javax.annotation.processing.Generated;
import java.util.concurrent.atomic.AtomicLong;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-08-04T21:17:01.787026800+03:00[Europe/Moscow]", comments = "Generator version: 7.12.0")
@RestController
@RequestMapping("/payments")
public class PaymentsController {
    private AtomicLong balance = new AtomicLong(100_000L);

    @GetMapping("/balance")
    public Mono<ResponseEntity<BalanceResponse>> getBalance() {

        return Mono.just(ResponseEntity.ok(new BalanceResponse("-1", balance.get())));
    }

    @PostMapping("/pay")
    public Mono<ResponseEntity<PaymentResponse>> makePayment(@RequestBody PaymentRequest paymentRequest) {
        return Mono.fromSupplier(() -> {
            balance.addAndGet(-paymentRequest.getAmount());
            return ResponseEntity.ok(new PaymentResponse(true, balance.get()));
        });
    }

    //Костыль с прошлой дз
    //Для тестов
    public void setBalance(Long balance) {
        this.balance.set(balance);
    }
}
