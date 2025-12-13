package com.merchant.wallet.transaction.controller;

import com.merchant.wallet.transaction.service.FeeCalculator;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/fees")
public class FeeController {
    
    private final FeeCalculator feeCalculator = new FeeCalculator();
    
    @PostMapping("/calculate")
    public FeeResponse calculateFee(@RequestBody FeeRequest request) {
        BigDecimal fee = feeCalculator.calculateFee(request.getAmount(), request.getTransactionType());
        return new FeeResponse(fee);
    }
    
    public static class FeeRequest {
        private BigDecimal amount;
        private String transactionType;
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getTransactionType() { return transactionType; }
        public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    }
    
    public static class FeeResponse {
        private BigDecimal fee;
        
        public FeeResponse(BigDecimal fee) { this.fee = fee; }
        public BigDecimal getFee() { return fee; }
    }
}