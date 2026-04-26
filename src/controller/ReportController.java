package controller;

import dao.FinancePromoterDAO;
import dao.FixedExpenseHistoryDAO;
import dao.VariableExpenseDAO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReportController {

    private FinancePromoterDAO financePromoterDAO = new FinancePromoterDAO();
    private FixedExpenseHistoryDAO fixedExpenseHistoryDAO = new FixedExpenseHistoryDAO();
    private VariableExpenseDAO variableExpenseDAO = new VariableExpenseDAO();

    public void showGeneralReport(LocalDate start, LocalDate end) {

        if (start.isAfter(end)) {
            System.out.println("Erro: data inicial maior que final.");
            return;
        }

        var totals = financePromoterDAO.getTotalByTypeAndPeriod(start, end);

        BigDecimal gastosPromotores = BigDecimal.ZERO;

        String[] tiposGasto = {
                "BONIFICACAO",
                "AJUDA_CUSTO",
                "ASO",
                "EPI"
        };

        for (String type : tiposGasto) {
            gastosPromotores = gastosPromotores.add(
                    totals.getOrDefault(type, BigDecimal.ZERO)
            );
        }

        BigDecimal descontos = totals.getOrDefault("DESCONTO", BigDecimal.ZERO);

        BigDecimal fixedTotal = fixedExpenseHistoryDAO.getTotalByPeriod(start, end);
        BigDecimal variableTotal = variableExpenseDAO.getTotalByPeriod(start, end);

        BigDecimal totalGeral = gastosPromotores
                .add(fixedTotal)
                .add(variableTotal);

        System.out.println("\n=== RELATÓRIO GERAL DA EMPRESA ===");
        System.out.println("Período: " + formatDate(start) + " até " + formatDate(end));
        System.out.println("----------------------------------");

        System.out.println("Financeiro dos promotores: R$ " + gastosPromotores);
        System.out.println("Despesas fixas:           R$ " + fixedTotal);
        System.out.println("Despesas variáveis:       R$ " + variableTotal);

        System.out.println("----------------------------------");
        System.out.println("Descontos aplicados:      R$ " + descontos);
        System.out.println("----------------------------------");

        System.out.println("TOTAL REAL DE GASTOS:     R$ " + totalGeral);
    }

    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }
}