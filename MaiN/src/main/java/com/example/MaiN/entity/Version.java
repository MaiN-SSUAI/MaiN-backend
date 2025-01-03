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

    @Column(name = "present_ios")
    private String presentIos;

    @Column(name = "latest_android")
    private String latestAndroid;

    @Column(name = "present_android")
    private String presentAndroid;
}
