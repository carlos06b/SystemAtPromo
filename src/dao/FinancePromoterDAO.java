package dao;

import database.ConnectionFactory;
import model.FinancePromoter;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class FinancePromoterDAO {

    public void save(FinancePromoter f) {

        String sql = "INSERT INTO finance_promoter (id_promoter, type, amount, date, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, f.getIdPromoter());
            stmt.setString(2, f.getType());
            stmt.setBigDecimal(3, f.getAmount());
            stmt.setDate(4, Date.valueOf(f.getDate()));
            stmt.setString(5, f.getStatus());

            stmt.executeUpdate();

            System.out.println("Registro financeiro salvo!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> findByPeriodWithPromoterName(LocalDate start, LocalDate end) {

        List<String> list = new ArrayList<>();

        String sql = "SELECT fp.id, p.name, fp.type, fp.amount, fp.date, fp.status " +
                "FROM finance_promoter fp " +
                "JOIN promoter p ON fp.id_promoter = p.idpromoter " +
                "WHERE fp.date BETWEEN ? AND ? " +
                "ORDER BY fp.date";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                String line =
                        rs.getInt("id") + " | " +
                                rs.getString("name") + " | " +
                                rs.getString("type") + " | " +
                                "R$ " + rs.getBigDecimal("amount") + " | " +
                                rs.getDate("date").toLocalDate() + " | " +
                                rs.getString("status");

                list.add(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public BigDecimal getTotalByPeriod(LocalDate start, LocalDate end) {

        BigDecimal total = BigDecimal.ZERO;

        String sql = "SELECT SUM(amount) AS total FROM finance_promoter WHERE date BETWEEN ? AND ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                total = rs.getBigDecimal("total");
                if (total == null) total = BigDecimal.ZERO;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return total;
    }

    public Map<String, BigDecimal> getTotalByTypeAndPeriod(LocalDate start, LocalDate end) {

        Map<String, BigDecimal> totals = new LinkedHashMap<>();

        String sql = "SELECT type, SUM(amount) AS total FROM finance_promoter " +
                "WHERE date BETWEEN ? AND ? GROUP BY type";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                totals.put(rs.getString("type"), rs.getBigDecimal("total"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return totals;
    }
}