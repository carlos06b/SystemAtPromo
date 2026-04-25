package view;

import controller.PromoterController;
import model.Promoter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PromoterFrame extends JFrame {

    private PromoterController promoterController;
    private JTable table;
    private DefaultTableModel tableModel;

    public PromoterFrame() {

        promoterController = new PromoterController();

        setTitle("Promotores");
        setSize(1050, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel title = new JLabel("Gerenciamento de Promotores");
        title.setBounds(380, 20, 300, 30);
        add(title);

        JButton btnList = new JButton("Listar");
        btnList.setBounds(30, 70, 100, 30);
        add(btnList);

        JButton btnFilter = new JButton("Filtrar");
        btnFilter.setBounds(140, 70, 100, 30);
        add(btnFilter);

        JButton btnSearch = new JButton("Buscar");
        btnSearch.setBounds(250, 70, 100, 30);
        add(btnSearch);

        JButton btnInactive = new JButton("Inativar");
        btnInactive.setBounds(360, 70, 110, 30);
        add(btnInactive);

        JButton btnActivate = new JButton("Ativar");
        btnActivate.setBounds(480, 70, 100, 30);
        add(btnActivate);

        JButton btnRegister = new JButton("Cadastrar");
        btnRegister.setBounds(590, 70, 120, 30);
        add(btnRegister);

        String[] columns = {"ID", "Nome", "CPF", "Telefone", "Tipo", "Salário", "Status", "Editar"};

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) {
                return c == 7;
            }
        };

        table = new JTable(tableModel);

        table.getColumn("Editar").setCellRenderer(new ButtonRenderer());
        table.getColumn("Editar").setCellEditor(new ButtonEditor(new JCheckBox(), this));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(30, 120, 970, 350);
        add(scroll);

        // AÇÕES
        btnList.addActionListener(e -> loadPromoters());
        btnFilter.addActionListener(e -> openFilterDialog());
        btnSearch.addActionListener(e -> searchByName());
        btnInactive.addActionListener(e -> actionInactivate());
        btnActivate.addActionListener(e -> actionActivate());
        btnRegister.addActionListener(e -> openRegisterDialog());

        setVisible(true);
    }

    private void loadPromoters() {
        fillTable(promoterController.getAll());
    }

    private void openFilterDialog() {

        JComboBox<String> typeBox = new JComboBox<>(new String[]{"TODOS", "CLT", "MEI"});
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"TODOS", "ATIVO", "INATIVO"});

        Object[] fields = {
                "Tipo:", typeBox,
                "Status:", statusBox
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Filtrar Promotores",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option != JOptionPane.OK_OPTION) return;

        String type = typeBox.getSelectedItem().toString();
        String status = statusBox.getSelectedItem().toString();

        List<Promoter> list = promoterController.getAll();

        List<Promoter> filtered = list.stream()
                .filter(p -> {
                    boolean matchType =
                            type.equals("TODOS") || p.getType().equalsIgnoreCase(type);

                    boolean matchStatus =
                            status.equals("TODOS") ||
                                    (status.equals("ATIVO") && p.isActive()) ||
                                    (status.equals("INATIVO") && !p.isActive());

                    return matchType && matchStatus;
                })
                .toList();

        fillTable(filtered);
    }

    private void searchByName() {

        JTextField searchField = new JTextField();

        DefaultListModel<PromoterItem> listModel = new DefaultListModel<>();
        JList<PromoterItem> promoterList = new JList<>(listModel);

        JScrollPane listScroll = new JScrollPane(promoterList);
        listScroll.setPreferredSize(new Dimension(300, 120));

        searchField.addCaretListener(e -> {
            String text = searchField.getText().trim();

            listModel.clear();

            if (text.length() < 2) return;

            List<Promoter> promoters = promoterController.searchByName(text);

            for (Promoter p : promoters) {
                listModel.addElement(new PromoterItem(p.getId(), p.getName()));
            }
        });

        Object[] fields = {
                "Buscar promotor pelo nome:", searchField,
                "Resultados:", listScroll
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Buscar Promotor",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {

            PromoterItem selected = promoterList.getSelectedValue();

            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Selecione um promotor.");
                return;
            }

            Promoter promoter = promoterController.findById(selected.getId());

            tableModel.setRowCount(0);

            if (promoter != null) addRow(promoter);
        }
    }

    private void actionInactivate() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um promotor.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        promoterController.inactivate(id);
        loadPromoters();
    }

    private void actionActivate() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um promotor.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        promoterController.activate(id);
        loadPromoters();
    }

    public void openEditDialog(int id) {

        Promoter p = promoterController.findById(id);

        JTextField name = new JTextField(p.getName());
        JTextField phone = new JTextField(p.getPhone());
        JTextField salary = new JTextField(p.getSalary().toString());

        JComboBox<String> type = new JComboBox<>(new String[]{"CLT", "MEI"});
        type.setSelectedItem(p.getType());

        Object[] fields = {
                "Nome:", name,
                "Telefone:", phone,
                "Salário:", salary,
                "Tipo:", type
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Editar Promotor", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            promoterController.update(
                    id,
                    name.getText(),
                    phone.getText(),
                    new BigDecimal(salary.getText().replace(",", ".")),
                    type.getSelectedItem().toString()
            );

            JOptionPane.showMessageDialog(this, "Atualizado!");
            loadPromoters();
        }
    }

    private void openRegisterDialog() {

        JTextField name = new JTextField();
        JTextField cpf = new JTextField();
        JTextField phone = new JTextField();
        JTextField salary = new JTextField();

        JSpinner birthSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor birthEditor = new JSpinner.DateEditor(birthSpinner, "dd/MM/yyyy");
        birthSpinner.setEditor(birthEditor);

        JComboBox<String> type = new JComboBox<>(new String[]{"CLT", "MEI"});

        Object[] fields = {
                "Nome:", name,
                "CPF:", cpf,
                "Telefone:", phone,
                "Nascimento:", birthSpinner,
                "Salário:", salary,
                "Tipo:", type
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Cadastrar Promotor",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            try {
                java.util.Date birthDate = (java.util.Date) birthSpinner.getValue();

                LocalDate dateBirth = birthDate.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate();

                promoterController.register(
                        name.getText(),
                        cpf.getText(),
                        phone.getText(),
                        dateBirth,
                        new BigDecimal(salary.getText().replace(",", ".")),
                        type.getSelectedItem().toString()
                );

                JOptionPane.showMessageDialog(this, "Cadastrado!");
                loadPromoters();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Dados inválidos.");
            }
        }
    }

    private void fillTable(List<Promoter> list) {
        tableModel.setRowCount(0);
        for (Promoter p : list) addRow(p);
    }

    private void addRow(Promoter p) {
        tableModel.addRow(new Object[]{
                p.getId(),
                p.getName(),
                formatCpf(p.getCpf()),
                p.getPhone(),
                p.getType(),
                p.getSalary(),
                p.isActive() ? "ATIVO" : "INATIVO",
                "✏️"
        });
    }

    private String formatCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.substring(0,3)+"."+cpf.substring(3,6)+"."+cpf.substring(6,9)+"-"+cpf.substring(9);
    }

    private static class PromoterItem {
        private int id;
        private String name;

        public PromoterItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }

        public String toString() { return name; }
    }

    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() { setText("✏️"); }
        public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){ return this; }
    }

    class ButtonEditor extends DefaultCellEditor {

        private JButton button;
        private int row;
        private PromoterFrame frame;

        public ButtonEditor(JCheckBox checkBox, PromoterFrame frame) {
            super(checkBox);
            this.frame = frame;
            button = new JButton("✏️");

            button.addActionListener(e -> {
                int id = (int) table.getValueAt(row, 0);
                frame.openEditDialog(id);
            });
        }

        public Component getTableCellEditorComponent(JTable t,Object v,boolean s,int r,int c){
            row = r;
            return button;
        }

        public Object getCellEditorValue() {
            return "✏️";
        }
    }
}