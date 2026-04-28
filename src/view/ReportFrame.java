package view;

import controller.FinanceController;
import controller.ReportController;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.ZoneId;

public class ReportFrame extends JFrame {

    private final ReportController reportController;
    private final FinanceController financeController;

    private JSpinner startSpinner;
    private JSpinner endSpinner;
    private JTextArea resultArea;
    private JLabel statusLabel;

    private final Color ORANGE = new Color(255, 102, 0);
    private final Color BLACK = new Color(18, 18, 18);
    private final Color WHITE = Color.WHITE;
    private final Color LIGHT_GRAY = new Color(245, 245, 245);
    private final Color BORDER_GRAY = new Color(220, 220, 220);
    private final Color TEXT_GRAY = new Color(90, 90, 90);

    public ReportFrame() {
        reportController = new ReportController();
        financeController = new FinanceController();

        setTitle("Sistema At Promo - Relatórios Financeiros");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(null);
        header.setPreferredSize(new Dimension(1000, 95));
        header.setBackground(BLACK);

        JLabel title = new JLabel("Relatórios Financeiros");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBounds(30, 20, 400, 34);
        header.add(title);

        JLabel subtitle = new JLabel("Analise gastos, despesas, lançamentos financeiros e resultado geral da empresa.");
        subtitle.setForeground(new Color(210, 210, 210));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setBounds(32, 56, 700, 22);
        header.add(subtitle);

        JPanel line = new JPanel();
        line.setBackground(ORANGE);
        line.setBounds(30, 82, 180, 4);
        header.add(line);

        return header;
    }

    private JPanel createMainPanel() {
        JPanel main = new JPanel(null);
        main.setBackground(LIGHT_GRAY);

        JPanel filterPanel = new JPanel(null);
        filterPanel.setBackground(WHITE);
        filterPanel.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));
        filterPanel.setBounds(30, 25, 925, 125);
        main.add(filterPanel);

        JLabel filterTitle = new JLabel("Filtros do relatório");
        filterTitle.setForeground(BLACK);
        filterTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        filterTitle.setBounds(20, 12, 250, 25);
        filterPanel.add(filterTitle);

        JLabel startLabel = new JLabel("Data início");
        startLabel.setForeground(TEXT_GRAY);
        startLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        startLabel.setBounds(20, 50, 100, 22);
        filterPanel.add(startLabel);

        startSpinner = new JSpinner(new SpinnerDateModel());
        startSpinner.setEditor(new JSpinner.DateEditor(startSpinner, "dd/MM/yyyy"));
        startSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        startSpinner.setBounds(20, 75, 135, 32);
        filterPanel.add(startSpinner);

        JLabel endLabel = new JLabel("Data fim");
        endLabel.setForeground(TEXT_GRAY);
        endLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        endLabel.setBounds(180, 50, 100, 22);
        filterPanel.add(endLabel);

        endSpinner = new JSpinner(new SpinnerDateModel());
        endSpinner.setEditor(new JSpinner.DateEditor(endSpinner, "dd/MM/yyyy"));
        endSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        endSpinner.setBounds(180, 75, 135, 32);
        filterPanel.add(endSpinner);

        JButton btnGeneral = createPrimaryButton("Relatório Geral");
        btnGeneral.setBounds(360, 67, 150, 40);
        filterPanel.add(btnGeneral);

        JButton btnFinance = createDarkButton("Financeiro Completo");
        btnFinance.setBounds(520, 67, 175, 40);
        filterPanel.add(btnFinance);

        JButton btnByType = createSecondaryButton("Por Tipo");
        btnByType.setBounds(705, 67, 105, 40);
        filterPanel.add(btnByType);

        JButton btnClear = createSecondaryButton("Limpar");
        btnClear.setBounds(820, 67, 85, 40);
        filterPanel.add(btnClear);

        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBackground(WHITE);
        resultPanel.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));
        resultPanel.setBounds(30, 170, 925, 420);
        main.add(resultPanel);

        JPanel resultHeader = new JPanel(new BorderLayout());
        resultHeader.setBackground(WHITE);
        resultHeader.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel resultTitle = new JLabel("Resultado do relatório");
        resultTitle.setForeground(BLACK);
        resultTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        resultHeader.add(resultTitle, BorderLayout.WEST);

        statusLabel = new JLabel("Aguardando geração");
        statusLabel.setForeground(TEXT_GRAY);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultHeader.add(statusLabel, BorderLayout.EAST);

        resultPanel.add(resultHeader, BorderLayout.NORTH);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBackground(WHITE);
        resultArea.setForeground(BLACK);
        resultArea.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        resultArea.setText("Selecione o período e escolha um tipo de relatório.");

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        btnGeneral.addActionListener(e -> generateGeneralReport());
        btnFinance.addActionListener(e -> generateFinanceReport());
        btnByType.addActionListener(e -> generateReportByType());
        btnClear.addActionListener(e -> clearResult());

        return main;
    }

    private void generateGeneralReport() {
        LocalDate start = getStartDate();
        LocalDate end = getEndDate();

        if (!validatePeriod(start, end)) return;

        String output = captureOutput(() -> reportController.showGeneralReport(start, end));
        showOutput("Relatório geral gerado", output);
    }

    private void generateFinanceReport() {
        LocalDate start = getStartDate();
        LocalDate end = getEndDate();

        if (!validatePeriod(start, end)) return;

        String output = captureOutput(() -> financeController.showReportByPeriod(start, end));
        showOutput("Financeiro completo gerado", output);
    }

    private void generateReportByType() {
        LocalDate start = getStartDate();
        LocalDate end = getEndDate();

        if (!validatePeriod(start, end)) return;

        String output = captureOutput(() -> financeController.showReportByTypeAndPeriod(start, end));
        showOutput("Relatório por tipo gerado", output);
    }

    private void showOutput(String status, String output) {
        resultArea.setText(output);
        resultArea.setCaretPosition(0);
        statusLabel.setText(status);
    }

    private void clearResult() {
        resultArea.setText("Selecione o período e escolha um tipo de relatório.");
        statusLabel.setText("Aguardando geração");
    }

    private LocalDate getStartDate() {
        java.util.Date date = (java.util.Date) startSpinner.getValue();

        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private LocalDate getEndDate() {
        java.util.Date date = (java.util.Date) endSpinner.getValue();

        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private boolean validatePeriod(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Data inicial não pode ser maior que a final.",
                    "Período inválido",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        return true;
    }

    private String captureOutput(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream newOut = new PrintStream(outputStream);

        try {
            System.setOut(newOut);
            action.run();
        } finally {
            System.setOut(originalOut);
            newOut.close();
        }

        String output = outputStream.toString();

        if (output.isBlank()) {
            return "Nenhum dado encontrado para esse período.";
        }

        return output;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = baseButton(text);
        button.setBackground(ORANGE);
        button.setForeground(WHITE);
        button.setBorderPainted(false);
        return button;
    }

    private JButton createDarkButton(String text) {
        JButton button = baseButton(text);
        button.setBackground(BLACK);
        button.setForeground(WHITE);
        button.setBorderPainted(false);
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = baseButton(text);
        button.setBackground(WHITE);
        button.setForeground(BLACK);
        button.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));
        return button;
    }

    private JButton baseButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}