package me.exrates.model;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter@Setter
public class Wallet {
	
	private int id;
	private int currencyId;
	private User user;
	private BigDecimal activeBalance;
	private BigDecimal reservedBalance;
	private String name;

	public Wallet() {

	}

	public Wallet(int currencyId, User user, BigDecimal activeBalance) {
		this.currencyId = currencyId;
		this.user = user;
		this.activeBalance = activeBalance;
	}

	public Wallet(int currencyId, int userId, BigDecimal activeBalance) {
		this.currencyId = currencyId;
		this.user = new User();
		user.setId(userId);
		this.activeBalance = activeBalance;
	}

	public static Wallet nullWallet(int userId, int currencyId) {
		return new Wallet(currencyId, userId, BigDecimal.ZERO);
	}



	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
	
		Wallet wallet = (Wallet) o;
	
		if (id != wallet.id) return false;
		if (currencyId != wallet.currencyId) return false;
		if (!user.equals(wallet.user)) return false;
		if (activeBalance != null ? !activeBalance.equals(wallet.activeBalance) : wallet.activeBalance != null) return false;
		if (reservedBalance != null ? !reservedBalance.equals(wallet.reservedBalance) : wallet.reservedBalance != null) return false;
		return name != null ? name.equals(wallet.name) : wallet.name == null;
	}

	@Override
	public int hashCode() {
		int result;
		result = id;
		result = 31 * result + currencyId;
		result = 31 * result + (activeBalance != null ? activeBalance.hashCode() : 0);
		result = 31 * result + (reservedBalance != null ? reservedBalance.hashCode() : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Wallet{" +
				"id=" + id +
				", currencyId=" + currencyId +
				", userId=" + user.getEmail() +
				", activeBalance=" + activeBalance +
				", reservedBalance=" + reservedBalance +
				", name='" + name + '\'' +
				'}';
	}
}