package bankslip.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bankslip.model.*;
import bankslip.view.*;
import bankslip.repository.BankSlipRepository;

@RestController
@RequestMapping(value = "/rest/bankslips")
public class BankSlipsController {

    @Autowired
    private BankSlipRepository repository;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> post(@RequestBody(required = false) @Valid BankSlip bankSlip, Errors errors) {

        if (errors.hasErrors()) {
            return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body("Invalid bankslip provided.The possible reasons are: A field of the provided bankslip was null or with invalid values.");
        }

        if (bankSlip == null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Bankslip not provided in the request body");
        }

        repository.save(bankSlip);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("Bankslip created");
    }

    @RequestMapping(method = RequestMethod.GET)
    @JsonView(View.BankSlip.class)
    public ResponseEntity<List<BankSlip>> get() {
        List<BankSlip> results = new ArrayList<BankSlip>();
        Iterable<BankSlip> bankslips = repository.findAll();

        for (BankSlip bankslip : bankslips) {
            results.add(bankslip);
        }

        return new ResponseEntity<List<BankSlip>>(results, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @JsonView(View.BankSlipWithFine.class)
    public ResponseEntity<?> get(@PathVariable("id") String id) {

        UUID uuid = parseId(id);

        if (uuid == null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid id provided - it must be a valid UUID.");
        }

        BankSlip bankSlip = repository.findById(uuid);

        if (bankSlip == null) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Bankslip not found with the specified id.");
        }

        return new ResponseEntity<BankSlip>(bankSlip, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> put(@PathVariable("id") String id, @RequestBody ObjectNode json) {

        UUID uuid = parseId(id);

        if (uuid == null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid id provided - it must be a valid UUID.");
        }

        BankSlip bankSlip = repository.findById(uuid);

        if (bankSlip == null) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Bankslip not found with the specified id.");
        }

        String status = json.get("status").asText();

        bankSlip.setStatus(status);

        String returnMessage = null;
        HttpStatus httpStatus = null;

        switch (status) {
            case "PAID":
                repository.save(bankSlip);
                returnMessage = "Bankslip paid";
                httpStatus = HttpStatus.OK;
                break;

            case "CANCELED":
                repository.save(bankSlip);
                returnMessage = "Bankslip canceled";
                httpStatus = HttpStatus.OK;
                break;

            default:
                returnMessage = "Invalid option";
                httpStatus = HttpStatus.BAD_REQUEST;
                break;
        }

        return ResponseEntity
                .status(httpStatus)
                .body(returnMessage);
    }

    private UUID parseId(String id) {
        UUID uuid = null;

        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException exception) {
        }

        return uuid;
    }

}
