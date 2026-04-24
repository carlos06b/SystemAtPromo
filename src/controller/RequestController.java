package controller;

import dao.FinancePromoterDAO;
import dao.PromoterDAO;
import dao.RequestDAO;
import model.FinancePromoter;
import model.Request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class RequestController {

    private RequestDAO requestDAO = new RequestDAO();
    private PromoterDAO promoterDAO = new PromoterDAO();

    public void createRequest(int idUserRH, int idUserFin, int idPromoter,
                              String type, BigDecimal amount, String message) {

        if (promoterDAO.findById(idPromoter) == null) {
            System.out.println("Promotor não existe!");
            return;
        }

        Request request = new Request();

        request.setId_UserRH(idUserRH);
        request.setId_UserFin(idUserFin);
        request.setId_Promoter(idPromoter);
        request.setType(type);
        request.setAmount(amount);
        request.setMessage(message);
        request.setStatus("PENDENTE");
        request.setDate(LocalDateTime.now());

        requestDAO.save(request);
    }

    public void listAll() {

        List<Request> list = requestDAO.findAll();

        if (list.isEmpty()) {
            System.out.println("Nenhuma solicitação encontrada.");
            return;
        }

        printRequests(list);
    }

    public void listAllWithPromoterName() {

        List<String> list = requestDAO.findAllWithPromoterName();

        if (list.isEmpty()) {
            System.out.println("Nenhuma solicitação encontrada.");
            return;
        }

        for (String line : list) {
            System.out.println(line);
        }
    }

    public void listByStatus(String status) {

        List<Request> list = requestDAO.findByStatus(status);

        if (list.isEmpty()) {
            System.out.println("Nenhuma solicitação com status: " + status);
            return;
        }

        printRequests(list);
    }

    public void listByPeriod(LocalDateTime start, LocalDateTime end) {

        List<Request> list = requestDAO.findByPeriod(start, end);

        if (list.isEmpty()) {
            System.out.println("Nenhuma solicitação nesse período.");
            return;
        }

        printRequests(list);
    }

    public void approve(int id) {

        List<Request> list = requestDAO.findAll();

        for (Request r : list) {

            if (r.getId() == id) {

                if (!r.getStatus().equalsIgnoreCase("PENDENTE")) {
                    System.out.println("Essa solicitação já foi analisada.");
                    return;
                }

                if (promoterDAO.findById(r.getId_Promoter()) == null) {
                    System.out.println("Erro: promotor não existe mais.");
                    return;
                }

                requestDAO.updateStatus(id, "APROVADO");

                FinancePromoter finance = new FinancePromoter();

                finance.setIdPromoter(r.getId_Promoter());
                finance.setType(r.getType());
                finance.setAmount(r.getAmount());
                finance.setDate(LocalDate.now());
                finance.setStatus("PAGO");

                FinancePromoterDAO financeDAO = new FinancePromoterDAO();
                financeDAO.save(finance);

                System.out.println("Solicitação aprovada e lançada no financeiro.");
                return;
            }
        }

        System.out.println("Solicitação não encontrada.");
    }

    public void reject(int id) {

        List<Request> list = requestDAO.findAll();

        for (Request r : list) {

            if (r.getId() == id) {

                if (!r.getStatus().equalsIgnoreCase("PENDENTE")) {
                    System.out.println("Essa solicitação já foi analisada.");
                    return;
                }

                requestDAO.updateStatus(id, "REJEITADO");
                System.out.println("Solicitação rejeitada.");
                return;
            }
        }

        System.out.println("Solicitação não encontrada.");
    }

    public void delete(int id) {
        requestDAO.delete(id);
    }

    private void printRequests(List<Request> list) {

        for (Request r : list) {
            System.out.println(
                    r.getId() + " | " +
                            "Promotor: " + r.getId_Promoter() + " | " +
                            r.getType() + " | " +
                            "R$ " + r.getAmount() + " | " +
                            r.getMessage() + " | " +
                            r.getStatus() + " | " +
                            r.getDate()
            );
        }
    }
}