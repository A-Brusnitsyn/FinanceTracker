package org.brusnitsyn.financetracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.brusnitsyn.financetracker.model.dto.TransactionCreateRequest;
import org.brusnitsyn.financetracker.model.dto.TransactionResponse;
import org.brusnitsyn.financetracker.model.enums.TransactionType;
import org.brusnitsyn.financetracker.service.TransactionService;
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
    public List<TransactionResponse> getTransactions(
            @Parameter(description = "User ID", required = true, example = "1")
            @RequestParam Long userId,
            @Parameter(description = "Filter by account ID", example = "3")
            @RequestParam(required = false) Long accountId,
            @Parameter(description = "Filter by category ID", example = "5")
            @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Filter by transaction type", example = "EXPENSE")
            @RequestParam(required = false) TransactionType type,
            @Parameter(description = "Start date (ISO format)", example = "2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "End date (ISO format)", example = "2024-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return transactionService.getTransactions(userId, accountId, categoryId, type, from, to);
    }
}
