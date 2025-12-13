package com.merchant.wallet.wallet.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import java.math.BigDecimal;

@Table("wallets")
public class Wallet {

	@Id
	@Column("wallet_id")
	private Long walletId;
	
	@Column("user_id")
	private Long userId;
	
	@Column("merchant_id")
	private Long merchantId;

	private BigDecimal balance = BigDecimal.ZERO;
	private String currency = "ZAR";
	
	@Column("wallet_alias")
	private String walletAlias;
	
	private boolean active = true;

	public Long getWalletId() {
		return walletId;
	}

	public void setWalletId(Long walletId) {
		this.walletId = walletId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getWalletAlias() {
		return walletAlias;
	}

	public void setWalletAlias(String walletAlias) {
		this.walletAlias = walletAlias;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
