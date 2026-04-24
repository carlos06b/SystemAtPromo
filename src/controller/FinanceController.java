package controller;

import dao.FinancePromoterDAO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class FinanceController {

    private FinancePromoterDAO dao = new FinancePromoterDAO();

    public void listByPeriod(LocalDate start, LocalDate end) {

        List<String> list = dao.findByPeriodWithPromoterName(start, end);

        if (list.isEmpty()) {
            System.out.println("Nenhum registro financeiro nesse período.");
            return;
        }

        for (String line : list) {
            System.out.println(line);
        }
    }

    public void showReportByPeriod(LocalDate start, LocalDate end) {

        System.out.println("\n=== RELATÓRIO FINANCEIRO ===");

        listByPeriod(start, end);

        BigDecimal total = dao.getTotalByPeriod(start, end);

        System.out.println("---------------------------");
        System.out.println("TOTAL: R$ " + total);
    }

    public void showReportByTypeAndPeriod(LocalDate start, LocalDate end) {

        Map<String, BigDecimal> totals = dao.getTotalByTypeAndPeriod(start, end);

        if (totals.isEmpty()) {
            System.out.println("Nenhum registro nesse período.");
            return;
        }

        BigDecimal geral = BigDecimal.ZERO;

        System.out.println("\n=== RELATÓRIO POR TIPO ===");

        for (Map.Entry<String, BigDecimal> entry : totals.entrySet()) {
            System.out.println(entry.getKey() + ": R$ " + entry.getValue());
            geral = geral.add(entry.getValue());
        }

        System.out.println("---------------------------");
        System.out.println("TOTAL GERAL: R$ " + geral);
    }
}