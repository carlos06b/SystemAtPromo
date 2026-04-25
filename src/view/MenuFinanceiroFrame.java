package view;

import model.User;

import javax.swing.*;

public class MenuFinanceiroFrame extends JFrame {

    public MenuFinanceiroFrame(User user) {

        setTitle("Menu Financeiro - " + user.getName());
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton btnRequests = new JButton("Solicitações");
        btnRequests.setBounds(100, 60, 200, 40);
        btnRequests.addActionListener(e -> new RequestFrame(user));

        JButton btnReports = new JButton("Relatórios");
        btnReports.setBounds(100, 120, 200, 40);
        btnReports.addActionListener(e -> new ReportFrame());

        JButton btnPayroll = new JButton("Folha de Pagamento");
        btnPayroll.setBounds( 100, 180, 200, 40);
        add(btnPayroll);
        btnPayroll.addActionListener(e -> new PayrollFrame());

        JButton btnExpenses = new JButton("Despesas");
        btnExpenses.setBounds(100, 240, 200, 40);

        setLayout(null);
        add(btnRequests);
        add(btnReports);
        add(btnExpenses);

        setVisible(true);
    }
}