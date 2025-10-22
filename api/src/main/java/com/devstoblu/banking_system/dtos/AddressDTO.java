package com.devstoblu.banking_system.dtos;

import jakarta.validation.constraints.NotBlank;

public record AddressDTO (
        @NotBlank String logradouro,
        @NotBlank String numero,
        String complemento,
        @NotBlank String bairro,
        @NotBlank String cidade,
        @NotBlank String estado,
        @NotBlank String cep
){}
