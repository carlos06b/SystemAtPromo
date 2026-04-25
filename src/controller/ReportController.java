package controller;

import dao.FinancePromoterDAO;
import dao.FixedExpenseHistoryDAO;
import dao.VariableExpenseDAO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReportController {

    private FinancePromoterDAO financePromoterDAO = new FinancePromoterDAO();
    private FixedExpenseHistoryDAO fixedExpenseHistoryDAO = new FixedExpenseHistoryDAO();
    private VariableExpenseDAO variableExpenseDAO = new VariableExpenseDAO();

    public void showGeneralReport(LocalDate start, LocalDate end) {

        if (start.isAfter(end)) {
            System.out.println("Erro: data inicial maior que final.");
            return;
        }

        BigDecimal promoterTotal = financePromoterDAO.getTotalByPeriod(start, end);
        BigDecimal fixedTotal = fixedExpenseHistoryDAO.getTotalByPeriod(start, end);
        BigDecimal variableTotal = variableExpenseDAO.getTotalByPeriod(start, end);

        BigDecimal total = promoterTotal
                .add(fixedTotal)
                .add(variableTotal);

        System.out.println("\n=== RELATÓRIO GERAL DA EMPRESA ===");
        System.out.println("Período: " + start + " até " + end);
        System.out.println("----------------------------------");
        System.out.println("Financeiro dos promotores: R$ " + promoterTotal);
        System.out.println("Despesas fixas mensais:    R$ " + fixedTotal);
        System.out.println("Despesas variáveis:        R$ " + variableTotal);
        System.out.println("----------------------------------");
        System.out.println("TOTAL GERAL:               R$ " + total);
    }
}