package com.bank.loanpricing.dto;

import com.bank.loanpricing.model.Loan;
import org.springframework.beans.BeanUtils;

public class LoanMapper {

    public static UserLoanResponse toUserDto(Loan loan) {
        UserLoanResponse dto = new UserLoanResponse();
        BeanUtils.copyProperties(loan, dto);
        return dto;
    }

    public static AdminLoanResponse toAdminDto(Loan loan) {
        AdminLoanResponse dto = new AdminLoanResponse();
        BeanUtils.copyProperties(loan, dto);
        return dto;
    }
}

