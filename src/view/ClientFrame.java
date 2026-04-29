package view;

import controller.ClientController;
import model.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClientFrame extends JFrame {

    private final ClientController clientController = new ClientController();

    private final Color ORANGE = new Color(255, 102, 0);
    private final Color BLACK = new Color(18, 18, 18);
    private final Color WHITE = Color.WHITE;
    private final Color LIGHT_GRAY = new Color(245, 245, 245);
    private final Color DARK_GRAY = new Color(55, 55, 55);

    private JTextField txtName;
    private JTextField txtCnpj;
    private JTextField txtPhone;
    private JTextField txtSearch;

    private JComboBox<String> cbCompanyLink;
    private JComboBox<String> cbStatusFilter;
    private JComboBox<String> cbCompanyFilter;

    private JTable table;
    private DefaultTableModel tableModel;

    public ClientFrame() {
        setTitle("Clientes / Indústrias");
        setSize(980, 690);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(LIGHT_GRAY);
        add(mainPanel);

        createHeader(mainPanel);
        createForm(mainPanel);
        createFilters(mainPanel);
        createTable(mainPanel);
        createActions(mainPanel);

        loadClients();
        setVisible(true);
    }

    private void createHeader(JPanel panel) {
        JLabel title = new JLabel("Clientes / Indústrias");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(BLACK);
        title.setBounds(30, 25, 400, 35);
        panel.add(title);

        JLabel subtitle = new JLabel("Cadastro e controle de indústrias (AT / TEJO)");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(DARK_GRAY);
        subtitle.setBounds(32, 62, 600, 25);
        panel.add(subtitle);
    }

    private void createForm(JPanel panel) {
        JPanel formPanel = new JPanel(null);
        formPanel.setBackground(WHITE);
        formPanel.setBounds(30, 105, 910, 120);
        formPanel.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225)));
        panel.add(formPanel);

        JLabel lblName = new JLabel("Indústria");
        lblName.setBounds(20, 15, 120, 20);
        formPanel.add(lblName);

        txtName = new JTextField();
        txtName.setBounds(20, 40, 250, 34);
        formPanel.add(txtName);

        JLabel lblCnpj = new JLabel("CNPJ");
        lblCnpj.setBounds(290, 15, 120, 20);
        formPanel.add(lblCnpj);

        txtCnpj = new JTextField();
        txtCnpj.setBounds(290, 40, 160, 34);
        formPanel.add(txtCnpj);

        JLabel lblPhone = new JLabel("Telefone");
        lblPhone.setBounds(470, 15, 120, 20);
        formPanel.add(lblPhone);

        txtPhone = new JTextField();
        txtPhone.setBounds(470, 40, 140, 34);
        formPanel.add(txtPhone);

        JLabel lblCompany = new JLabel("Vínculo");
        lblCompany.setBounds(630, 15, 120, 20);
        formPanel.add(lblCompany);

        cbCompanyLink = new JComboBox<>(new String[]{"AT", "TEJO"});
        cbCompanyLink.setBounds(630, 40, 90, 34);
        formPanel.add(cbCompanyLink);

        JButton btnSave = createPrimaryButton("Cadastrar");
        btnSave.setBounds(740, 40, 140, 34);
        btnSave.addActionListener(e -> saveClient());
        formPanel.add(btnSave);
    }

    private void createFilters(JPanel panel) {
        JPanel filterPanel = new JPanel(null);
        filterPanel.setBackground(WHITE);
        filterPanel.setBounds(30, 245, 910, 75);
        filterPanel.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225)));
        panel.add(filterPanel);

        JLabel lblSearch = new JLabel("Buscar por nome");
        lblSearch.setBounds(20, 10, 150, 20);
        filterPanel.add(lblSearch);

        txtSearch = new JTextField();
        txtSearch.setBounds(20, 35, 250, 30);
        filterPanel.add(txtSearch);

        JLabel lblStatus = new JLabel("Status");
        lblStatus.setBounds(290, 10, 100, 20);
        filterPanel.add(lblStatus);

        cbStatusFilter = new JComboBox<>(new String[]{"Todos", "Ativos", "Inativos"});
        cbStatusFilter.setBounds(290, 35, 120, 30);
        filterPanel.add(cbStatusFilter);

        JLabel lblCompany = new JLabel("Vínculo");
        lblCompany.setBounds(430, 10, 100, 20);
        filterPanel.add(lblCompany);

        cbCompanyFilter = new JComboBox<>(new String[]{"Todos", "AT", "TEJO"});
        cbCompanyFilter.setBounds(430, 35, 120, 30);
        filterPanel.add(cbCompanyFilter);

        JButton btnFilter = createDarkButton("Filtrar");
        btnFilter.setBounds(580, 35, 100, 30);
        btnFilter.addActionListener(e -> applyFilters());
        filterPanel.add(btnFilter);

        JButton btnRefresh = createDarkButton("Atualizar");
        btnRefresh.setBounds(690, 35, 100, 30);
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbStatusFilter.setSelectedIndex(0);
            cbCompanyFilter.setSelectedIndex(0);
            loadClients();
        });
        filterPanel.add(btnRefresh);

        JButton btnClear = createDarkButton("Limpar");
        btnClear.setBounds(800, 35, 100, 30);
        btnClear.addActionListener(e -> clearFields());
        filterPanel.add(btnClear);
    }

    private void createTable(JPanel panel) {
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Indústria", "CNPJ", "Telefone", "Vínculo", "Status"}, 0
        );

        table = new JTable(tableModel);
        table.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 340, 910, 200);
        panel.add(scrollPane);

        // esconder ID
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        // largura das colunas
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
    }

    private void createActions(JPanel panel) {
        JButton btnActivate = createPrimaryButton("Ativar");
        btnActivate.setBounds(600, 560, 100, 35);
        btnActivate.addActionListener(e -> activateClient());
        panel.add(btnActivate);

        JButton btnDeactivate = createPrimaryButton("Inativar");
        btnDeactivate.setBounds(710, 560, 100, 35);
        btnDeactivate.addActionListener(e -> deactivateClient());
        panel.add(btnDeactivate);

        JButton btnClose = createDarkButton("Fechar");
        btnClose.setBounds(820, 560, 120, 35);
        btnClose.addActionListener(e -> dispose());
        panel.add(btnClose);
    }

    private void saveClient() {
        try {
            clientController.registerClient(
                    txtName.getText(),
                    txtCnpj.getText(),
                    txtPhone.getText(),
                    cbCompanyLink.getSelectedItem().toString()
            );

            JOptionPane.showMessageDialog(this, "Cadastrado com sucesso.");
            clearFields();
            loadClients();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void loadClients() {
        fillTable(clientController.listAll());
    }

    private void applyFilters() {
        String search = txtSearch.getText().toLowerCase();
        String status = cbStatusFilter.getSelectedItem().toString();
        String company = cbCompanyFilter.getSelectedItem().toString();

        List<Client> clients = clientController.listAll();

        List<Client> filtered = clients.stream()
                .filter(c -> c.getName().toLowerCase().contains(search))
                .filter(c -> status.equals("Todos") ||
                        (status.equals("Ativos") && c.isActive()) ||
                        (status.equals("Inativos") && !c.isActive()))
                .filter(c -> company.equals("Todos") || c.getCompanyLink().equals(company))
                .toList();

        fillTable(filtered);
    }

    private void activateClient() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = (int) tableModel.getValueAt(row, 0);
        clientController.activateClient(id);
        loadClients();
    }

    private void deactivateClient() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = (int) tableModel.getValueAt(row, 0);
        clientController.deactivateClient(id);
        loadClients();
    }

    private void fillTable(List<Client> clients) {
        tableModel.setRowCount(0);

        for (Client c : clients) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getName(),
                    c.getCnpj(),
                    c.getPhone(),
                    c.getCompanyLink(),
                    c.isActive() ? "Ativo" : "Inativo"
            });
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtCnpj.setText("");
        txtPhone.setText("");
    }

    private JButton createPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(ORANGE);
        b.setForeground(WHITE);
        return b;
    }

    private JButton createDarkButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(BLACK);
        b.setForeground(WHITE);
        return b;
    }
}