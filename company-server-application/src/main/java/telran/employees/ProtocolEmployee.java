package telran.employees;

import telran.io.*;
import telran.net.*;
import java.lang.reflect.Method;
import java.util.*;
import org.json.JSONArray;

import static telran.employees.ServerConfigProperties.*;

@SuppressWarnings("unused")
public class ProtocolEmployee implements Protocol {
    Company company;

    public ProtocolEmployee(Company company) {
        this.company = company;
    }

    @Override
    public Response getResponse(Request request) {
        String type = request.requestType();
        String data = request.requestData();
        Response response = null;
        try {
            Method method = ProtocolEmployee.class.getDeclaredMethod(type, String.class);
            method.setAccessible(true);
            response = (Response) method.invoke(this, data);
        } catch (NoSuchMethodException e) {
            response = new Response(ResponseCode.WRONG_TYPE, "Wrong type of command to server");
        } catch (Exception e) {
        }
        return response;
    }

    private Response addEmployee(String data) {
        Response res = null;
        Employee employee = Employee.getEmployeeFromJSON(data);
        try {
            company.addEmployee(employee);
            res = new Response(ResponseCode.OK, "");
        } catch (IllegalStateException e) {
            res = new Response(ResponseCode.WRONG_DATA, 
                    "Employee with this id already presents in the company");
        }
        return res;
    }

    private Response removeEmployee(String data) {
        Response res = null;
        long id = Integer.valueOf(data);
        try {
            company.removeEmployee(id);
            res = new Response(ResponseCode.OK, "");
        } catch (NoSuchElementException e) {
            res = new Response(ResponseCode.WRONG_DATA, 
                    "Employee with this id is not found in the company");
        }
        return res;
    }

    private Response getEmployee(String data) {
        Response res = null;
        long id = Long.valueOf(data);
        Employee employee = company.getEmployee(id);
        if (employee != null) {
            res = new Response(ResponseCode.OK, employee.toString());
        } else {
            res = new Response(ResponseCode.WRONG_DATA, 
                    "Employee with this id is not found in the company");
        }
        return res;
    }

    private Response getManagersWithMostFactor(String data) {
        Manager[] managers = company.getManagersWithMostFactor();
        String[] jsonStrings = Arrays.stream(managers).map(Manager::toString).toArray(String[]::new);
        JSONArray jsonArray = new JSONArray(jsonStrings);
        return new Response(ResponseCode.OK, jsonArray.toString());
    }

    private Response getDepartments(String data) {
        String[] departments = company.getDepartments();
        JSONArray departmentsJSON = new JSONArray(departments);
        return new Response(ResponseCode.OK, departmentsJSON.toString());
    }

    private Response getDepartmentBudget(String data) {
        int buget = company.getDepartmentBudget(data);
        return new Response(ResponseCode.OK, buget + "");
    }

    private Response saveCompany(String data) {
        Response res = null;
        if (company instanceof Persistable persistable) {
            try {
                persistable.saveToFile(FILE_NAME);
                res = new Response(ResponseCode.OK, "");
            } catch (Exception e) {
                res = new Response(ResponseCode.WRONG_DATA, e.getMessage());
            }
        }
        return res;
    }
}
