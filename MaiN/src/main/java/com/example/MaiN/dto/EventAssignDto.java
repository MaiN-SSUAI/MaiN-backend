package com.example.MaiN.dto;

import com.example.MaiN.entity.Reserv;
import com.example.MaiN.entity.EventAssign;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventAssignDto {
    private int reservId;
    private int userId;

    public EventAssign toEntity() {
        return new EventAssign(
                this.reservId,
                this.userId
        );
    }
}
