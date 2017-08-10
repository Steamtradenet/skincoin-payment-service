package net.steamtrade.payment.backend.exceptions;

import lombok.Getter;

/**
 * Created by sasha on 17.02.16.
 */
public enum Error {


    INCORRECT_FORMAT_JSON("Incorrect format JSON"),
	UNEXPECTED_EXCEPTION("Unexpected exception"),

	INSUFFICIENT_AMOUNT("User doesn't have sufficient amount", false),
	CREATE_PAYMENT_FAILED("Create payment failed"),
	INCORRECT_PAYMENT("Incorrect payment"),
	AUTHENTICATION_FAILED("Authentication failed"),

	// payment-service
	ETHEREUM_UNAVAILABLE("Ethereum service is unavailable"),
	GAS_PRICE_OVERLIMIT("Gas price is too expensive"),
	TOO_LOW_GAS_SPECIFIED("Gas price is too expensive"),
	UNFINISHED_PAYMENTS("There are too many active payments"),
	SEND_TRANSACTION_FAILED("Can't send a transaction"),
	FORBIDDEN_TO_DELETE_FIRST_ADMIN("Forbidden to delete the first admin")

	;

	@Getter private String description;
	@Getter private boolean printStackTrace = true;

    Error(String description) {
        this.description = description;
    }

	Error(String description, boolean printStackTrace) {
		this.description = description;
		this.printStackTrace = printStackTrace;
	}
}
