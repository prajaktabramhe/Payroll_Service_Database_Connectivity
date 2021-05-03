package com.bridgelabz;

import java.time.LocalDate;
import java.util.Objects;

public class EmployeePayrollData
{
    public static Double  basic_pay;
    public String name;
    public int id;
    public String gender;
    public LocalDate startDate;

    public EmployeePayrollData(Integer id, String name, Double  basic_pay)
    {
        this.id = id;
        this.name = name;
        this. basic_pay =  basic_pay;
        this.startDate = null;
    }

    public EmployeePayrollData(Integer id, String name, Double basic_pay, LocalDate startDate)
    {
        this(id, name, basic_pay);
        this.startDate = startDate;
    }

    public EmployeePayrollData(Integer id, String name, String gender, Double salary, LocalDate startDate)
    {
        this(id, name, salary, startDate);
        this.gender = gender;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, gender, basic_pay, startDate);
    }

    @Override
    public String toString()
    {
        return "id=" + id +
                ", salary=" + basic_pay +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeePayrollData that = (EmployeePayrollData) o;
        return id == that.id && Double.compare(that.basic_pay, basic_pay) == 0 && Objects.equals(name, that.name);
    }
}
