package com.example.MaiN.dto;

import com.example.MaiN.entity.ReservAssign;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventAssignDto {
    private int reservId;
    private int userId;
}
