import controller.FinanceController;
import controller.RequestController;
import controller.UserController;
import model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        UserController userController = new UserController();
        RequestController requestController = new RequestController();
        FinanceController financeController = new FinanceController();

        System.out.println("=== SISTEMA AT PROMO ===");

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Senha: ");
        String password = sc.nextLine();

        User loggedUser = userController.login(email, password);

        if (loggedUser == null) {
            System.out.println("Encerrando sistema...");
            return;
        }

        if (loggedUser.getJobTittle().trim().equalsIgnoreCase("RH")) {
            menuRH(sc, loggedUser, requestController);
        } else if (loggedUser.getJobTittle().trim().equalsIgnoreCase("FINANCEIRO")) {
            menuFinanceiro(sc, requestController, financeController);
        } else {
            System.out.println("Cargo sem permissão.");
        }

        sc.close();
    }

    public static void menuRH(Scanner sc, User loggedUser, RequestController requestController) {

        int option;

        do {
            System.out.println("\n=== MENU RH ===");
            System.out.println("1 - Criar solicitação");
            System.out.println("2 - Listar solicitações");
            System.out.println("3 - Listar solicitações por período");
            System.out.println("0 - Sair");

            option = readInt(sc, "Escolha: ");

            switch (option) {
                case 1:
                    int idFinanceiro = readInt(sc, "ID do usuário financeiro: ");
                    int idPromoter = readInt(sc, "ID do promotor: ");

                    System.out.print("Tipo (BONIFICACAO / AJUDA_CUSTO / DESCONTO): ");
                    String type = sc.nextLine();

                    BigDecimal amount = readBigDecimal(sc, "Valor: ");

                    System.out.print("Mensagem: ");
                    String message = sc.nextLine();

                    requestController.createRequest(
                            loggedUser.getId(),
                            idFinanceiro,
                            idPromoter,
                            type,
                            amount,
                            message
                    );
                    break;

                case 2:
                    requestController.listAllWithPromoterName();
                    break;

                case 3:
                    listRequestsByPeriod(sc, requestController);
                    break;

                case 0:
                    System.out.println("Saindo do menu RH...");
                    break;

                default:
                    System.out.println("Opção inválida.");
            }

        } while (option != 0);
    }

    public static void menuFinanceiro(Scanner sc, RequestController requestController, FinanceController financeController) {

        int option;

        do {
            System.out.println("\n=== MENU FINANCEIRO ===");
            System.out.println("1 - Listar solicitações pendentes");
            System.out.println("2 - Listar todas as solicitações");
            System.out.println("3 - Aprovar solicitação");
            System.out.println("4 - Rejeitar solicitação");
            System.out.println("5 - Listar solicitações por período");
            System.out.println("6 - Listar financeiro por período");
            System.out.println("7 - Relatório financeiro completo");
            System.out.println("8 - Relatório financeiro por tipo");
            System.out.println("0 - Sair");

            option = readInt(sc, "Escolha: ");

            switch (option) {
                case 1:
                    requestController.listByStatus("PENDENTE");
                    break;

                case 2:
                    requestController.listAllWithPromoterName();
                    break;

                case 3:
                    int idApprove = readInt(sc, "ID da solicitação para aprovar: ");
                    requestController.approve(idApprove);
                    break;

                case 4:
                    int idReject = readInt(sc, "ID da solicitação para rejeitar: ");
                    requestController.reject(idReject);
                    break;

                case 5:
                    listRequestsByPeriod(sc, requestController);
                    break;

                case 6:
                    listFinanceByPeriod(sc, financeController);
                    break;

                case 7:
                    reportFinanceByPeriod(sc, financeController);
                    break;

                case 8:
                    reportFinanceByTypeAndPeriod(sc, financeController);
                    break;

                case 0:
                    System.out.println("Saindo do menu financeiro...");
                    break;

                default:
                    System.out.println("Opção inválida.");
            }

        } while (option != 0);
    }

    public static int readInt(Scanner sc, String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Número inválido! Digite um número inteiro.");
            }
        }
    }

    public static BigDecimal readBigDecimal(Scanner sc, String message) {
        while (true) {
            try {
                System.out.print(message);
                String input = sc.nextLine().replace(",", ".");
                return new BigDecimal(input);
            } catch (Exception e) {
                System.out.println("Valor inválido! Exemplo: 500.00");
            }
        }
    }

    public static LocalDate readDate(Scanner sc, String message) {
        while (true) {
            try {
                System.out.print(message);
                return LocalDate.parse(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Data inválida! Use AAAA-MM-DD.");
            }
        }
    }

    public static void listRequestsByPeriod(Scanner sc, RequestController requestController) {

        LocalDate start = readDate(sc, "Data início (AAAA-MM-DD): ");
        LocalDate end = readDate(sc, "Data fim (AAAA-MM-DD): ");

        if (start.isAfter(end)) {
            System.out.println("Erro: data inicial maior que final.");
            return;
        }

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        requestController.listByPeriod(startDateTime, endDateTime);
    }

    public static void listFinanceByPeriod(Scanner sc, FinanceController financeController) {

        LocalDate start = readDate(sc, "Data início (AAAA-MM-DD): ");
        LocalDate end = readDate(sc, "Data fim (AAAA-MM-DD): ");

        if (start.isAfter(end)) {
            System.out.println("Erro: data inicial maior que final.");
            return;
        }

        financeController.listByPeriod(start, end);
    }

    public static void reportFinanceByPeriod(Scanner sc, FinanceController financeController) {

        LocalDate start = readDate(sc, "Data início (AAAA-MM-DD): ");
        LocalDate end = readDate(sc, "Data fim (AAAA-MM-DD): ");

        if (start.isAfter(end)) {
            System.out.println("Erro: data inicial maior que final.");
            return;
        }

        financeController.showReportByPeriod(start, end);
    }

    public static void reportFinanceByTypeAndPeriod(Scanner sc, FinanceController financeController) {

        LocalDate start = readDate(sc, "Data início (AAAA-MM-DD): ");
        LocalDate end = readDate(sc, "Data fim (AAAA-MM-DD): ");

        if (start.isAfter(end)) {
            System.out.println("Erro: data inicial maior que final.");
            return;
        }

        financeController.showReportByTypeAndPeriod(start, end);
    }
}