package telran.employees;

import telran.io.*;
import telran.net.*;
import static telran.employees.ServerConfigProperties.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Company company = new CompanyImpl();
        Protocol protocol = new ProtocolEmployee(company);
        TcpServer server = new TcpServer(protocol, PORT, BAD_RESPONSES, REQUEST_PER_SECOND, TOTAL_TIMEOUT, N_THREADS);
        CompanySaver companySaver = new CompanySaver(company);
        if (company instanceof Persistable persistable) {
            persistable.restoreFromFile(FILE_NAME);
            System.out.printf("State of company restored from the file %s\n", FILE_NAME);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> persistable.saveToFile(FILE_NAME)));
        }
        Thread threadTcpServer = new Thread(server);
        Thread threadCompanySaver = new Thread(companySaver);
        threadTcpServer.start();
        Scanner scanner = new Scanner(System.in); 
        while (true) {
            System.out.print("To shutdown server input \"shutdown\":");
            String command = scanner.nextLine();
            if (command.equals("shutdown")) {
                server.shutdown();
                break;
            }
        }
        scanner.close();
        threadCompanySaver.setDaemon(true);
        threadCompanySaver.start();
    }
}