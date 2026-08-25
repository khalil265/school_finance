package com.schoolfinance.dto.security;

public record AccountActivationCheckResponse(

        boolean valid,

        String username,

        String firstName,

        boolean expired
) {
}