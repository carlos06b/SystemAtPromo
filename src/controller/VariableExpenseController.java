package controller;

import dao.PromoterDAO;
import dao.VariableExpenseDAO;
import model.VariableExpense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class VariableExpenseController {

    private VariableExpenseDAO dao = new VariableExpenseDAO();
    private PromoterDAO promoterDAO = new PromoterDAO();

    public void register(String name, BigDecimal amount, int idPromoter,
                         LocalDate date, String description) {

        if (promoterDAO.findById(idPromoter) == null) {
            System.out.println("Promotor não existe.");
            return;
        }

        VariableExpense expense = new VariableExpense();

        expense.setName(name);
        expense.setAmount(amount);
        expense.setIdPromoter(idPromoter);
        expense.setDate(date);
        expense.setStatus(false);
        expense.setPaymentDate(null);
        expense.setDescription(description);

        dao.save(expense);
    }

    public void listByPeriod(LocalDate start, LocalDate end) {

        if (start.isAfter(end)) {
            System.out.println("Erro: data inicial maior que final.");
            return;
        }

        List<VariableExpense> list = dao.findByPeriod(start, end);

        if (list.isEmpty()) {
            System.out.println("Nenhuma despesa variável nesse período.");
            return;
        }

        for (VariableExpense e : list) {
            printExpense(e);
        }
    }

    public void listByPromoter(int idPromoter) {

        List<VariableExpense> list = dao.findByPromoter(idPromoter);

        if (list.isEmpty()) {
            System.out.println("Nenhuma despesa variável para esse promotor.");
            return;
        }

        for (VariableExpense e : list) {
            printExpense(e);
        }
    }

    public void markAsPaid(int id, LocalDate paymentDate) {
        dao.markAsPaid(id, paymentDate);
    }

    public void delete(int id) {
        dao.delete(id);
    }

    private void printExpense(VariableExpense e) {
        System.out.println(
                e.getId() + " | " +
                        e.getName() + " | " +
                        "Promotor: " + e.getIdPromoter() + " | " +
                        "R$ " + e.getAmount() + " | " +
                        e.getDate() + " | " +
                        (e.isStatus() ? "PAGO" : "PENDENTE") + " | " +
                        "Pagamento: " + e.getPaymentDate() + " | " +
                        e.getDescription()
        );
    }
}