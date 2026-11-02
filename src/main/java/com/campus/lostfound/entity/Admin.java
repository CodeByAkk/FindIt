package com.campus.lostfound.entity;
import jakarta.persistence.*;
@Entity @DiscriminatorValue("ADMIN") public class Admin extends User { protected Admin(){} public Admin(String name,String email,String password){super(name,email,password,"ROLE_ADMIN");} }
