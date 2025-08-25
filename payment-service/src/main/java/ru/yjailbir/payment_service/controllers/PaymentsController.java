package ru.yjailbir.payment_service.controllers;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yjailbir.payment_service.dto.BalanceResponse;
import ru.yjailbir.payment_service.dto.PaymentRequest;
import ru.yjailbir.payment_service.dto.PaymentResponse;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.processing.Generated;
import java.util.concurrent.atomic.AtomicLong;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-08-04T21:17:01.787026800+03:00[Europe/Moscow]", comments = "Generator version: 7.12.0")
@Controller
@RequestMapping("/payments")
public class PaymentsController implements DefaultApi {
    private AtomicLong balance = new AtomicLong(100_000L);

    @Override
    public Mono<ResponseEntity<BalanceResponse>> getBalance(String userId, ServerWebExchange exchange) {
        //Id добавлено заранее для следующего дз (на всякий случай. При ненадобности будет убрано)
        //В целом оно не пригодилось, но пусть останется. Оно работает - я его не трогаю
        //Энивей всё по тз
        return Mono.just(ResponseEntity.ok(new BalanceResponse("-1", balance.get())));
    }

    @Override
    public Mono<ResponseEntity<PaymentResponse>> makePayment(Mono<PaymentRequest> paymentRequest, ServerWebExchange exchange) {
        return paymentRequest.map(request -> {
            balance.addAndGet(-request.getAmount());
            return ResponseEntity.ok(new PaymentResponse(true, balance.get()));
        });
    }

    //Костыль с прошлой дз
    //Для тестов
    public void setBalance(Long balance) {
        this.balance.set(balance);
    }
}
