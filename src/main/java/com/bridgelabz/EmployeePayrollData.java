package com.bridgelabz;

import java.time.LocalDate;
import java.util.Objects;

public class EmployeePayrollData
{
    public static Double  basic_pay;
    public String name;
    private int id;
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
