package controller;

import dao.PromoterDAO;
import model.Promoter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PromoterController {

    private PromoterDAO promoterDAO = new PromoterDAO();

    public void register(String name, String cpf, String phone, LocalDate dateBirth,
                         BigDecimal salary, String type) {

        cpf = cpf.replaceAll("[^0-9]", "");

        if (!isValidCpf(cpf)) {
            System.out.println("CPF inválido.");
            return;
        }

        if (promoterDAO.findByCpf(cpf) != null) {
            System.out.println("CPF já cadastrado.");
            return;
        }

        if (!type.equalsIgnoreCase("CLT") && !type.equalsIgnoreCase("MEI")) {
            System.out.println("Tipo inválido. Use CLT ou MEI.");
            return;
        }

        Promoter promoter = new Promoter();

        promoter.setName(name);
        promoter.setCpf(cpf);
        promoter.setPhone(phone);
        promoter.setDateBirth(dateBirth);
        promoter.setSalary(salary);
        promoter.setType(type.toUpperCase());
        promoter.setActive(true);

        int id = promoterDAO.save(promoter);

        if (id != -1) {
            System.out.println("Promotor cadastrado com ID: " + id);
        }
    }

    public void listAll() {

        List<Promoter> list = promoterDAO.findAll();

        if (list.isEmpty()) {
            System.out.println("Nenhum promotor cadastrado.");
            return;
        }

        for (Promoter p : list) {
            printPromoter(p);
        }
    }

    public void findById(int id) {

        Promoter p = promoterDAO.findById(id);

        if (p == null) {
            System.out.println("Promotor não encontrado.");
            return;
        }

        printPromoter(p);
    }

    public void update(int id, String name, String phone, BigDecimal salary, String type) {

        Promoter p = promoterDAO.findById(id);

        if (p == null) {
            System.out.println("Promotor não encontrado.");
            return;
        }

        if (!type.equalsIgnoreCase("CLT") && !type.equalsIgnoreCase("MEI")) {
            System.out.println("Tipo inválido. Use CLT ou MEI.");
            return;
        }

        p.setName(name);
        p.setPhone(phone);
        p.setSalary(salary);
        p.setType(type.toUpperCase());

        promoterDAO.update(p);
    }

    public void inactivate(int id) {

        Promoter p = promoterDAO.findById(id);

        if (p == null) {
            System.out.println("Promotor não encontrado.");
            return;
        }

        promoterDAO.inactivate(id);
    }

    private void printPromoter(Promoter p) {

        System.out.println(
                p.getId() + " | " +
                        p.getName() + " | " +
                        "CPF: " + p.getCpf() + " | " +
                        "Telefone: " + p.getPhone() + " | " +
                        "Nascimento: " + p.getDateBirth() + " | " +
                        "Salário: R$ " + p.getSalary() + " | " +
                        "Tipo: " + p.getType() + " | " +
                        (p.isActive() ? "ATIVO" : "INATIVO")
        );
    }

    private boolean isValidCpf(String cpf) {

        if (cpf.length() != 11) {
            return false;
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int sum = 0;

        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }

        int firstDigit = 11 - (sum % 11);

        if (firstDigit >= 10) {
            firstDigit = 0;
        }

        if (firstDigit != Character.getNumericValue(cpf.charAt(9))) {
            return false;
        }

        sum = 0;

        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }

        int secondDigit = 11 - (sum % 11);

        if (secondDigit >= 10) {
            secondDigit = 0;
        }

        return secondDigit == Character.getNumericValue(cpf.charAt(10));
    }
}