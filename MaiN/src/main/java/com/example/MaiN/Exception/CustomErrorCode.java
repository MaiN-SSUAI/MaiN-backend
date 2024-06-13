package com.example.MaiN.Exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CustomErrorCode implements EnumModel {

    // COMMON
    INVALID_CODE(400, "C001", "Invalid Code"),
    RESOURCE_NOT_FOUND(204, "C002", "Resource not found"),
    EXPIRED_CODE(400, "C003", "Expired Code"),

    // AWS
    AWS_ERROR(400, "A001", "aws client error"),
    TEMPORARY_SERVER_ERROR(400,"A002" ,"temporary server error" ),

    //validCheck
    MORE_THAN_2HOURS(400, "V001", "More than 2 Hours"),
    MORE_THAN_2APPOINTS(400, "V002", "More than 2 appointments a week"),
    EVENT_OVERLAPS(400, "V003", "Event Overlaps"),
    OUT_OF_DURATION(400, "V004", "Out of Duration reservation"),

    //Reservation
    RESERVATION_ONE_PERSON(400, "R001", "Impossible to make a reservation alone");

    private int status;
    private String code;
    private String message;
    private String detail;

    CustomErrorCode(int status, String code, String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    @Override
    public String getKey() {
        return this.code;
    }

    @Override
    public String getValue() {
        return this.message;
    }
}