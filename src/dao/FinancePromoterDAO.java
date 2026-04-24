package dao;

import database.ConnectionFactory;
import model.FinancePromoter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinancePromoterDAO {

    public void save(FinancePromoter f) {

        String sql = "INSERT INTO finance_promoter (id_promoter, type, amount, date, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, f.getIdPromoter());
            stmt.setString(2, f.getType());
            stmt.setBigDecimal(3, f.getAmount());
            stmt.setDate(4, java.sql.Date.valueOf(f.getDate()));
            stmt.setString(5, f.getStatus());

            stmt.executeUpdate();

            System.out.println("Registro financeiro salvo!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<FinancePromoter> findByPromoter(int idPromoter) {

        List<FinancePromoter> list = new ArrayList<>();

        String sql = "SELECT * FROM finance_promoter WHERE id_promoter = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPromoter);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                FinancePromoter f = new FinancePromoter();

                f.setId(rs.getInt("id"));
                f.setIdPromoter(rs.getInt("id_promoter"));
                f.setType(rs.getString("type"));
                f.setAmount(rs.getBigDecimal("amount"));
                f.setDate(rs.getDate("date").toLocalDate());
                f.setStatus(rs.getString("status"));

                list.add(f);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<FinancePromoter> findByPeriod(LocalDate start, LocalDate end) {

        List<FinancePromoter> list = new ArrayList<>();

        String sql = "SELECT * FROM finance_promoter WHERE date BETWEEN ? AND ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(start));
            stmt.setDate(2, java.sql.Date.valueOf(end));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                FinancePromoter f = new FinancePromoter();

                f.setId(rs.getInt("id"));
                f.setIdPromoter(rs.getInt("id_promoter"));
                f.setType(rs.getString("type"));
                f.setAmount(rs.getBigDecimal("amount"));
                f.setDate(rs.getDate("date").toLocalDate());
                f.setStatus(rs.getString("status"));

                list.add(f);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<FinancePromoter> findByPromoterAndPeriod(int idPromoter, LocalDate start, LocalDate end) {

        List<FinancePromoter> list = new ArrayList<>();

        String sql = """
        SELECT * FROM finance_promoter
        WHERE id_promoter = ?
        AND date BETWEEN ? AND ?
    """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPromoter);
            stmt.setDate(2, java.sql.Date.valueOf(start));
            stmt.setDate(3, java.sql.Date.valueOf(end));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                FinancePromoter f = new FinancePromoter();

                f.setId(rs.getInt("id"));
                f.setIdPromoter(rs.getInt("id_promoter"));
                f.setType(rs.getString("type"));
                f.setAmount(rs.getBigDecimal("amount"));
                f.setDate(rs.getDate("date").toLocalDate());
                f.setStatus(rs.getString("status"));

                list.add(f);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void update(FinancePromoter f) {

        String sql = "UPDATE finance_promoter SET type=?, amount=?, date=?, status=? WHERE id=?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, f.getType());
            stmt.setBigDecimal(2, f.getAmount());
            stmt.setDate(3, java.sql.Date.valueOf(f.getDate()));
            stmt.setString(4, f.getStatus());
            stmt.setInt(5, f.getId());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Atualizado com sucesso!");
            } else {
                System.out.println("Registro não encontrado!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
