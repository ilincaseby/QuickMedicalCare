package com.quickmedicalcare.backend.medicalDataDatabase.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_private_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPrivateData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "age")
    private int age;

    @Column(name = "weight")
    private double weight;

    @Column(name = "height")
    private double height;

    @Column(name = "sex")
    private int sex;

    @Column(name = "country")
    private String country;

    @Column(name = "smoker")
    private boolean smoker;

    @Column(name = "alcohol_consumption_freq")
    private int alcoholConsumptionFreq;

    public UserPrivateData(int age, double weight, double height, int sex, String country, boolean smoker, int alcoholConsumptionFreq) {
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.sex = sex;
        this.country = country;
        this.smoker = smoker;
        this.alcoholConsumptionFreq = alcoholConsumptionFreq;
    }

}
