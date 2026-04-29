package com.fictional.bank.response;

import java.util.List;

public record ListTransactionsResponse(
        List<TransactionResponse> transactions
) {}