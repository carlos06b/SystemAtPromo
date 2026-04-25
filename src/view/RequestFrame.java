package view;

import controller.PromoterController;
import controller.RequestController;
import model.Promoter;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RequestFrame extends JFrame {

    private RequestController requestController;
    private PromoterController promoterController;
    private User loggedUser;
    private JTable table;
    private DefaultTableModel tableModel;

    public RequestFrame(User loggedUser) {
        this.loggedUser = loggedUser;
        this.requestController = new RequestController();
        this.promoterController = new PromoterController();

        setTitle("Solicitações - " + loggedUser.getJobTittle());
        setSize(1100, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel title = new JLabel("Gerenciamento de Solicitações");
        title.setBounds(420, 20, 300, 30);
        add(title);

        JButton btnListAll = new JButton("Listar Todas");
        btnListAll.setBounds(30, 70, 130, 30);
        add(btnListAll);

        JButton btnPending = new JButton("Pendentes");
        btnPending.setBounds(170, 70, 120, 30);
        add(btnPending);

        JButton btnPeriod = new JButton("Por Período");
        btnPeriod.setBounds(300, 70, 130, 30);
        add(btnPeriod);

        JButton btnCreate = new JButton("Criar");
        btnCreate.setBounds(440, 70, 100, 30);
        add(btnCreate);

        JButton btnApprove = new JButton("Aprovar");
        btnApprove.setBounds(550, 70, 110, 30);
        add(btnApprove);

        JButton btnReject = new JButton("Rejeitar");
        btnReject.setBounds(670, 70, 110, 30);
        add(btnReject);

        JButton btnDetails = new JButton("Ver Detalhes");
        btnDetails.setBounds(790, 70, 130, 30);
        add(btnDetails);

        String[] columns = {
                "ID", "Promotor", "Tipo", "Valor", "Mensagem", "Status", "Data", "MensagemCompleta"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);

        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        table.getColumnModel().getColumn(4).setPreferredWidth(250);

        table.getColumnModel().getColumn(7).setMinWidth(0);
        table.getColumnModel().getColumn(7).setMaxWidth(0);
        table.getColumnModel().getColumn(7).setWidth(0);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showRequestDetails();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 120, 1020, 350);
        add(scrollPane);

        btnListAll.addActionListener(e -> loadAll());
        btnPending.addActionListener(e -> loadPending());
        btnPeriod.addActionListener(e -> loadByPeriod());
        btnCreate.addActionListener(e -> openCreateDialog());
        btnApprove.addActionListener(e -> approveSelected());
        btnReject.addActionListener(e -> rejectSelected());
        btnDetails.addActionListener(e -> showRequestDetails());

        if (loggedUser.getJobTittle().equalsIgnoreCase("RH")) {
            btnApprove.setEnabled(false);
            btnReject.setEnabled(false);
        }

        if (loggedUser.getJobTittle().equalsIgnoreCase("FINANCEIRO")) {
            btnCreate.setEnabled(false);
        }

        loadPending();
        setVisible(true);
    }

    private void loadAll() {
        fillTable(requestController.getAllWithPromoterName());
    }

    private void loadPending() {
        fillTable(requestController.getPendingWithPromoterName());
    }

    private void loadByPeriod() {
        try {
            JSpinner startSpinner = new JSpinner(new SpinnerDateModel());
            JSpinner endSpinner = new JSpinner(new SpinnerDateModel());

            startSpinner.setEditor(new JSpinner.DateEditor(startSpinner, "dd/MM/yyyy"));
            endSpinner.setEditor(new JSpinner.DateEditor(endSpinner, "dd/MM/yyyy"));

            Object[] fields = {
                    "Data início:", startSpinner,
                    "Data fim:", endSpinner
            };

            int option = JOptionPane.showConfirmDialog(
                    this,
                    fields,
                    "Selecionar Período",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (option != JOptionPane.OK_OPTION) return;

            java.util.Date startDate = (java.util.Date) startSpinner.getValue();
            java.util.Date endDate = (java.util.Date) endSpinner.getValue();

            LocalDate start = startDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            LocalDate end = endDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            if (start.isAfter(end)) {
                JOptionPane.showMessageDialog(this, "Data inicial não pode ser maior que a final.");
                return;
            }

            fillTable(
                    requestController.getByPeriodWithPromoterName(
                            start.atStartOfDay(),
                            end.atTime(23, 59, 59)
                    )
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao selecionar período.");
        }
    }

    private void openCreateDialog() {

        JTextField searchField = new JTextField();
        DefaultListModel<PromoterItem> listModel = new DefaultListModel<>();
        JList<PromoterItem> promoterList = new JList<>(listModel);
        JScrollPane listScroll = new JScrollPane(promoterList);

        listScroll.setPreferredSize(new java.awt.Dimension(300, 100));

        searchField.addCaretListener(e -> {
            String text = searchField.getText().trim();

            listModel.clear();

            if (text.length() < 2) return;

            List<Promoter> promoters = promoterController.searchByName(text);

            for (Promoter p : promoters) {
                listModel.addElement(new PromoterItem(p.getId(), p.getName()));
            }
        });

        JComboBox<String> typeBox = new JComboBox<>(new String[]{
                "Bonificação",
                "Ajuda de Custo",
                "Desconto",
                "ASO",
                "EPI"
        });

        JTextField amountField = new JTextField();
        JTextArea messageArea = new JTextArea(5, 25);

        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        Object[] fields = {
                "Buscar promotor pelo nome:", searchField,
                "Resultados:", listScroll,
                "Tipo:", typeBox,
                "Valor:", amountField,
                "Mensagem / PIX / Dados para pagamento:", new JScrollPane(messageArea)
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Criar Solicitação",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            try {
                PromoterItem selectedPromoter = promoterList.getSelectedValue();

                if (selectedPromoter == null) {
                    JOptionPane.showMessageDialog(this, "Selecione um promotor na lista.");
                    return;
                }

                BigDecimal amount = new BigDecimal(amountField.getText().trim().replace(",", "."));
                String selectedType = typeBox.getSelectedItem().toString();
                String type = convertTypeToDatabase(selectedType);
                String message = messageArea.getText().trim();

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this, "O valor precisa ser maior que zero.");
                    return;
                }

                if (message.isBlank()) {
                    JOptionPane.showMessageDialog(this, "Mensagem não pode ficar vazia.");
                    return;
                }

                requestController.createRequest(
                        loggedUser.getId(),
                        0,
                        selectedPromoter.getId(),
                        type,
                        amount,
                        message
                );

                JOptionPane.showMessageDialog(this, "Solicitação criada!");
                loadPending();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Dados inválidos.");
            }
        }
    }

    private void approveSelected() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma solicitação.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja aprovar esta solicitação?",
                "Confirmar aprovação",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            requestController.approve(id, loggedUser.getId());
            JOptionPane.showMessageDialog(this, "Solicitação aprovada!");
            loadPending();
        }
    }

    private void rejectSelected() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma solicitação.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja rejeitar esta solicitação?",
                "Confirmar rejeição",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            requestController.reject(id);
            JOptionPane.showMessageDialog(this, "Solicitação rejeitada!");
            loadPending();
        }
    }

    private void showRequestDetails() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma solicitação.");
            return;
        }

        Object promoter = tableModel.getValueAt(row, 1);
        Object type = tableModel.getValueAt(row, 2);
        Object amount = tableModel.getValueAt(row, 3);
        Object shortMessage = tableModel.getValueAt(row, 4);
        Object status = tableModel.getValueAt(row, 5);
        Object date = tableModel.getValueAt(row, 6);
        Object fullMessage = tableModel.getValueAt(row, 7);

        String messageToShow = fullMessage != null ? fullMessage.toString() : shortMessage.toString();

        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);

        detailsArea.setText(
                "Promotor: " + promoter + "\n" +
                        "Tipo: " + type + "\n" +
                        "Valor: " + amount + "\n" +
                        "Status: " + status + "\n" +
                        "Data: " + date + "\n\n" +
                        "Mensagem / PIX / Dados para pagamento:\n" +
                        messageToShow
        );

        JScrollPane scrollPane = new JScrollPane(detailsArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(550, 320));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Detalhes da Solicitação",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void fillTable(List<String> lines) {
        tableModel.setRowCount(0);

        for (String line : lines) {
            addLineToTable(line);
        }
    }

    private void addLineToTable(String line) {
        try {
            String[] parts = line.split("\\|");

            int id = Integer.parseInt(parts[0].trim());
            String promoter = parts[1].replace("Promotor:", "").trim();
            String type = convertTypeToView(parts[2].trim());
            String amount = parts[3].trim();
            String fullMessage = parts[4].trim();
            String shortMessage = shortenText(fullMessage, 45);
            String status = parts[5].trim();
            String date = formatDateTime(parts[6].trim());

            tableModel.addRow(new Object[]{
                    id,
                    promoter,
                    type,
                    amount,
                    shortMessage,
                    status,
                    date,
                    fullMessage
            });

        } catch (Exception e) {
            tableModel.addRow(new Object[]{
                    "-", "-", "-", "-", shortenText(line, 45), "-", "-", line
            });
        }
    }

    private String convertTypeToDatabase(String type) {
        return switch (type) {
            case "Bonificação" -> "BONIFICACAO";
            case "Ajuda de Custo" -> "AJUDA_CUSTO";
            case "Desconto" -> "DESCONTO";
            case "ASO" -> "ASO";
            case "EPI" -> "EPI";
            default -> type.toUpperCase();
        };
    }

    private String convertTypeToView(String type) {
        return switch (type) {
            case "BONIFICACAO" -> "Bonificação";
            case "AJUDA_CUSTO" -> "Ajuda de Custo";
            case "DESCONTO" -> "Desconto";
            case "ASO" -> "ASO";
            case "EPI" -> "EPI";
            default -> type;
        };
    }

    private String shortenText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }

    private String formatDateTime(String dateTime) {
        try {
            LocalDateTime dt = LocalDateTime.parse(dateTime);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return dt.format(formatter);
        } catch (Exception e) {
            return dateTime;
        }
    }

    private static class PromoterItem {
        private int id;
        private String name;

        public PromoterItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String toString() {
            return name;
        }
    }
}