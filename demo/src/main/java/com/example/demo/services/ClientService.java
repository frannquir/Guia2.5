package com.example.demo.services;

import com.example.demo.dto.request.ClientRequest;
import com.example.demo.dto.response.ClientResponse;
import com.example.demo.entities.Client;
import com.example.demo.mappers.ClientMapper;
import com.example.demo.repositories.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Autowired
    public ClientService(ClientRepository clientRepository
            , ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    public ClientResponse findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Entity with id " + id + " not found."));
        return clientMapper.toResponse(client);
    }

    public ClientResponse save(@Valid ClientRequest clientRequest) {
        Client client = clientMapper.toEntity(clientRequest);

        client = clientRepository.save(client);
        return clientMapper.toResponse(client);
    }

}
