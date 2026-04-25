package dao;

import database.ConnectionFactory;
import model.VariableExpense;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VariableExpenseDAO {

    public void save(VariableExpense expense) {

        String sql = "INSERT INTO variable_expense " +
                "(name, amount, id_promoter, date, status, payment_date, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, expense.getName());
            stmt.setBigDecimal(2, expense.getAmount());
            stmt.setInt(3, expense.getIdPromoter());
            stmt.setDate(4, java.sql.Date.valueOf(expense.getDate()));
            stmt.setBoolean(5, expense.isStatus());

            if (expense.getPaymentDate() != null) {
                stmt.setDate(6, java.sql.Date.valueOf(expense.getPaymentDate()));
            } else {
                stmt.setNull(6, java.sql.Types.DATE);
            }

            stmt.setString(7, expense.getDescription());

            stmt.executeUpdate();

            System.out.println("Despesa variável cadastrada com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<VariableExpense> findByPeriod(java.time.LocalDate start, java.time.LocalDate end) {

        List<VariableExpense> list = new ArrayList<>();

        String sql = "SELECT * FROM variable_expense WHERE date BETWEEN ? AND ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(start));
            stmt.setDate(2, java.sql.Date.valueOf(end));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(buildVariableExpense(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<VariableExpense> findByPromoter(int idPromoter) {

        List<VariableExpense> list = new ArrayList<>();

        String sql = "SELECT * FROM variable_expense WHERE id_promoter = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPromoter);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(buildVariableExpense(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void markAsPaid(int id, java.time.LocalDate paymentDate) {

        String sql = "UPDATE variable_expense SET status = true, payment_date = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(paymentDate));
            stmt.setInt(2, id);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Despesa variável marcada como paga!");
            } else {
                System.out.println("Despesa variável não encontrada.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {

        String sql = "DELETE FROM variable_expense WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Despesa variável excluída!");
            } else {
                System.out.println("Despesa variável não encontrada.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VariableExpense buildVariableExpense(ResultSet rs) throws Exception {

        VariableExpense expense = new VariableExpense();

        expense.setId(rs.getInt("id"));
        expense.setName(rs.getString("name"));
        expense.setAmount(rs.getBigDecimal("amount"));
        expense.setIdPromoter(rs.getInt("id_promoter"));
        expense.setDate(rs.getDate("date").toLocalDate());
        expense.setStatus(rs.getBoolean("status"));

        if (rs.getDate("payment_date") != null) {
            expense.setPaymentDate(rs.getDate("payment_date").toLocalDate());
        }

        expense.setDescription(rs.getString("description"));

        return expense;
    }

    public BigDecimal getTotalByPeriod(LocalDate start, LocalDate end) {

        String sql = "SELECT SUM(amount) AS total FROM variable_expense WHERE date BETWEEN ? AND ?";

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