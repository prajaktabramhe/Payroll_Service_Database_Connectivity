package com.bridgelabz;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Arrays;

public class RestAssuredTest
{


    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
    }

    public EmployeePayrollData[] getEmployeeList() {
        Response response = RestAssured.get("/employee_payroll");
        System.out.println(response);
        System.out.println("Employee Payroll Entries in JSONSever:\n" + response.asString());
        EmployeePayrollData[] arrayOfEpms = new Gson().fromJson(response.asString(), EmployeePayrollData[].class);
        return arrayOfEpms;
    }

    @Test
    public void givenEmployeeDataInJsonServer_WhenRetrieved_ShouldMatchTheCount()
    {
        EmployeePayrollData[] arrayOfEpms = getEmployeeList();
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEpms));
        long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.REST_IO);
        Assertions.assertEquals(2, entries);
    }
}
