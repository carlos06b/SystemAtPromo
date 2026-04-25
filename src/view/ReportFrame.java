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

    private ReportController reportController;
    private FinanceController financeController;

    private JSpinner startSpinner;
    private JSpinner endSpinner;
    private JTextArea resultArea;

    public ReportFrame() {

        reportController = new ReportController();
        financeController = new FinanceController();

        setTitle("Relatórios Financeiros");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel title = new JLabel("Relatórios Financeiros");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBounds(280, 20, 300, 30);
        add(title);

        JLabel startLabel = new JLabel("Data início:");
        startLabel.setBounds(50, 75, 100, 25);
        add(startLabel);

        startSpinner = new JSpinner(new SpinnerDateModel());
        startSpinner.setEditor(new JSpinner.DateEditor(startSpinner, "dd/MM/yyyy"));
        startSpinner.setBounds(140, 75, 130, 25);
        add(startSpinner);

        JLabel endLabel = new JLabel("Data fim:");
        endLabel.setBounds(300, 75, 100, 25);
        add(endLabel);

        endSpinner = new JSpinner(new SpinnerDateModel());
        endSpinner.setEditor(new JSpinner.DateEditor(endSpinner, "dd/MM/yyyy"));
        endSpinner.setBounds(370, 75, 130, 25);
        add(endSpinner);

        JButton btnGeneral = new JButton("Relatório Geral");
        btnGeneral.setBounds(50, 120, 180, 30);
        add(btnGeneral);

        JButton btnFinance = new JButton("Financeiro Completo");
        btnFinance.setBounds(250, 120, 190, 30);
        add(btnFinance);

        JButton btnByType = new JButton("Por Tipo");
        btnByType.setBounds(460, 120, 140, 30);
        add(btnByType);

        JButton btnClear = new JButton("Limpar");
        btnClear.setBounds(620, 120, 100, 30);
        add(btnClear);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBounds(50, 180, 680, 330);
        add(scrollPane);

        btnGeneral.addActionListener(e -> generateGeneralReport());
        btnFinance.addActionListener(e -> generateFinanceReport());
        btnByType.addActionListener(e -> generateReportByType());
        btnClear.addActionListener(e -> resultArea.setText(""));

        setVisible(true);
    }

    private void generateGeneralReport() {
        LocalDate start = getStartDate();
        LocalDate end = getEndDate();

        if (!validatePeriod(start, end)) return;

        String output = captureOutput(() ->
                reportController.showGeneralReport(start, end)
        );

        resultArea.setText(output);
    }

    private void generateFinanceReport() {
        LocalDate start = getStartDate();
        LocalDate end = getEndDate();

        if (!validatePeriod(start, end)) return;

        String output = captureOutput(() ->
                financeController.showReportByPeriod(start, end)
        );

        resultArea.setText(output);
    }

    private void generateReportByType() {
        LocalDate start = getStartDate();
        LocalDate end = getEndDate();

        if (!validatePeriod(start, end)) return;

        String output = captureOutput(() ->
                financeController.showReportByTypeAndPeriod(start, end)
        );

        resultArea.setText(output);
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
            JOptionPane.showMessageDialog(this, "Data inicial não pode ser maior que a final.");
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
        }

        String output = outputStream.toString();

        if (output.isBlank()) {
            return "Nenhum dado encontrado para esse período.";
        }

        return output;
    }
}