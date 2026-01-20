package com.ohgiraffers.backendapi.domain.credit.service;

import com.ohgiraffers.backendapi.domain.credit.entity.Credit;
import com.ohgiraffers.backendapi.domain.credit.entity.CreditType;
import com.ohgiraffers.backendapi.domain.credit.repository.CreditRepository;
import com.ohgiraffers.backendapi.domain.credit.repository.CreditTypeRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditTypeRepository creditTypeRepository;
    private final CreditRepository creditRepository;

    @Transactional
    public void addCredits(User user, int amount, String creditName, int expiryDays) {
        CreditType creditType = creditTypeRepository.findByUser_IdAndCreditName(user.getId(), creditName)
                .orElseGet(() -> {
                    CreditType newType = CreditType.builder()
                            .user(user)
                            .creditName(creditName)
                            .baseExpiryDays(expiryDays)
                            .build();
                    return creditTypeRepository.save(newType);
                });

        Credit credit = Credit.builder()
                .creditType(creditType)
                .credits(amount)
                .status("ACTIVE")
                .build();

        creditRepository.save(credit);
    }

    @Transactional(readOnly = true)
    public int getBalance(User user) {
        // This is a simplified balance check. It sums up all ACTIVE credits.
        // In a real scenario, we might iterate over all CreditTypes for the user.
        // For now, let's assume we sum everything for the user.
        // But since CreditRepository finds by CreditType, we need to find all types
        // first.
        // Or we can add a custom query in Repository to sum by UserId across types.
        // Let's keep it simple: find types, then sum credits.

        // TODO: Optimize with a single query
        return creditTypeRepository.findAll().stream() // Ideally filter by user in DB
                .filter(type -> type.getUser().getId().equals(user.getId()))
                .mapToInt(type -> creditRepository
                        .findByCreditType_CreditTypeIdAndStatus(type.getCreditTypeId(), "ACTIVE")
                        .stream()
                        .mapToInt(Credit::getCredits)
                        .sum())
                .sum();
    }

    @Transactional
    public void useCredits(User user, int amount) {
        int balance = getBalance(user);
        if (balance < amount) {
            throw new IllegalArgumentException("Not enough credits");
        }

        int remainingAmount = amount;

        // Strategy: Use credits from oldest to newest (FIFO) among ACTIVE credits
        // We need to fetch all active credits for the user
        List<CreditType> userCreditTypes = creditTypeRepository.findAll().stream()
                .filter(type -> type.getUser().getId().equals(user.getId()))
                .toList();

        for (CreditType type : userCreditTypes) {
            List<Credit> credits = creditRepository.findByCreditType_CreditTypeIdAndStatus(type.getCreditTypeId(),
                    "ACTIVE");
            // Sort by CreatedAt if needed, but ID is usually sufficient for FIFO

            for (Credit credit : credits) {
                if (remainingAmount <= 0)
                    break;

                int available = credit.getCredits();
                if (available > remainingAmount) {
                    // Update credit with reduced amount
                    // Since entity is immutable-ish (SuperBuilder), we might need setter or direct
                    // field access if managed
                    // But Credit entity fields are private and no setters shown in view_file.
                    // Wait, @SuperBuilder and @Getter only.
                    // I need to check if I can update 'credits' field.
                    // If not, I need to add a method to Credit entity "deduct(int amount)".

                    // Assuming I'll add deduct method or use reflection if desperate, but better
                    // add method.
                    // For now, let's assume deduct method exists or I will add it.
                    // Actually, let's use a workaround: create new credit with lower amount? No,
                    // update is better.
                    // I will ADD 'deduct' and 'useUp' methods to Credit entity in the next step.
                    credit.deduct(remainingAmount);
                    remainingAmount = 0;
                } else {
                    credit.useUp(); // Set status to USED_UP
                    remainingAmount -= available;
                }
                creditRepository.save(credit); // Explicit save if not using dirty checking or just to be safe
            }
        }
    }
}
