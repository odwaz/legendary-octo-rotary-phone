package com.merchant.wallet.transaction.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("transactions")
public class Transaction {

	@Id
	private Long id;
	
	@Column("transaction_id")
	private String transactionId;
	
	@Column("sender_wallet_id")
	private String senderWalletId;
	
	@Column("recipient_wallet_id")
	private String recipientWalletId;

	@Column("merchant_id")
	private Long merchantId;

	private BigDecimal amount;
	
	@Column("transaction_type")
	private String transactionType;
	
	@Column("created_at")
	private LocalDateTime createdAt;
	
	private String description;
	private String currency = "ZAR";
	private String status = "PENDING";

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getSenderWalletId() {
		return senderWalletId;
	}

	public void setSenderWalletId(String senderWalletId) {
		this.senderWalletId = senderWalletId;
	}

	public String getRecipientWalletId() {
		return recipientWalletId;
	}

	public void setRecipientWalletId(String recipientWalletId) {
		this.recipientWalletId = recipientWalletId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}
}
