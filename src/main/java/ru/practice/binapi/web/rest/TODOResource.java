package ru.practice.binapi.web.rest;

import ru.practice.binapi.domain.TODO;
import ru.practice.binapi.repository.TODORepository;
import ru.practice.binapi.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link ru.practice.binapi.domain.TODO}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TODOResource {

    private final Logger log = LoggerFactory.getLogger(TODOResource.class);

    private static final String ENTITY_NAME = "tODO";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TODORepository tODORepository;

    public TODOResource(TODORepository tODORepository) {
        this.tODORepository = tODORepository;
    }

    /**
     * {@code POST  /todos} : Create a new tODO.
     *
     * @param tODO the tODO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tODO, or with status {@code 400 (Bad Request)} if the tODO has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/todos")
    public ResponseEntity<TODO> createTODO(@RequestBody TODO tODO) throws URISyntaxException {
        log.debug("REST request to save TODO : {}", tODO);
        if (tODO.getId() != null) {
            throw new BadRequestAlertException("A new tODO cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TODO result = tODORepository.save(tODO);
        return ResponseEntity.created(new URI("/api/todos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /todos} : Updates an existing tODO.
     *
     * @param tODO the tODO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tODO,
     * or with status {@code 400 (Bad Request)} if the tODO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tODO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/todos")
    public ResponseEntity<TODO> updateTODO(@RequestBody TODO tODO) throws URISyntaxException {
        log.debug("REST request to update TODO : {}", tODO);
        if (tODO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TODO result = tODORepository.save(tODO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tODO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /todos} : get all the tODOS.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tODOS in body.
     */
    @GetMapping("/todos")
    public List<TODO> getAllTODOS() {
        log.debug("REST request to get all TODOS");
        return tODORepository.findAll();
    }

    /**
     * {@code GET  /todos/:id} : get the "id" tODO.
     *
     * @param id the id of the tODO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tODO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/todos/{id}")
    public ResponseEntity<TODO> getTODO(@PathVariable Long id) {
        log.debug("REST request to get TODO : {}", id);
        Optional<TODO> tODO = tODORepository.findById(id);
        return ResponseUtil.wrapOrNotFound(tODO);
    }

    /**
     * {@code DELETE  /todos/:id} : delete the "id" tODO.
     *
     * @param id the id of the tODO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/todos/{id}")
    public ResponseEntity<Void> deleteTODO(@PathVariable Long id) {
        log.debug("REST request to delete TODO : {}", id);
        tODORepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
