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
            menuFinanceiro(sc, requestController);
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
            System.out.print("Escolha: ");

            option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    System.out.print("ID do usuário financeiro: ");
                    int idFinanceiro = sc.nextInt();

                    System.out.print("ID do promotor: ");
                    int idPromoter = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Tipo (BONIFICACAO / AJUDA_CUSTO / DESCONTO): ");
                    String type = sc.nextLine();

                    System.out.print("Valor: ");
                    BigDecimal amount = sc.nextBigDecimal();
                    sc.nextLine();

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
                    listarPorPeriodo(sc, requestController);
                    break;

                case 0:
                    System.out.println("Saindo do menu RH...");
                    break;

                default:
                    System.out.println("Opção inválida.");
            }

        } while (option != 0);
    }

    public static void menuFinanceiro(Scanner sc, RequestController requestController) {

        int option;

        do {
            System.out.println("\n=== MENU FINANCEIRO ===");
            System.out.println("1 - Listar todas as solicitações");
            System.out.println("2 - Listar solicitações pendentes");
            System.out.println("3 - Aprovar solicitação");
            System.out.println("4 - Rejeitar solicitação");
            System.out.println("5 - Listar solicitações por período");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");

            option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    requestController.listAllWithPromoterName();
                    break;

                case 2:
                    requestController.listByStatus("PENDENTE");
                    break;

                case 3:
                    System.out.print("ID da solicitação para aprovar: ");
                    int idApprove = sc.nextInt();
                    sc.nextLine();

                    requestController.approve(idApprove);
                    break;

                case 4:
                    System.out.print("ID da solicitação para rejeitar: ");
                    int idReject = sc.nextInt();
                    sc.nextLine();

                    requestController.reject(idReject);
                    break;

                case 5:
                    listarPorPeriodo(sc, requestController);
                    break;

                case 0:
                    System.out.println("Saindo do menu financeiro...");
                    break;

                default:
                    System.out.println("Opção inválida.");
            }

        } while (option != 0);
    }

    public static void listarPorPeriodo(Scanner sc, RequestController requestController) {

        System.out.print("Data início (AAAA-MM-DD): ");
        String startStr = sc.nextLine();

        System.out.print("Data fim (AAAA-MM-DD): ");
        String endStr = sc.nextLine();

        LocalDateTime start = LocalDate.parse(startStr).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endStr).atTime(23, 59, 59);

        requestController.listByPeriod(start, end);
    }
}