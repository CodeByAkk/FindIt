package com.campus.lostfound.entity;
import jakarta.persistence.*;
@Entity @DiscriminatorValue("STUDENT") public class Student extends User { @Column(length=30) private String studentNumber; protected Student(){} public Student(String name,String email,String password,String studentNumber){super(name,email,password,"ROLE_STUDENT");this.studentNumber=studentNumber;} public String getStudentNumber(){return studentNumber;} public void setStudentNumber(String v){studentNumber=v;} }
