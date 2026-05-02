package view;

import model.User;

import javax.swing.*;
import java.awt.*;

public class MenuFinanceiroFrame extends JFrame {

    private final Color ORANGE = new Color(255, 102, 0);
    private final Color BLACK = new Color(18, 18, 18);
    private final Color WHITE = Color.WHITE;
    private final Color LIGHT_GRAY = new Color(245, 245, 245);
    private final Color DARK_GRAY = new Color(55, 55, 55);

    public MenuFinanceiroFrame(User user) {
        setTitle("Sistema At Promo - Financeiro");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(WHITE);

        mainPanel.add(createSidebar(user), BorderLayout.WEST);
        mainPanel.add(createContentPanel(user), BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createSidebar(User user) {
        JPanel sidebar = new JPanel(null);
        sidebar.setPreferredSize(new Dimension(270, 600));
        sidebar.setBackground(BLACK);

        JLabel logoAt = new JLabel("AT");
        logoAt.setForeground(ORANGE);
        logoAt.setFont(new Font("Segoe UI", Font.BOLD, 42));
        logoAt.setBounds(35, 30, 80, 50);
        sidebar.add(logoAt);

        JLabel logoPromo = new JLabel("PROMO");
        logoPromo.setForeground(WHITE);
        logoPromo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logoPromo.setBounds(95, 40, 150, 35);
        sidebar.add(logoPromo);

        JPanel line = new JPanel();
        line.setBackground(ORANGE);
        line.setBounds(35, 95, 190, 4);
        sidebar.add(line);

        JLabel setor = new JLabel("Módulo Financeiro");
        setor.setForeground(WHITE);
        setor.setFont(new Font("Segoe UI", Font.BOLD, 18));
        setor.setBounds(35, 135, 210, 30);
        sidebar.add(setor);

        JLabel userLabel = new JLabel("<html>Usuário:<br><b>" + user.getName() + "</b></html>");
        userLabel.setForeground(new Color(210, 210, 210));
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setBounds(35, 175, 210, 55);
        sidebar.add(userLabel);

        JLabel cargoLabel = new JLabel("Cargo: " + user.getJobTittle());
        cargoLabel.setForeground(new Color(170, 170, 170));
        cargoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cargoLabel.setBounds(35, 235, 210, 25);
        sidebar.add(cargoLabel);

        JLabel footer = new JLabel("Sistema interno");
        footer.setForeground(new Color(150, 150, 150));
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.setBounds(35, 515, 200, 25);
        sidebar.add(footer);

        return sidebar;
    }

    private JPanel createContentPanel(User user) {
        JPanel panel = new JPanel(null);
        panel.setBackground(LIGHT_GRAY);

        JLabel title = new JLabel("Painel Financeiro");
        title.setForeground(BLACK);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setBounds(45, 35, 400, 40);
        panel.add(title);

        JLabel subtitle = new JLabel("Gerencie solicitações, relatórios, folha de pagamento e despesas da empresa.");
        subtitle.setForeground(DARK_GRAY);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setBounds(48, 78, 620, 30);
        panel.add(subtitle);

        JPanel cardsPanel = new JPanel(new GridLayout(3, 2, 22, 22));
        cardsPanel.setBackground(LIGHT_GRAY);
        cardsPanel.setBounds(45, 135, 585, 290);

        cardsPanel.add(createMenuButton(
                "Solicitações",
                "Aprovar ou rejeitar solicitações do RH",
                e -> new RequestFrame(user)
        ));

        cardsPanel.add(createMenuButton(
                "Relatórios",
                "Consultar relatórios financeiros por período",
                e -> new ReportFrame()
        ));

        cardsPanel.add(createMenuButton(
                "Clientes / Indústrias",
                "Cadastrar e gerenciar indústrias atendidas",
                e -> new ClientFrame().setVisible(true)
        ));

        cardsPanel.add(createMenuButton(
                "Faturamento",
                "Controlar cobranças, emissão e pagamentos",
                e -> new InvoiceFrame()
        ));

        cardsPanel.add(createMenuButton(
                "Folha de Pagamento",
                "Visualizar salários, adicionais e descontos",
                e -> new PayrollFrame()
        ));

        cardsPanel.add(createMenuButton(
                "Despesas",
                "Controlar despesas fixas e variáveis",
                e -> new ExpenseFrame()
        ));

        panel.add(cardsPanel);

        JButton logoutButton = new JButton("Sair do sistema");
        logoutButton.setBounds(455, 480, 175, 42);
        logoutButton.setBackground(BLACK);
        logoutButton.setForeground(WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        panel.add(logoutButton);

        return panel;
    }

    private JButton createMenuButton(String title, String description, java.awt.event.ActionListener action) {
        JButton button = new JButton("<html><b style='font-size:14px;'>" + title + "</b><br><span style='font-size:10px;'>"
                + description + "</span></html>");

        button.setBackground(WHITE);
        button.setForeground(BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(ORANGE);
                button.setForeground(WHITE);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(WHITE);
                button.setForeground(BLACK);
            }
        });

        return button;
    }
}