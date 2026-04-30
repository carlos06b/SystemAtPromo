package controller;

import dao.FinancePromoterDAO;
import dao.PromoterDAO;
import model.FinancePromoter;
import model.Promoter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PayrollController {

    private PromoterDAO promoterDAO = new PromoterDAO();
    private FinancePromoterDAO financePromoterDAO = new FinancePromoterDAO();

    public String generatePayroll(LocalDate start, LocalDate end, String promoterType) {
        if (start.isAfter(end)) {
            return "Erro: data inicial não pode ser maior que a final.";
        }

        List<Promoter> promoters = promoterDAO.findAll();

        StringBuilder report = new StringBuilder();

        report.append("=== FOLHA DE PAGAMENTO ===\n");
        report.append("Período: ")
                .append(formatDate(start))
                .append(" até ")
                .append(formatDate(end))
                .append("\n");

        report.append("Tipo: ").append(promoterType).append("\n");
        report.append("------------------------------------------------------------\n\n");

        BigDecimal totalBase = BigDecimal.ZERO;
        BigDecimal totalDesconto = BigDecimal.ZERO;
        BigDecimal totalLiquido = BigDecimal.ZERO;

        boolean found = false;

        for (Promoter promoter : promoters) {
            if (!promoter.isActive()) {
                continue;
            }

            if (!promoterType.equalsIgnoreCase("TODOS")
                    && !promoter.getType().equalsIgnoreCase(promoterType)) {
                continue;
            }

            found = true;

            List<FinancePromoter> launches = financePromoterDAO.findByPromoterAndPeriod(
                    promoter.getId(),
                    start,
                    end
            );

            BigDecimal desconto = BigDecimal.ZERO;

            for (FinancePromoter finance : launches) {
                String type = finance.getType();
                BigDecimal amount = finance.getAmount();

                if (type == null || amount == null) {
                    continue;
                }

                if ("DESCONTO".equalsIgnoreCase(type)) {
                    desconto = desconto.add(amount);
                }
            }

            BigDecimal baseSalary = promoter.getSalary();

            if (baseSalary == null) {
                baseSalary = BigDecimal.ZERO;
            }

            BigDecimal totalToPay = baseSalary.subtract(desconto);

            totalBase = totalBase.add(baseSalary);
            totalDesconto = totalDesconto.add(desconto);
            totalLiquido = totalLiquido.add(totalToPay);

            report.append("Promotor: ").append(promoter.getName()).append("\n");
            report.append("Tipo: ").append(promoter.getType()).append("\n");
            report.append("Salário/Base: R$ ").append(baseSalary).append("\n");
            report.append("Descontos: R$ ").append(desconto).append("\n");
            report.append("TOTAL LÍQUIDO A PAGAR: R$ ").append(totalToPay).append("\n");
            report.append("------------------------------------------------------------\n");
        }

        if (!found) {
            return "Nenhum promotor encontrado para esse filtro.";
        }

        report.append("\n=== RESUMO DA FOLHA ===\n");
        report.append("Total Salário/Base: R$ ").append(totalBase).append("\n");
        report.append("Total Descontos: R$ ").append(totalDesconto).append("\n");
        report.append("------------------------------------------------------------\n");
        report.append("TOTAL LÍQUIDO DA FOLHA: R$ ").append(totalLiquido).append("\n");

        return report.toString();
    }

    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }
}