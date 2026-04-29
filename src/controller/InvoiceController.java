package controller;

import dao.ClientDAO;
import dao.InvoiceDAO;
import model.Client;
import model.Invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class InvoiceController {

    private final InvoiceDAO invoiceDAO;
    private final ClientDAO clientDAO;

    public InvoiceController() {
        this.invoiceDAO = new InvoiceDAO();
        this.clientDAO = new ClientDAO();
    }

    public void createPendingInvoice(int clientId, BigDecimal amount, String description, LocalDate dueDate) {
        Client client = clientDAO.findById(clientId);

        if (client == null) {
            throw new RuntimeException("Indústria não encontrada.");
        }

        if (!client.isActive()) {
            throw new RuntimeException("Não é possível criar faturamento para indústria inativa.");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("O valor do faturamento deve ser maior que zero.");
        }

        if (dueDate == null) {
            throw new RuntimeException("A data de faturamento é obrigatória.");
        }

        Invoice invoice = new Invoice();
        invoice.setClientId(clientId);
        invoice.setAmount(amount);
        invoice.setDescription(description);
        invoice.setDueDate(dueDate);
        invoice.setStatus("PENDENTE");

        invoiceDAO.save(invoice);
    }

    public List<Invoice> listByPeriod(LocalDate start, LocalDate end) {
        validatePeriod(start, end);
        return invoiceDAO.findByPeriod(start, end);
    }

    public List<Invoice> listPendingByPeriod(LocalDate start, LocalDate end) {
        validatePeriod(start, end);
        return invoiceDAO.findPendingByPeriod(start, end);
    }

    public List<Invoice> listIssuedNotPaid() {
        return invoiceDAO.findIssuedNotPaid();
    }

    public void markAsIssued(int invoiceId) {
        Invoice invoice = invoiceDAO.findById(invoiceId);

        if (invoice == null) {
            throw new RuntimeException("Faturamento não encontrado.");
        }

        if (!"PENDENTE".equals(invoice.getStatus())) {
            throw new RuntimeException("Apenas faturamentos pendentes podem ser marcados como faturados.");
        }

        invoiceDAO.markAsIssued(invoiceId);
    }

    public void markAsPaid(int invoiceId, LocalDate paymentDate) {
        Invoice invoice = invoiceDAO.findById(invoiceId);

        if (invoice == null) {
            throw new RuntimeException("Faturamento não encontrado.");
        }

        if (!"FATURADO".equals(invoice.getStatus())) {
            throw new RuntimeException("Apenas faturamentos já emitidos podem ser marcados como pagos.");
        }

        if (paymentDate == null) {
            throw new RuntimeException("A data de pagamento é obrigatória.");
        }

        invoiceDAO.markAsPaid(invoiceId, paymentDate);
    }

    private void validatePeriod(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new RuntimeException("Informe a data inicial e a data final.");
        }

        if (end.isBefore(start)) {
            throw new RuntimeException("A data final não pode ser menor que a data inicial.");
        }
    }
}