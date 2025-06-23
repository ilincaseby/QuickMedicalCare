package com.quickmedicalcare.backend.correlationDataDatabase.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_data_correlation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDataCorrelation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_private_data_id")
    private Long userPrivateDataId;

    public UserDataCorrelation(Long userId, Long userPrivateDataId) {
        this.userPrivateDataId = userPrivateDataId;
        this.userId = userId;
    }
}
