package dao;

import database.ConnectionFactory;
import model.FixedExpenseHistory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FixedExpenseHistoryDAO {

    public void generateMonthlyExpenses(int month, int year) {

        String selectSql = "SELECT * FROM fixed_expense";
        String insertSql = "INSERT INTO fixed_expense_history " +
                "(fixed_expense_id, name, amount, due_date, status, payment_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            ResultSet rs = selectStmt.executeQuery();

            while (rs.next()) {

                LocalDate originalDueDate = rs.getDate("due_date").toLocalDate();

                int day = originalDueDate.getDayOfMonth();

                LocalDate dueDate = LocalDate.of(year, month, 1)
                        .withDayOfMonth(Math.min(day, LocalDate.of(year, month, 1).lengthOfMonth()));

                insertStmt.setInt(1, rs.getInt("id"));
                insertStmt.setString(2, rs.getString("name"));
                insertStmt.setBigDecimal(3, rs.getBigDecimal("amount"));
                insertStmt.setDate(4, java.sql.Date.valueOf(dueDate));
                insertStmt.setBoolean(5, false);
                insertStmt.setNull(6, java.sql.Types.DATE);

                insertStmt.executeUpdate();
            }

            System.out.println("Despesas fixas do mês geradas com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<FixedExpenseHistory> findByPeriod(LocalDate start, LocalDate end) {

        List<FixedExpenseHistory> list = new ArrayList<>();

        String sql = "SELECT * FROM fixed_expense_history WHERE due_date BETWEEN ? AND ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(start));
            stmt.setDate(2, java.sql.Date.valueOf(end));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                FixedExpenseHistory h = new FixedExpenseHistory();

                h.setId(rs.getInt("id"));
                h.setFixedExpenseId(rs.getInt("fixed_expense_id"));
                h.setName(rs.getString("name"));
                h.setAmount(rs.getBigDecimal("amount"));
                h.setDueDate(rs.getDate("due_date").toLocalDate());
                h.setStatus(rs.getBoolean("status"));

                if (rs.getDate("payment_date") != null) {
                    h.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                }

                list.add(h);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void markAsPaid(int id, LocalDate paymentDate) {

        String sql = "UPDATE fixed_expense_history SET status = true, payment_date = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(paymentDate));
            stmt.setInt(2, id);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Despesa fixa mensal marcada como paga!");
            } else {
                System.out.println("Despesa fixa mensal não encontrada.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BigDecimal getTotalByPeriod(LocalDate start, LocalDate end) {

        String sql = "SELECT SUM(amount) AS total FROM fixed_expense_history WHERE due_date BETWEEN ? AND ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(start));
            stmt.setDate(2, java.sql.Date.valueOf(end));

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total");
                return total != null ? total : BigDecimal.ZERO;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }
}