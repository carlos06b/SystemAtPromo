package dao;

import database.ConnectionFactory;
import model.Promoter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PromoterDAO {

    public int save(Promoter promoter) {

        String sql = "INSERT INTO promoter (name, cpf, phone, date_birth, active, salary) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, promoter.getName());
            stmt.setString(2, promoter.getCpf());
            stmt.setString(3, promoter.getPhone());
            stmt.setDate(4, Date.valueOf(promoter.getDateBirth()));
            stmt.setBoolean(5, promoter.isActive());
            stmt.setBigDecimal(6, promoter.getSalary());

            stmt.executeUpdate();

            // pegar ID gerado pelo MySQL
            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                int id = rs.getInt(1);
                promoter.setId(id);
                return id;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public List<Promoter> findAll() {

        List<Promoter> list = new ArrayList<>();

        String sql = "SELECT * FROM promoter";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Promoter p = new Promoter();

                p.setId(rs.getInt("idpromoter"));
                p.setName(rs.getString("name"));
                p.setCpf(rs.getString("cpf"));
                p.setPhone(rs.getString("phone"));
                p.setDateBirth(rs.getDate("date_birth").toLocalDate());
                p.setActive(rs.getBoolean("active"));
                p.setSalary(rs.getBigDecimal("salary"));

                list.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public Promoter findById(int id) {

        String sql = "SELECT * FROM promoter WHERE idpromoter = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                Promoter p = new Promoter();

                p.setId(rs.getInt("idpromoter"));
                p.setName(rs.getString("name"));
                p.setCpf(rs.getString("cpf"));
                p.setPhone(rs.getString("phone"));
                p.setDateBirth(rs.getDate("date_birth").toLocalDate());
                p.setActive(rs.getBoolean("active"));
                p.setSalary(rs.getBigDecimal("salary"));

                return p;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void update(Promoter promoter) {

        String sql = "UPDATE promoter SET name = ?, cpf = ?, phone = ?, date_birth = ?, active = ?, salary = ? WHERE idpromoter = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, promoter.getName());
            stmt.setString(2, promoter.getCpf());
            stmt.setString(3, promoter.getPhone());
            stmt.setDate(4, java.sql.Date.valueOf(promoter.getDateBirth()));
            stmt.setBoolean(5, promoter.isActive());
            stmt.setBigDecimal(6, promoter.getSalary());
            stmt.setInt(7, promoter.getId());

            stmt.executeUpdate();

            System.out.println("Promotor atualizado com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {

        String checkSql = "SELECT idpromoter FROM promoter WHERE idpromoter = ?";
        String deleteSql = "DELETE FROM promoter WHERE idpromoter = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            // verificar se existe
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Promotor não encontrado!");
                return;
            }

            // deletar
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {

                deleteStmt.setInt(1, id);
                deleteStmt.executeUpdate();

                System.out.println("Promotor excluído com sucesso!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}