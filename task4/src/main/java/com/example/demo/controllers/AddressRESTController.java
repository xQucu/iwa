package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Address;
import com.example.demo.repository.AddressRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("addresses")
public class AddressRESTController {
    private AddressRepository addressRepository;

    @Autowired
    public AddressRESTController(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Address> findAllAddresses() {
        return addressRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Address> getAddressInfo(@PathVariable("id") long id) {
        Address address = addressRepository.findById(id).orElse(null);
        if (address == null) {
            return new ResponseEntity<Address>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Address>(address, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Address> addAddress(@RequestBody Address address) {
        addressRepository.save(address);
        return new ResponseEntity<Address>(address, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Address> updateAddress(@RequestBody Address address, @PathVariable("id") long id) {
        if (addressRepository.existsById(id)) {
            address.setId(id);
            addressRepository.save(address);
            return new ResponseEntity<Address>(address, HttpStatus.CREATED);
        }
        addressRepository.save(address);
        return new ResponseEntity<Address>(address, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Address> deleteAddress(@PathVariable("id") long id) {
        Address address = addressRepository.findById(id).orElse(null);
        if (address == null) {
            return new ResponseEntity<Address>(HttpStatus.NOT_FOUND);
        }
        addressRepository.deleteById(id);
        return new ResponseEntity<Address>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Address> updatePartOfAddress(@RequestBody Map<String, Object> updates,
            @PathVariable("id") long id) {
        Address address = addressRepository.findById(id).orElse(null);
        if (address == null) {
            return new ResponseEntity<Address>(HttpStatus.NOT_FOUND);
        }
        partialUpdate(address, updates);
        return new ResponseEntity<Address>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void substituteCollection(@RequestBody List<Address> addresses) {
        deleteAllAddresses();
        for (Address address : addresses) {
            addressRepository.save(address);
        }
        ResponseEntity.ok();
        return;
    }

    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public void deleteAllAddresses() {
        addressRepository.deleteAll();
        ResponseEntity.noContent();
        return;
    }

    private void partialUpdate(Address address, Map<String, Object> updates) {
        if (updates.containsKey("city")) {
            address.setCity((String) updates.get("city"));
        }
        if (updates.containsKey("street")) {
            address.setStreet((String) updates.get("street"));
        }
        if (updates.containsKey("number")) {
            address.setNumber((String) updates.get("number"));
        }
        if (updates.containsKey("postalCode")) {
            address.setPostalCode((String) updates.get("postalCode"));
        }
        addressRepository.save(address);
    }
}
