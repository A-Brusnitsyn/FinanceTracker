package org.brusnitsyn.financetracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brusnitsyn.financetracker.model.dto.TransactionCreateRequest;
import org.brusnitsyn.financetracker.model.dto.TransactionResponse;
import org.brusnitsyn.financetracker.model.enums.TransactionType;
import org.brusnitsyn.financetracker.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Endpoints for managing financial transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @Operation(
            summary = "Create new transaction",
            description = "Creates a new income or expense transaction. Updates account balance automatically."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User, account or category not found"),
            @ApiResponse(responseCode = "409", description = "Insufficient funds for expense")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createTransaction(@Valid @RequestBody TransactionCreateRequest request) {
        log.info("Create transaction request");
        transactionService.createTransaction(request);
    }

    @Operation(
            summary = "Get user transactions",
            description = "Returns transactions for a user with optional filters by account, category, type, and date range"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })

    @GetMapping
    public Page<TransactionResponse> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) Long categoryId
    ) {
        return transactionService.getTransactions(
                page, size, from, to, type, accountId, categoryId
        );
    }
}
