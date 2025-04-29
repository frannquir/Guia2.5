package com.example.demo.mappers;

import com.example.demo.dto.request.ClientRequest;
import com.example.demo.dto.response.ClientResponse;
import com.example.demo.entities.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {
    public Client toEntity (ClientRequest clientRequest) {
        return Client.builder()
                .firstName(clientRequest.getFirstName())
                .lastName(clientRequest.getLastName())
                .email(clientRequest.getEmail())
                .build();
    }
    public ClientResponse toResponse(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .email(client.getEmail())
                .build();
    }
}
