import dao.FinancePromoterDAO;
import dao.UserDAO;
import model.FinancePromoter;
import model.User;

import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        UserDAO dao = new UserDAO();

// login
        User logado = dao.login("carlos@email.com", "123");

        if (logado != null) {
            System.out.println("Login OK: " + logado.getName());
            System.out.println("Cargo: " + logado.getJobTittle());
        } else {
            System.out.println("Login inválido");
        }

    }
}