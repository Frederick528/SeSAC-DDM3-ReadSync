package com.ohgiraffers.backendapi.domain.credit.service;

import com.ohgiraffers.backendapi.domain.credit.entity.Credit;
import com.ohgiraffers.backendapi.domain.credit.entity.CreditType;
import com.ohgiraffers.backendapi.domain.credit.repository.CreditRepository;
import com.ohgiraffers.backendapi.domain.credit.repository.CreditTypeRepository;
import com.ohgiraffers.backendapi.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditServiceTest {

    @Mock
    private CreditTypeRepository creditTypeRepository;

    @Mock
    private CreditRepository creditRepository;

    @InjectMocks
    private CreditService creditService;

    @Test
    @DisplayName("크레딧 사용 성공 - 잔액 차감 확인")
    void useCredits_Success() {
        // given
        Long userId = 1L;
        int amountToUse = 50;
        User user = User.builder().id(userId).build();

        // Mock CreditType
        CreditType creditType = CreditType.builder().creditTypeId(1L).user(user).build();

        // Mock Credits (active)
        // Scenario: 2 credit items. First has 30, Second has 30. Total 60. Use 50.
        // Expect: First -> 0 (USED_UP), Second -> 10.
        Credit credit1 = Credit.builder().creditsId(101L).creditType(creditType).credits(30).status("ACTIVE").build();
        Credit credit2 = Credit.builder().creditsId(102L).creditType(creditType).credits(30).status("ACTIVE").build();

        // Logic uses getBalance first, which calls findAll and then findActiveCredits
        // We need to mock findAll first
        when(creditTypeRepository.findAll()).thenReturn(List.of(creditType));

        // Mock Repository calls
        // Since logic iterates types and finds credits for each, we mock that.
        when(creditRepository.findByCreditType_CreditTypeIdAndStatus(1L, "ACTIVE"))
                .thenReturn(List.of(credit1, credit2));

        // when
        creditService.useCredits(user, amountToUse);

        // then
        // Verify credit1 was used up
        assertEquals(0, credit1.getCredits());
        assertEquals("USED_UP", credit1.getStatus());

        // Verify credit2 was partially used
        assertEquals(10, credit2.getCredits());
        assertEquals("ACTIVE", credit2.getStatus());

        verify(creditRepository, times(2)).save(any(Credit.class));
    }
}
