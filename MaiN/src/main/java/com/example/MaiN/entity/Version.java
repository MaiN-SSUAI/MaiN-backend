package com.example.MaiN.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "version")
public class Version {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "latest_ios")
    private String latestIos;

    @Column(name = "minimum_ios")
    private String minimumIos;

    @Column(name = "latest_android")
    private String latestAndroid;

    @Column(name = "minimum_android")
    private String minimumAndroid;
}
