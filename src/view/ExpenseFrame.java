package view;

import dao.FixedExpenseDAO;
import dao.FixedExpenseHistoryDAO;
import dao.VariableExpenseDAO;
import model.FixedExpense;
import model.FixedExpenseHistory;
import model.VariableExpense;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExpenseFrame extends JFrame {

    private final FixedExpenseDAO fixedExpenseDAO = new FixedExpenseDAO();
    private final FixedExpenseHistoryDAO fixedExpenseHistoryDAO = new FixedExpenseHistoryDAO();
    private final VariableExpenseDAO variableExpenseDAO = new VariableExpenseDAO();

    private final DateTimeFormatter brFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JTable fixedExpenseTable;
    private JTable fixedExpenseHistoryTable;
    private JTable variableExpenseTable;

    private JTextField txtFixedName;
    private JTextField txtFixedAmount;
    private JTextField txtFixedDueDate;

    private JTextField txtHistoryStartDate;
    private JTextField txtHistoryEndDate;

    private JTextField txtVariableName;
    private JTextField txtVariableAmount;
    private JTextField txtVariableDate;
    private JTextField txtVariableDescription;
    private JTextField txtVariableStartDate;
    private JTextField txtVariableEndDate;

    public ExpenseFrame() {
        setTitle("Controle de Despesas");
        setSize(1050, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Despesas Fixas", createFixedExpensePanel());
        tabbedPane.addTab("Fixas do Mês", createFixedExpenseHistoryPanel());
        tabbedPane.addTab("Despesas Variáveis", createVariableExpensePanel());

        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createFixedExpensePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Cadastrar despesa fixa"));

        txtFixedName = new JTextField();
        txtFixedAmount = new JTextField();
        txtFixedDueDate = new JTextField();

        JButton btnSave = new JButton("Cadastrar");
        JButton btnList = new JButton("Atualizar Lista");
        JButton btnDelete = new JButton("Excluir Selecionada");
        JButton btnMarkPaid = new JButton("Marcar como Paga");

        formPanel.add(new JLabel("Nome:"));
        formPanel.add(new JLabel("Valor:"));
        formPanel.add(new JLabel("Vencimento (dd/MM/yyyy):"));
        formPanel.add(new JLabel("Ações:"));

        formPanel.add(txtFixedName);
        formPanel.add(txtFixedAmount);
        formPanel.add(txtFixedDueDate);
        formPanel.add(btnSave);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnList);
        buttonPanel.add(btnMarkPaid);
        buttonPanel.add(btnDelete);

        fixedExpenseTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(fixedExpenseTable);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> saveFixedExpense());
        btnList.addActionListener(e -> loadFixedExpenses());
        btnDelete.addActionListener(e -> deleteFixedExpense());
        btnMarkPaid.addActionListener(e -> markFixedExpenseAsPaid());

        loadFixedExpenses();

        return panel;
    }

    private JPanel createFixedExpenseHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Despesas fixas lançadas no mês"));

        txtHistoryStartDate = new JTextField();
        txtHistoryEndDate = new JTextField();

        JButton btnGenerateMonth = new JButton("Gerar Fixas do Mês Atual");
        JButton btnSearch = new JButton("Buscar por Período");
        JButton btnMarkPaid = new JButton("Marcar como Paga");

        topPanel.add(new JLabel("Data inicial (dd/MM/yyyy):"));
        topPanel.add(new JLabel("Data final (dd/MM/yyyy):"));
        topPanel.add(new JLabel("Gerar despesas mensais:"));
        topPanel.add(new JLabel("Ações:"));

        topPanel.add(txtHistoryStartDate);
        topPanel.add(txtHistoryEndDate);
        topPanel.add(btnGenerateMonth);
        topPanel.add(btnSearch);

        fixedExpenseHistoryTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(fixedExpenseHistoryTable);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnMarkPaid);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        btnGenerateMonth.addActionListener(e -> generateMonthlyFixedExpenses());
        btnSearch.addActionListener(e -> loadFixedExpenseHistoryByPeriod());
        btnMarkPaid.addActionListener(e -> markFixedExpenseHistoryAsPaid());

        fillCurrentMonthFields();
        loadFixedExpenseHistoryByPeriod();

        return panel;
    }

    private JPanel createVariableExpensePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 5, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Cadastrar despesa variável"));

        txtVariableName = new JTextField();
        txtVariableAmount = new JTextField();
        txtVariableDate = new JTextField();
        txtVariableDescription = new JTextField();

        JButton btnSave = new JButton("Cadastrar");

        formPanel.add(new JLabel("Nome:"));
        formPanel.add(new JLabel("Valor:"));
        formPanel.add(new JLabel("Data (dd/MM/yyyy):"));
        formPanel.add(new JLabel("Descrição:"));
        formPanel.add(new JLabel("Ações:"));

        formPanel.add(txtVariableName);
        formPanel.add(txtVariableAmount);
        formPanel.add(txtVariableDate);
        formPanel.add(txtVariableDescription);
        formPanel.add(btnSave);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Listar despesas variáveis por período"));

        txtVariableStartDate = new JTextField(10);
        txtVariableEndDate = new JTextField(10);

        JButton btnSearch = new JButton("Buscar");
        JButton btnMarkPaid = new JButton("Marcar como Paga");
        JButton btnDelete = new JButton("Excluir Selecionada");

        filterPanel.add(new JLabel("Inicial:"));
        filterPanel.add(txtVariableStartDate);
        filterPanel.add(new JLabel("Final:"));
        filterPanel.add(txtVariableEndDate);
        filterPanel.add(btnSearch);
        filterPanel.add(btnMarkPaid);
        filterPanel.add(btnDelete);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(formPanel, BorderLayout.NORTH);
        northPanel.add(filterPanel, BorderLayout.SOUTH);

        variableExpenseTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(variableExpenseTable);

        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        btnSave.addActionListener(e -> saveVariableExpense());
        btnSearch.addActionListener(e -> loadVariableExpensesByPeriod());
        btnMarkPaid.addActionListener(e -> markVariableExpenseAsPaid());
        btnDelete.addActionListener(e -> deleteVariableExpense());

        fillVariableCurrentMonthFields();
        loadVariableExpensesByPeriod();

        return panel;
    }

    private void saveFixedExpense() {
        try {
            String name = txtFixedName.getText().trim();
            BigDecimal amount = parseMoney(txtFixedAmount.getText());
            LocalDate dueDate = parseDate(txtFixedDueDate.getText());

            if (name.isEmpty()) {
                showError("Informe o nome da despesa fixa.");
                return;
            }

            FixedExpense expense = new FixedExpense();
            expense.setName(name);
            expense.setAmount(amount);
            expense.setDueDate(dueDate);
            expense.setStatus(false);
            expense.setPaymentDate(null);

            fixedExpenseDAO.save(expense);

            clearFixedFields();
            loadFixedExpenses();

            showSuccess("Despesa fixa cadastrada com sucesso!");

        } catch (Exception ex) {
            showError("Erro ao cadastrar despesa fixa: " + ex.getMessage());
        }
    }

    private void loadFixedExpenses() {
        List<FixedExpense> expenses = fixedExpenseDAO.findAll();

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Nome");
        model.addColumn("Valor");
        model.addColumn("Vencimento");
        model.addColumn("Status");
        model.addColumn("Pagamento");

        for (FixedExpense e : expenses) {
            model.addRow(new Object[]{
                    e.getId(),
                    e.getName(),
                    formatMoney(e.getAmount()),
                    formatDate(e.getDueDate()),
                    e.isStatus() ? "PAGO" : "PENDENTE",
                    formatDate(e.getPaymentDate())
            });
        }

        fixedExpenseTable.setModel(model);
        hideIdColumn(fixedExpenseTable);
    }

    private void deleteFixedExpense() {
        int id = getSelectedId(fixedExpenseTable);

        if (id == -1) {
            showError("Selecione uma despesa fixa para excluir.");
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja excluir essa despesa fixa?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            fixedExpenseDAO.delete(id);
            loadFixedExpenses();
            showSuccess("Despesa fixa excluída com sucesso!");
        }
    }

    private void markFixedExpenseAsPaid() {
        int id = getSelectedId(fixedExpenseTable);

        if (id == -1) {
            showError("Selecione uma despesa fixa.");
            return;
        }

        fixedExpenseDAO.markAsPaid(id, LocalDate.now());
        loadFixedExpenses();
        showSuccess("Despesa fixa marcada como paga!");
    }

    private void generateMonthlyFixedExpenses() {
        LocalDate today = LocalDate.now();

        int option = JOptionPane.showConfirmDialog(
                this,
                "Gerar despesas fixas para " + today.getMonthValue() + "/" + today.getYear() + "?",
                "Gerar despesas do mês",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            fixedExpenseHistoryDAO.generateMonthlyExpenses(today.getMonthValue(), today.getYear());
            fillCurrentMonthFields();
            loadFixedExpenseHistoryByPeriod();
            showSuccess("Despesas fixas do mês geradas com sucesso!");
        }
    }

    private void loadFixedExpenseHistoryByPeriod() {
        try {
            LocalDate start = parseDate(txtHistoryStartDate.getText());
            LocalDate end = parseDate(txtHistoryEndDate.getText());

            if (start.isAfter(end)) {
                showError("A data inicial não pode ser maior que a data final.");
                return;
            }

            List<FixedExpenseHistory> expenses = fixedExpenseHistoryDAO.findByPeriod(start, end);

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("ID Fixa");
            model.addColumn("Nome");
            model.addColumn("Valor");
            model.addColumn("Vencimento");
            model.addColumn("Status");
            model.addColumn("Pagamento");

            for (FixedExpenseHistory e : expenses) {
                model.addRow(new Object[]{
                        e.getId(),
                        e.getFixedExpenseId(),
                        e.getName(),
                        formatMoney(e.getAmount()),
                        formatDate(e.getDueDate()),
                        e.isStatus() ? "PAGO" : "PENDENTE",
                        formatDate(e.getPaymentDate())
                });
            }

            fixedExpenseHistoryTable.setModel(model);
            hideIdColumn(fixedExpenseHistoryTable);
            hideFirstVisibleColumn(fixedExpenseHistoryTable);

        } catch (Exception ex) {
            showError("Erro ao buscar despesas fixas do mês: " + ex.getMessage());
        }
    }

    private void markFixedExpenseHistoryAsPaid() {
        int id = getSelectedId(fixedExpenseHistoryTable);

        if (id == -1) {
            showError("Selecione uma despesa fixa mensal.");
            return;
        }

        fixedExpenseHistoryDAO.markAsPaid(id, LocalDate.now());
        loadFixedExpenseHistoryByPeriod();
        showSuccess("Despesa fixa mensal marcada como paga!");
    }

    private void saveVariableExpense() {
        try {
            String name = txtVariableName.getText().trim();
            BigDecimal amount = parseMoney(txtVariableAmount.getText());
            LocalDate date = parseDate(txtVariableDate.getText());
            String description = txtVariableDescription.getText().trim();

            if (name.isEmpty()) {
                showError("Informe o nome da despesa variável.");
                return;
            }

            if (description.isEmpty()) {
                description = "Sem descrição";
            }

            VariableExpense expense = new VariableExpense();
            expense.setName(name);
            expense.setAmount(amount);
            expense.setDate(date);
            expense.setStatus(false);
            expense.setPaymentDate(null);
            expense.setDescription(description);

            variableExpenseDAO.save(expense);

            clearVariableFields();
            loadVariableExpensesByPeriod();

            showSuccess("Despesa variável cadastrada com sucesso!");

        } catch (Exception ex) {
            showError("Erro ao cadastrar despesa variável: " + ex.getMessage());
        }
    }

    private void loadVariableExpensesByPeriod() {
        try {
            LocalDate start = parseDate(txtVariableStartDate.getText());
            LocalDate end = parseDate(txtVariableEndDate.getText());

            if (start.isAfter(end)) {
                showError("A data inicial não pode ser maior que a data final.");
                return;
            }

            List<VariableExpense> expenses = variableExpenseDAO.findByPeriod(start, end);

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("Nome");
            model.addColumn("Valor");
            model.addColumn("Data");
            model.addColumn("Status");
            model.addColumn("Pagamento");
            model.addColumn("Descrição");

            for (VariableExpense e : expenses) {
                model.addRow(new Object[]{
                        e.getId(),
                        e.getName(),
                        formatMoney(e.getAmount()),
                        formatDate(e.getDate()),
                        e.isStatus() ? "PAGO" : "PENDENTE",
                        formatDate(e.getPaymentDate()),
                        e.getDescription()
                });
            }

            variableExpenseTable.setModel(model);
            hideIdColumn(variableExpenseTable);

        } catch (Exception ex) {
            showError("Erro ao buscar despesas variáveis: " + ex.getMessage());
        }
    }

    private void markVariableExpenseAsPaid() {
        int id = getSelectedId(variableExpenseTable);

        if (id == -1) {
            showError("Selecione uma despesa variável.");
            return;
        }

        variableExpenseDAO.markAsPaid(id, LocalDate.now());
        loadVariableExpensesByPeriod();
        showSuccess("Despesa variável marcada como paga!");
    }

    private void deleteVariableExpense() {
        int id = getSelectedId(variableExpenseTable);

        if (id == -1) {
            showError("Selecione uma despesa variável para excluir.");
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja excluir essa despesa variável?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            variableExpenseDAO.delete(id);
            loadVariableExpensesByPeriod();
            showSuccess("Despesa variável excluída com sucesso!");
        }
    }

    private void hideIdColumn(JTable table) {
        if (table.getColumnModel().getColumnCount() > 0) {
            table.removeColumn(table.getColumnModel().getColumn(0));
        }
    }

    private void hideFirstVisibleColumn(JTable table) {
        if (table.getColumnModel().getColumnCount() > 0) {
            table.removeColumn(table.getColumnModel().getColumn(0));
        }
    }

    private int getSelectedId(JTable table) {
        int viewRow = table.getSelectedRow();

        if (viewRow == -1) {
            return -1;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        return Integer.parseInt(table.getModel().getValueAt(modelRow, 0).toString());
    }

    private BigDecimal parseMoney(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe um valor.");
        }

        String normalized = value.trim().replace(".", "").replace(",", ".");
        return new BigDecimal(normalized);
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe uma data.");
        }

        return LocalDate.parse(value.trim(), brFormatter);
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return "-";
        }

        return date.format(brFormatter);
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) {
            return "R$ 0,00";
        }

        return "R$ " + value.setScale(2, RoundingMode.HALF_UP)
                .toString()
                .replace(".", ",");
    }

    private void fillCurrentMonthFields() {
        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.withDayOfMonth(1);
        LocalDate lastDay = today.withDayOfMonth(today.lengthOfMonth());

        txtHistoryStartDate.setText(formatDate(firstDay));
        txtHistoryEndDate.setText(formatDate(lastDay));
    }

    private void fillVariableCurrentMonthFields() {
        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.withDayOfMonth(1);
        LocalDate lastDay = today.withDayOfMonth(today.lengthOfMonth());

        txtVariableDate.setText(formatDate(today));
        txtVariableStartDate.setText(formatDate(firstDay));
        txtVariableEndDate.setText(formatDate(lastDay));
    }

    private void clearFixedFields() {
        txtFixedName.setText("");
        txtFixedAmount.setText("");
        txtFixedDueDate.setText("");
    }

    private void clearVariableFields() {
        txtVariableName.setText("");
        txtVariableAmount.setText("");
        txtVariableDescription.setText("");
        txtVariableDate.setText(formatDate(LocalDate.now()));
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}