package com.example.demo.controllers;

import com.example.demo.dto.request.ClientRequest;
import com.example.demo.dto.response.ClientResponse;
import com.example.demo.entities.Client;
import com.example.demo.services.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<ClientResponse> createClient (@Valid @RequestBody ClientRequest clientRequest) {
        ClientResponse clientResponse = clientService.save(clientRequest);
        return new ResponseEntity<>(clientResponse, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getClientByID (@PathVariable Long id) {
        ClientResponse clientResponse = clientService.findById(id);
        return ResponseEntity.ok(clientResponse);
    }

}
