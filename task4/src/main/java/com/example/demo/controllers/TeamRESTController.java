package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Team;
import com.example.demo.repository.TeamRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("teams")
public class TeamRESTController {
    private TeamRepository teamRepository;

    @Autowired
    public TeamRESTController(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Team> findAllTeams() {
        return teamRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Team> getTeamInfo(@PathVariable("id") long id) {
        Team team = teamRepository.findById(id).orElse(null);
        if (team == null) {
            return new ResponseEntity<Team>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Team>(team, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Team> addTeam(@RequestBody Team team) {
        teamRepository.save(team);
        return new ResponseEntity<Team>(team, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Team> updateTeam(@RequestBody Team team, @PathVariable("id") long id) {
        if (teamRepository.existsById(id)) {
            team.setId(id);
            teamRepository.save(team);
            return new ResponseEntity<Team>(team, HttpStatus.CREATED);
        }
        teamRepository.save(team);
        return new ResponseEntity<Team>(team, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Team> deleteTeam(@PathVariable("id") long id) {
        Team team = teamRepository.findById(id).orElse(null);
        if (team == null) {
            return new ResponseEntity<Team>(HttpStatus.NOT_FOUND);
        }
        teamRepository.deleteById(id);
        return new ResponseEntity<Team>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Team> updatePartOfTeam(@RequestBody Map<String, Object> updates,
            @PathVariable("id") long id) {
        Team team = teamRepository.findById(id).orElse(null);
        if (team == null) {
            return new ResponseEntity<Team>(HttpStatus.NOT_FOUND);
        }
        partialUpdate(team, updates);
        return new ResponseEntity<Team>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void substituteCollection(@RequestBody List<Team> teams) {
        deleteAllTeams();
        for (Team team : teams) {
            teamRepository.save(team);
        }
        ResponseEntity.ok();
        return;
    }

    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public void deleteAllTeams() {
        teamRepository.deleteAll();
        ResponseEntity.noContent();
        return;
    }

    private void partialUpdate(Team team, Map<String, Object> updates) {
        if (updates.containsKey("teamName")) {
            team.setTeamName((String) updates.get("teamName"));
        }
        teamRepository.save(team);
    }
}
