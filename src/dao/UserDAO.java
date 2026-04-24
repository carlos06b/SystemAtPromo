package dao;

import database.ConnectionFactory;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

        //  salvar
        public void save(User u) {

            String sql = "INSERT INTO user (name, email, password, jobTittle) VALUES (?, ?, ?, ?)";

            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, u.getName());
                stmt.setString(2, u.getEmail());
                stmt.setString(3, u.getPassword());
                stmt.setString(4, u.getJobTittle());

                stmt.executeUpdate();

                System.out.println("Usuário cadastrado!");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // buscar por email
        public User findByEmail(String email) {

            String sql = "SELECT * FROM user WHERE email = ?";

            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, email);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return new User(
                            rs.getInt("iduser"),
                            rs.getString("email"),
                            rs.getString("jobTittle"),
                            rs.getString("name"),
                            rs.getString("password")
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        // login
        public User login(String email, String password) {

            String sql = "SELECT * FROM user WHERE email = ? AND password = ?";

            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, email);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return new User(
                            rs.getInt("iduser"),
                            rs.getString("email"),
                            rs.getString("jobTittle"),
                            rs.getString("name"),
                            rs.getString("password")
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
}

