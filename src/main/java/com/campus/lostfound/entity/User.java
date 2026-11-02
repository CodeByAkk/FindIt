package com.campus.lostfound.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
@Entity @Table(name="users") @Inheritance(strategy=InheritanceType.SINGLE_TABLE) @DiscriminatorColumn(name="user_type")
public abstract class User {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @NotBlank @Size(max=80) @Column(nullable=false) private String name;
 @Email @NotBlank @Column(nullable=false,unique=true) private String email;
 @NotBlank @Column(nullable=false) private String password;
 @Column(nullable=false) private String role="ROLE_STUDENT";
 private boolean enabled=true;
 private LocalDateTime createdAt=LocalDateTime.now();
 protected User(){} protected User(String name,String email,String password,String role){this.name=name;this.email=email;this.password=password;this.role=role;}
 public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getPassword(){return password;} public void setPassword(String v){password=v;} public String getRole(){return role;} public void setRole(String v){role=v;} public boolean isEnabled(){return enabled;} public void setEnabled(boolean v){enabled=v;} public LocalDateTime getCreatedAt(){return createdAt;}
}
