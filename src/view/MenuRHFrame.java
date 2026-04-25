package view;

import model.User;

import javax.swing.*;

public class MenuRHFrame extends JFrame {

    public MenuRHFrame(User user) {

        setTitle("Menu RH - " + user.getName());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton btnPromoters = new JButton("Promotores");
        btnPromoters.setBounds(100, 80, 200, 40);

        btnPromoters.addActionListener(e -> new PromoterFrame());

        JButton btnRequests = new JButton("Solicitações");
        btnRequests.setBounds(100, 140, 200, 40);

        btnRequests.addActionListener(e -> new RequestFrame(user));

        setLayout(null);
        add(btnPromoters);
        add(btnRequests);

        setVisible(true);
    }
}