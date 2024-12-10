package telran.employees;

import telran.io.*;
import telran.net.*;

import static telran.employees.ServerConfigProperties.*;

public class Main {

    public static void main(String[] args) {
        Company company = new CompanyImpl();
        Protocol protocol = new ProtocolEmployee(company);
        TcpServer server = new TcpServer(protocol, PORT);
        CompanySaver companySaver = new CompanySaver(company);
        if (company instanceof Persistable persistable) {
            persistable.restoreFromFile(FILE_NAME);
            System.out.printf("State of company restored from the file %s\n", FILE_NAME);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> persistable.saveToFile(FILE_NAME)));
        }
        Thread threadTcpServer = new Thread(server);
        Thread threadCompanySaver = new Thread(companySaver);
        threadTcpServer.start();
        threadCompanySaver.setDaemon(true);
        threadCompanySaver.start();
    }
}