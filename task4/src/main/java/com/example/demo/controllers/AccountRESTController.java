package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Account;
import com.example.demo.repository.AccountRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("accounts")
public class AccountRESTController {
    private AccountRepository accountRepository;

    @Autowired
    public AccountRESTController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Account> findAllAccounts() {
        return accountRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Account> getAccountInfo(@PathVariable("id") long id) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null) {
            return new ResponseEntity<Account>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Account>(account, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Account> addAccount(@RequestBody Account account) {
        accountRepository.save(account);
        return new ResponseEntity<Account>(account, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Account> updateAccount(@RequestBody Account account, @PathVariable("id") long id) {
        if (accountRepository.existsById(id)) {
            account.setId(id);
            accountRepository.save(account);
            return new ResponseEntity<Account>(account, HttpStatus.CREATED);
        }
        accountRepository.save(account);
        return new ResponseEntity<Account>(account, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Account> deleteAccount(@PathVariable("id") long id) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null) {
            return new ResponseEntity<Account>(HttpStatus.NOT_FOUND);
        }
        accountRepository.deleteById(id);
        return new ResponseEntity<Account>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Account> updatePartOfAccount(@RequestBody Map<String, Object> updates,
            @PathVariable("id") long id) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null) {
            return new ResponseEntity<Account>(HttpStatus.NOT_FOUND);
        }
        partialUpdate(account, updates);
        return new ResponseEntity<Account>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void substituteCollection(@RequestBody List<Account> accounts) {
        deleteAllAccounts();
        for (Account account : accounts) {
            accountRepository.save(account);
        }
        ResponseEntity.ok();
        return;
    }

    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public void deleteAllAccounts() {
        accountRepository.deleteAll();
        ResponseEntity.noContent();
        return;
    }

    private void partialUpdate(Account account, Map<String, Object> updates) {
        if (updates.containsKey("accountName")) {
            account.setAccountName((String) updates.get("accountName"));
        }
        accountRepository.save(account);
    }
}
