package dao;

import database.ConnectionFactory;
import model.Invoice;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    public void save(Invoice invoice) {
        String sql = """
                INSERT INTO invoice (id_client, amount, description, due_date, status)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, invoice.getClientId());
            stmt.setBigDecimal(2, invoice.getAmount());
            stmt.setString(3, invoice.getDescription());
            stmt.setDate(4, Date.valueOf(invoice.getDueDate()));
            stmt.setString(5, "PENDENTE");

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar faturamento pendente", e);
        }
    }

    public List<Invoice> findByPeriod(LocalDate start, LocalDate end) {
        String sql = """
                SELECT *
                FROM invoice
                WHERE due_date BETWEEN ? AND ?
                ORDER BY due_date
                """;

        List<Invoice> invoices = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar faturamentos por período", e);
        }

        return invoices;
    }

    public List<Invoice> findPendingByPeriod(LocalDate start, LocalDate end) {
        String sql = """
                SELECT *
                FROM invoice
                WHERE due_date BETWEEN ? AND ?
                AND status = 'PENDENTE'
                ORDER BY due_date
                """;

        List<Invoice> invoices = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar faturamentos pendentes", e);
        }

        return invoices;
    }

    public List<Invoice> findIssuedNotPaid() {
        String sql = """
                SELECT *
                FROM invoice
                WHERE status = 'FATURADO'
                ORDER BY issue_date
                """;

        List<Invoice> invoices = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar faturamentos ainda não pagos", e);
        }

        return invoices;
    }

    public void markAsIssued(int id) {
        String sql = """
                UPDATE invoice
                SET status = 'FATURADO',
                    issue_date = CURDATE()
                WHERE id = ?
                AND status = 'PENDENTE'
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao marcar como faturado", e);
        }
    }

    public void markAsPaid(int id, LocalDate paymentDate) {
        String sql = """
                UPDATE invoice
                SET status = 'PAGO',
                    payment_date = ?
                WHERE id = ?
                AND status = 'FATURADO'
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(paymentDate));
            stmt.setInt(2, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao marcar como pago", e);
        }
    }

    public Invoice findById(int id) {
        String sql = "SELECT * FROM invoice WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToInvoice(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar faturamento por ID", e);
        }

        return null;
    }

    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();

        invoice.setId(rs.getInt("id"));
        invoice.setClientId(rs.getInt("id_client"));
        invoice.setAmount(rs.getBigDecimal("amount"));
        invoice.setDescription(rs.getString("description"));
        invoice.setDueDate(rs.getDate("due_date").toLocalDate());

        Date issueDate = rs.getDate("issue_date");
        if (issueDate != null) {
            invoice.setIssueDate(issueDate.toLocalDate());
        }

        Date paymentDate = rs.getDate("payment_date");
        if (paymentDate != null) {
            invoice.setPaymentDate(paymentDate.toLocalDate());
        }

        invoice.setStatus(rs.getString("status"));

        return invoice;
    }
}