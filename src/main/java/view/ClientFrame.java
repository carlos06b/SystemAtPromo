package view;

import controller.ClientController;
import model.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class ClientFrame extends JFrame {

    private final ClientController clientController = new ClientController();

    private final Color ORANGE = new Color(255, 102, 0);
    private final Color BLACK = new Color(18, 18, 18);
    private final Color WHITE = Color.WHITE;
    private final Color LIGHT_GRAY = new Color(245, 245, 245);
    private final Color DARK_GRAY = new Color(55, 55, 55);
    private final Color RED = new Color(180, 40, 40);

    private JTextField txtSearch;
    private JComboBox<String> cbStatusFilter;
    private JComboBox<String> cbCompanyFilter;

    private JTable table;
    private DefaultTableModel tableModel;

    public ClientFrame() {
        setTitle("Sistema At Promo - Clientes / Indústrias");
        setSize(980, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(LIGHT_GRAY);
        add(mainPanel);

        createHeader(mainPanel);
        createFilterPanel(mainPanel);
        createTable(mainPanel);
        createActionButtons(mainPanel);

        loadClients();

        setVisible(true);
    }

    private void createHeader(JPanel panel) {
        JPanel header = new JPanel(null);
        header.setBackground(BLACK);
        header.setBounds(0, 0, 980, 95);
        panel.add(header);

        JLabel title = new JLabel("Clientes / Indústrias");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setBounds(30, 18, 400, 35);
        header.add(title);

        JLabel subtitle = new JLabel("Cadastro, filtros e controle de vínculo AT / TEJO");
        subtitle.setForeground(new Color(210, 210, 210));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setBounds(32, 55, 600, 25);
        header.add(subtitle);

        JPanel orangeLine = new JPanel();
        orangeLine.setBackground(ORANGE);
        orangeLine.setBounds(30, 85, 900, 4);
        header.add(orangeLine);
    }

    private void createFilterPanel(JPanel panel) {
        JPanel filterPanel = new JPanel(null);
        filterPanel.setBackground(WHITE);
        filterPanel.setBounds(30, 120, 910, 95);
        filterPanel.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225)));
        panel.add(filterPanel);

        JLabel lblSearch = new JLabel("Buscar por nome");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSearch.setBounds(20, 12, 150, 20);
        filterPanel.add(lblSearch);

        txtSearch = new JTextField();
        txtSearch.setBounds(20, 38, 260, 32);
        filterPanel.add(txtSearch);

        JLabel lblStatus = new JLabel("Status");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStatus.setBounds(300, 12, 100, 20);
        filterPanel.add(lblStatus);

        cbStatusFilter = new JComboBox<>(new String[]{"Todos", "Ativos", "Inativos"});
        cbStatusFilter.setBounds(300, 38, 130, 32);
        filterPanel.add(cbStatusFilter);

        JLabel lblCompany = new JLabel("Vínculo");
        lblCompany.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCompany.setBounds(450, 12, 100, 20);
        filterPanel.add(lblCompany);

        cbCompanyFilter = new JComboBox<>(new String[]{"Todos", "AT", "TEJO"});
        cbCompanyFilter.setBounds(450, 38, 130, 32);
        filterPanel.add(cbCompanyFilter);

        JButton btnFilter = createDarkButton("Filtrar");
        btnFilter.setBounds(610, 38, 100, 32);
        btnFilter.addActionListener(e -> applyFilters());
        filterPanel.add(btnFilter);

        JButton btnRefresh = createSecondaryButton("Atualizar");
        btnRefresh.setBounds(720, 38, 100, 32);
        btnRefresh.addActionListener(e -> resetFilters());
        filterPanel.add(btnRefresh);

        JButton btnRegister = createPrimaryButton("Novo Cliente");
        btnRegister.setBounds(830, 38, 70, 32);
        btnRegister.setText("Novo");
        btnRegister.addActionListener(e -> openRegisterDialog());
        filterPanel.add(btnRegister);
    }

    private void createTable(JPanel panel) {
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Indústria", "CNPJ", "Telefone", "Vínculo", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(230, 230, 230));

        JTableHeader header = table.getTableHeader();
        header.setBackground(BLACK);
        header.setForeground(WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 235, 910, 270);
        panel.add(scrollPane);

        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        table.getColumnModel().getColumn(1).setPreferredWidth(280);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
    }

    private void createActionButtons(JPanel panel) {
        JButton btnActivate = createPrimaryButton("Ativar");
        btnActivate.setBounds(600, 525, 100, 36);
        btnActivate.addActionListener(e -> activateClient());
        panel.add(btnActivate);

        JButton btnDeactivate = createDangerButton("Inativar");
        btnDeactivate.setBounds(715, 525, 100, 36);
        btnDeactivate.addActionListener(e -> deactivateClient());
        panel.add(btnDeactivate);

        JButton btnClose = createDarkButton("Fechar");
        btnClose.setBounds(830, 525, 110, 36);
        btnClose.addActionListener(e -> dispose());
        panel.add(btnClose);
    }

    private void openRegisterDialog() {
        JTextField txtName = new JTextField();
        JTextField txtCnpj = new JTextField();
        JTextField txtPhone = new JTextField();
        JComboBox<String> cbCompanyLink = new JComboBox<>(new String[]{"AT", "TEJO"});

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Nome da indústria:"));
        panel.add(txtName);
        panel.add(new JLabel("CNPJ:"));
        panel.add(txtCnpj);
        panel.add(new JLabel("Telefone:"));
        panel.add(txtPhone);
        panel.add(new JLabel("Vínculo:"));
        panel.add(cbCompanyLink);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Cadastrar Indústria",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                clientController.registerClient(
                        txtName.getText(),
                        txtCnpj.getText(),
                        txtPhone.getText(),
                        cbCompanyLink.getSelectedItem().toString()
                );

                JOptionPane.showMessageDialog(this, "Indústria cadastrada com sucesso.");
                loadClients();

            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadClients() {
        try {
            fillTable(clientController.listAll());
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilters() {
        String search = txtSearch.getText().trim().toLowerCase();
        String status = cbStatusFilter.getSelectedItem().toString();
        String company = cbCompanyFilter.getSelectedItem().toString();

        List<Client> clients = clientController.listAll();

        List<Client> filtered = clients.stream()
                .filter(c -> search.isEmpty() || c.getName().toLowerCase().contains(search))
                .filter(c -> status.equals("Todos")
                        || (status.equals("Ativos") && c.isActive())
                        || (status.equals("Inativos") && !c.isActive()))
                .filter(c -> company.equals("Todos") || company.equals(c.getCompanyLink()))
                .toList();

        fillTable(filtered);
    }

    private void resetFilters() {
        txtSearch.setText("");
        cbStatusFilter.setSelectedIndex(0);
        cbCompanyFilter.setSelectedIndex(0);
        loadClients();
    }

    private void activateClient() {
        int id = getSelectedClientId();

        if (id == -1) {
            return;
        }

        clientController.activateClient(id);
        loadClients();
    }

    private void deactivateClient() {
        int id = getSelectedClientId();

        if (id == -1) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja inativar esta indústria?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            clientController.deactivateClient(id);
            loadClients();
        }
    }

    private int getSelectedClientId() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma indústria na tabela.");
            return -1;
        }

        return (int) tableModel.getValueAt(row, 0);
    }

    private void fillTable(List<Client> clients) {
        tableModel.setRowCount(0);

        for (Client client : clients) {
            tableModel.addRow(new Object[]{
                    client.getId(),
                    client.getName(),
                    client.getCnpj(),
                    client.getPhone(),
                    client.getCompanyLink(),
                    client.isActive() ? "Ativo" : "Inativo"
            });
        }
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(ORANGE);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createDarkButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BLACK);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(WHITE);
        button.setForeground(BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(RED);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}