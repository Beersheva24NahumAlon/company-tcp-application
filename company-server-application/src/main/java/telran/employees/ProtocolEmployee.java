package telran.employees;

import telran.io.*;
import telran.net.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
        } catch (InvocationTargetException e) {
            Throwable causeExc = e.getCause();
            String msg = causeExc == null ? e.getMessage() : causeExc.getMessage();
            response = new Response(ResponseCode.WRONG_DATA, msg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    private Response addEmployee(String data) {
        Employee employee = Employee.getEmployeeFromJSON(data);
        company.addEmployee(employee);
        return new Response(ResponseCode.OK, "");
    }

    private Response removeEmployee(String data) throws NoSuchElementException {
        long id = Integer.valueOf(data);
        company.removeEmployee(id);
        return new Response(ResponseCode.OK, "");
    }

    private Response getEmployee(String data) {
        long id = Long.valueOf(data);
        Employee employee = company.getEmployee(id);
        if (employee == null) {
            throw new NoSuchElementException("Employee with this id is not found in the company");
        }
        return new Response(ResponseCode.OK, employee.toString());
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
}
