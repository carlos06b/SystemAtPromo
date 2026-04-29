package view;

import controller.ClientController;
import controller.InvoiceController;
import model.Client;
import model.InvoiceView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InvoiceFrame extends JFrame {

    private final InvoiceController invoiceController = new InvoiceController();
    private final ClientController clientController = new ClientController();

    private final DateTimeFormatter BR_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Color ORANGE = new Color(255, 102, 0);
    private final Color BLACK = new Color(18, 18, 18);
    private final Color WHITE = Color.WHITE;
    private final Color LIGHT_GRAY = new Color(245, 245, 245);

    private JTextField txtStartDate;
    private JTextField txtEndDate;

    private JComboBox<String> cbStatusFilter;
    private JComboBox<String> cbCompanyFilter;

    private JTable table;
    private DefaultTableModel tableModel;

    public InvoiceFrame() {
        setTitle("Sistema At Promo - Faturamento");
        setSize(1120, 680);
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

        loadInvoices();

        setVisible(true);
    }

    private void createHeader(JPanel panel) {
        JPanel header = new JPanel(null);
        header.setBackground(BLACK);
        header.setBounds(0, 0, 1120, 95);
        panel.add(header);

        JLabel title = new JLabel("Faturamento");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setBounds(30, 18, 400, 35);
        header.add(title);

        JLabel subtitle = new JLabel("Controle de cobranças pendentes, faturadas e pagas");
        subtitle.setForeground(new Color(210, 210, 210));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setBounds(32, 55, 650, 25);
        header.add(subtitle);

        JPanel orangeLine = new JPanel();
        orangeLine.setBackground(ORANGE);
        orangeLine.setBounds(30, 85, 1040, 4);
        header.add(orangeLine);
    }

    private void createFilterPanel(JPanel panel) {
        JPanel filterPanel = new JPanel(null);
        filterPanel.setBackground(WHITE);
        filterPanel.setBounds(30, 120, 1040, 95);
        filterPanel.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225)));
        panel.add(filterPanel);

        JLabel lblStart = new JLabel("Data inicial");
        lblStart.setBounds(20, 12, 120, 20);
        filterPanel.add(lblStart);

        txtStartDate = new JTextField(LocalDate.now().withDayOfMonth(1).format(BR_FORMAT));
        txtStartDate.setBounds(20, 38, 120, 32);
        filterPanel.add(txtStartDate);

        JLabel lblEnd = new JLabel("Data final");
        lblEnd.setBounds(155, 12, 120, 20);
        filterPanel.add(lblEnd);

        txtEndDate = new JTextField(LocalDate.now().format(BR_FORMAT));
        txtEndDate.setBounds(155, 38, 120, 32);
        filterPanel.add(txtEndDate);

        JLabel lblStatus = new JLabel("Status");
        lblStatus.setBounds(295, 12, 120, 20);
        filterPanel.add(lblStatus);

        cbStatusFilter = new JComboBox<>(new String[]{"Todos", "PENDENTE", "FATURADO", "PAGO"});
        cbStatusFilter.setBounds(295, 38, 140, 32);
        filterPanel.add(cbStatusFilter);

        JLabel lblCompany = new JLabel("Vínculo");
        lblCompany.setBounds(455, 12, 120, 20);
        filterPanel.add(lblCompany);

        cbCompanyFilter = new JComboBox<>(new String[]{"Todos", "AT", "TEJO"});
        cbCompanyFilter.setBounds(455, 38, 120, 32);
        filterPanel.add(cbCompanyFilter);

        JButton btnFilter = createDarkButton("Filtrar");
        btnFilter.setBounds(605, 38, 100, 32);
        btnFilter.addActionListener(e -> applyFilters());
        filterPanel.add(btnFilter);

        JButton btnRefresh = createSecondaryButton("Atualizar");
        btnRefresh.setBounds(715, 38, 110, 32);
        btnRefresh.addActionListener(e -> resetFilters());
        filterPanel.add(btnRefresh);

        JButton btnNew = createPrimaryButton("Novo faturamento");
        btnNew.setBounds(840, 38, 170, 32);
        btnNew.addActionListener(e -> openRegisterDialog());
        filterPanel.add(btnNew);
    }

    private void createTable(JPanel panel) {
        tableModel = new DefaultTableModel(
                new Object[]{
                        "ID", "Indústria", "Vínculo", "Valor", "Descrição",
                        "Previsto", "Faturado em", "Pago em", "Status"
                }, 0
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
        scrollPane.setBounds(30, 235, 1040, 330);
        panel.add(scrollPane);

        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        table.getColumnModel().getColumn(1).setPreferredWidth(240);
        table.getColumnModel().getColumn(2).setPreferredWidth(70);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(210);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(110);
        table.getColumnModel().getColumn(7).setPreferredWidth(100);
        table.getColumnModel().getColumn(8).setPreferredWidth(100);
    }

    private void createActionButtons(JPanel panel) {
        JButton btnIssue = createPrimaryButton("Marcar como faturado");
        btnIssue.setBounds(485, 590, 190, 38);
        btnIssue.addActionListener(e -> markAsIssued());
        panel.add(btnIssue);

        JButton btnPaid = createPrimaryButton("Marcar como pago");
        btnPaid.setBounds(690, 590, 170, 38);
        btnPaid.addActionListener(e -> markAsPaid());
        panel.add(btnPaid);

        JButton btnClose = createDarkButton("Fechar");
        btnClose.setBounds(880, 590, 190, 38);
        btnClose.addActionListener(e -> dispose());
        panel.add(btnClose);
    }

    private void openRegisterDialog() {
        List<Client> clients = clientController.listActiveClients();

        if (clients.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhuma indústria ativa cadastrada.");
            return;
        }

        JComboBox<ClientComboItem> cbClient = new JComboBox<>();

        for (Client client : clients) {
            cbClient.addItem(new ClientComboItem(client.getId(), client.getName(), client.getCompanyLink()));
        }

        JTextField txtAmount = new JTextField();
        JTextField txtDescription = new JTextField();
        JTextField txtDueDate = new JTextField(LocalDate.now().format(BR_FORMAT));

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Indústria:"));
        panel.add(cbClient);
        panel.add(new JLabel("Valor:"));
        panel.add(txtAmount);
        panel.add(new JLabel("Descrição:"));
        panel.add(txtDescription);
        panel.add(new JLabel("Data prevista (dd/MM/aaaa):"));
        panel.add(txtDueDate);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Novo Faturamento",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                ClientComboItem selectedClient = (ClientComboItem) cbClient.getSelectedItem();

                if (selectedClient == null) {
                    throw new RuntimeException("Selecione uma indústria.");
                }

                BigDecimal amount = new BigDecimal(txtAmount.getText().replace(",", "."));
                LocalDate dueDate = parseBrazilianDate(txtDueDate.getText());

                invoiceController.createPendingInvoice(
                        selectedClient.getId(),
                        amount,
                        txtDescription.getText(),
                        dueDate
                );

                JOptionPane.showMessageDialog(this, "Faturamento pendente criado com sucesso.");
                loadInvoices();

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Valor inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadInvoices() {
        try {
            LocalDate start = parseBrazilianDate(txtStartDate.getText());
            LocalDate end = parseBrazilianDate(txtEndDate.getText());

            fillTable(invoiceController.listByPeriod(start, end));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar faturamentos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilters() {
        try {
            LocalDate start = parseBrazilianDate(txtStartDate.getText());
            LocalDate end = parseBrazilianDate(txtEndDate.getText());

            String status = cbStatusFilter.getSelectedItem().toString();
            String company = cbCompanyFilter.getSelectedItem().toString();

            fillTable(invoiceController.listByFilters(start, end, status, company));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao filtrar faturamentos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFilters() {
        txtStartDate.setText(LocalDate.now().withDayOfMonth(1).format(BR_FORMAT));
        txtEndDate.setText(LocalDate.now().format(BR_FORMAT));
        cbStatusFilter.setSelectedIndex(0);
        cbCompanyFilter.setSelectedIndex(0);
        loadInvoices();
    }

    private void markAsIssued() {
        int id = getSelectedInvoiceId();

        if (id == -1) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja marcar este faturamento como FATURADO?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                invoiceController.markAsIssued(id);
                JOptionPane.showMessageDialog(this, "Faturamento marcado como faturado.");
                loadInvoices();
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void markAsPaid() {
        int id = getSelectedInvoiceId();

        if (id == -1) {
            return;
        }

        String paymentDateText = JOptionPane.showInputDialog(
                this,
                "Informe a data de pagamento (dd/MM/aaaa):",
                LocalDate.now().format(BR_FORMAT)
        );

        if (paymentDateText == null || paymentDateText.trim().isEmpty()) {
            return;
        }

        try {
            LocalDate paymentDate = parseBrazilianDate(paymentDateText.trim());

            invoiceController.markAsPaid(id, paymentDate);

            JOptionPane.showMessageDialog(this, "Faturamento marcado como pago.");
            loadInvoices();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getSelectedInvoiceId() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um faturamento na tabela.");
            return -1;
        }

        return (int) tableModel.getValueAt(row, 0);
    }

    private void fillTable(List<InvoiceView> invoices) {
        tableModel.setRowCount(0);

        for (InvoiceView invoice : invoices) {
            tableModel.addRow(new Object[]{
                    invoice.getId(),
                    invoice.getClientName(),
                    invoice.getCompanyLink(),
                    "R$ " + invoice.getAmount(),
                    invoice.getDescription(),
                    formatDate(invoice.getDueDate()),
                    formatDate(invoice.getIssueDate()),
                    formatDate(invoice.getPaymentDate()),
                    invoice.getStatus()
            });
        }
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return "-";
        }

        return date.format(BR_FORMAT);
    }

    private LocalDate parseBrazilianDate(String date) {
        try {
            return LocalDate.parse(date.trim(), BR_FORMAT);
        } catch (Exception e) {
            throw new RuntimeException("Data inválida. Use o formato dd/MM/aaaa.");
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

    private static class ClientComboItem {
        private final int id;
        private final String name;
        private final String companyLink;

        public ClientComboItem(int id, String name, String companyLink) {
            this.id = id;
            this.name = name;
            this.companyLink = companyLink;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return name + " - " + companyLink;
        }
    }
}