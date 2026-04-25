package view;

import controller.UserController;
import model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private UserController userController;

    public LoginFrame() {

        userController = new UserController();

        setTitle("Sistema At Promo - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel title = new JLabel("Sistema At Promo");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBounds(100, 20, 200, 30);
        add(title);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 70, 80, 25);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(130, 70, 200, 25);
        add(emailField);

        JLabel passwordLabel = new JLabel("Senha:");
        passwordLabel.setBounds(50, 110, 80, 25);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(130, 110, 200, 25);
        add(passwordField);

        JButton loginButton = new JButton("Entrar");
        loginButton.setBounds(130, 160, 120, 30);
        add(loginButton);

        loginButton.addActionListener(e -> login());

        setVisible(true);
    }

    private void login() {

        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        User user = userController.login(email, password);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Email ou senha inválidos");
            return;
        }

        JOptionPane.showMessageDialog(this, "Login realizado!");

        dispose();

        if (user.getJobTittle().equalsIgnoreCase("RH")) {
            new MenuRHFrame(user);
        } else {
            new MenuFinanceiroFrame(user);
        }
    }
}