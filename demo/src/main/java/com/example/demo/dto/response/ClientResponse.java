package com.example.demo.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}

