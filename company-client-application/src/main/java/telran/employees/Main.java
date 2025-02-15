package telran.employees;

import telran.view.*;
import java.io.*;
import java.util.*;
import telran.net.*;

public class Main {

    //private static final String HOST = "54.147.210.32";
    private static final String HOST = "localhost";
    //private static final int PORT = 4000;
    private static final int PORT = 3500;
    private static final String PROTOCOL = "http";


    public static void main(String[] args) {
        InputOutput io = new StandardInputOutput();
        //NetworkClient client = new TcpClient(HOST, PORT);
        NetworkClient client = new HttpCustomClient(PROTOCOL, HOST, PORT);
        Company company = new CompanyNetProxy(client);
        Item[] items = CompanyItems.getItems(company);
        items = addExitItem(items, client);
        Menu menu = new Menu("Company network application", items);
        menu.perform(io);
        io.writeLine("Application is finished");
    }

    private static Item[] addExitItem(Item[] items, NetworkClient client) {
        Item[] res = Arrays.copyOf(items, items.length + 1);
        res[res.length - 1] = Item.of("Exit", io -> {
            try {
                if (client instanceof Closeable closeable) {
                    closeable.close();
                }
                io.writeString("Session closed correctly");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, true);
        return res;
    }
}