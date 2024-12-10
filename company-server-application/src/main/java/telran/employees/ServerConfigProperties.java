package telran.employees;

public interface ServerConfigProperties {
    int PORT = 4000;
    String FILE_NAME = "employees.data";
    int SAVING_DELAY = 5;
    int BAD_RESPONSES = 20;
    int REQUEST_PER_SECOND = 100;
    int TOTAL_TIMEOUT = 50000;
}
