package com.merchant.wallet.wallet.controller;

import com.merchant.wallet.wallet.service.BalanceCalculator;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/balance")
public class BalanceController {
    
    private final BalanceCalculator balanceCalculator = new BalanceCalculator();
    
    @PostMapping("/add-credit")
    public BalanceResponse addCredit(@RequestBody CreditRequest request) {
        BigDecimal newBalance = balanceCalculator.addCredit(request.getCurrentBalance(), request.getCreditAmount());
        return new BalanceResponse(newBalance);
    }
    
    @PostMapping("/subtract-debit")
    public BalanceResponse subtractDebit(@RequestBody DebitRequest request) {
        BigDecimal newBalance = balanceCalculator.subtractDebit(request.getCurrentBalance(), request.getDebitAmount());
        return new BalanceResponse(newBalance);
    }
    
    public static class CreditRequest {
        private BigDecimal currentBalance;
        private BigDecimal creditAmount;
        
        public BigDecimal getCurrentBalance() { return currentBalance; }
        public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }
        public BigDecimal getCreditAmount() { return creditAmount; }
        public void setCreditAmount(BigDecimal creditAmount) { this.creditAmount = creditAmount; }
    }
    
    public static class DebitRequest {
        private BigDecimal currentBalance;
        private BigDecimal debitAmount;
        
        public BigDecimal getCurrentBalance() { return currentBalance; }
        public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }
        public BigDecimal getDebitAmount() { return debitAmount; }
        public void setDebitAmount(BigDecimal debitAmount) { this.debitAmount = debitAmount; }
    }
    
    public static class BalanceResponse {
        private BigDecimal balance;
        
        public BalanceResponse(BigDecimal balance) { this.balance = balance; }
        public BigDecimal getBalance() { return balance; }
    }
}