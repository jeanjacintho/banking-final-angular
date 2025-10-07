package com.devstoblu.banking_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationDTO(
        @NotBlank
        @JsonProperty("login")
        String login,
        @NotBlank
        @JsonProperty("password")
        String password
) {}
