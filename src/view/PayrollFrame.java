package view;

import controller.PayrollController;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;

public class PayrollFrame extends JFrame {

    private PayrollController payrollController;

    private JSpinner startSpinner;
    private JSpinner endSpinner;
    private JComboBox<String> typeBox;
    private JTextArea resultArea;

    public PayrollFrame() {

        payrollController = new PayrollController();

        setTitle("Folha de Pagamento");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel title = new JLabel("Folha de Pagamento");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBounds(300, 20, 300, 30);
        add(title);

        JLabel startLabel = new JLabel("Data início:");
        startLabel.setBounds(50, 80, 100, 25);
        add(startLabel);

        startSpinner = new JSpinner(new SpinnerDateModel());
        startSpinner.setEditor(new JSpinner.DateEditor(startSpinner, "dd/MM/yyyy"));
        startSpinner.setBounds(140, 80, 130, 25);
        add(startSpinner);

        JLabel endLabel = new JLabel("Data fim:");
        endLabel.setBounds(300, 80, 100, 25);
        add(endLabel);

        endSpinner = new JSpinner(new SpinnerDateModel());
        endSpinner.setEditor(new JSpinner.DateEditor(endSpinner, "dd/MM/yyyy"));
        endSpinner.setBounds(370, 80, 130, 25);
        add(endSpinner);

        JLabel typeLabel = new JLabel("Tipo:");
        typeLabel.setBounds(530, 80, 50, 25);
        add(typeLabel);

        typeBox = new JComboBox<>(new String[]{"TODOS", "CLT", "MEI"});
        typeBox.setBounds(580, 80, 120, 25);
        add(typeBox);

        JButton btnGenerate = new JButton("Gerar Folha");
        btnGenerate.setBounds(50, 130, 150, 30);
        add(btnGenerate);

        JButton btnClear = new JButton("Limpar");
        btnClear.setBounds(220, 130, 120, 30);
        add(btnClear);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBounds(50, 180, 740, 350);
        add(scrollPane);

        btnGenerate.addActionListener(e -> generatePayroll());
        btnClear.addActionListener(e -> resultArea.setText(""));

        setVisible(true);
    }

    private void generatePayroll() {

        LocalDate start = getStartDate();
        LocalDate end = getEndDate();

        if (start.isAfter(end)) {
            JOptionPane.showMessageDialog(this, "Data inicial não pode ser maior que a final.");
            return;
        }

        String type = typeBox.getSelectedItem().toString();

        String result = payrollController.generatePayroll(start, end, type);

        resultArea.setText(result);
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
}