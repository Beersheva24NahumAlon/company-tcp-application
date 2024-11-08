package telran.employees;

import telran.io.*;
import telran.net.*;

import static telran.employees.ServerConfigProperties.*;

public class Main {
    private static final int PORT = 4000;

    public static void main(String[] args) {
        Company company = new CompanyImpl();
        Protocol protocol = new ProtocolEmployee(company);
        TcpServer server = new TcpServer(protocol, PORT);
        if (company instanceof Persistable persistable) {
            persistable.restoreFromFile(FILE_NAME);
        }
        server.run();
    }
}