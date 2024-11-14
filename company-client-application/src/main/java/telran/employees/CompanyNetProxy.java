package telran.employees;

import java.util.*;
import org.json.JSONArray;
import telran.net.NetworkClient;

public class CompanyNetProxy implements Company {
    NetworkClient client;

    public CompanyNetProxy(NetworkClient client) {
        this.client = client;
    }

    @Override
    public Iterator<Employee> iterator() {
        return null;
    }

    @Override
    public void addEmployee(Employee empl) {
        client.sendAndReceive("addEmployee", empl.toString());
    }

    @Override
    public int getDepartmentBudget(String department) {
        return Integer.parseInt(client.sendAndReceive("getDepartmentBudget", department));
    }

    @Override
    public String[] getDepartments() {
        String jsonArrayStr = client.sendAndReceive("getDepartments", "");
        JSONArray jsonArray = new JSONArray(jsonArrayStr);
        return jsonArray.toList().toArray(String[]::new);
    }

    @Override
    public Employee getEmployee(long id) {
        String employeeJSON = client.sendAndReceive("getEmployee", id + "");
        Employee employee = Employee.getEmployeeFromJSON(employeeJSON);
        return employee;
    }

    @Override
    public Manager[] getManagersWithMostFactor() {
        String jsonArrayString = client.sendAndReceive("getManagersWithMostFactor", "");
        JSONArray jsonArray = new JSONArray(jsonArrayString);
        String[] jsonObjectsStrings = jsonArray.toList().toArray(String[]::new);
        return Arrays.stream(jsonObjectsStrings).map(Employee::getEmployeeFromJSON).toArray(Manager[]::new);
    }

    @Override
    public Employee removeEmployee(long id) {
        String employeeJSON = client.sendAndReceive("removeEmployee", id + "");
        Employee employee = Employee.getEmployeeFromJSON(employeeJSON);
        return employee;
    }

    public void saveCompany() {
        client.sendAndReceive("saveCompany", "");
    }
}
