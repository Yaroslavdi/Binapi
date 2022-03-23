package ru.practice.binapi.web.rest;

import ru.practice.binapi.BinapiApp;
import ru.practice.binapi.domain.TODO;
import ru.practice.binapi.repository.TODORepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link TODOResource} REST controller.
 */
@SpringBootTest(classes = BinapiApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class TODOResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private TODORepository tODORepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTODOMockMvc;

    private TODO tODO;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TODO createEntity(EntityManager em) {
        TODO tODO = new TODO()
            .name(DEFAULT_NAME)
            .date(DEFAULT_DATE);
        return tODO;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TODO createUpdatedEntity(EntityManager em) {
        TODO tODO = new TODO()
            .name(UPDATED_NAME)
            .date(UPDATED_DATE);
        return tODO;
    }

    @BeforeEach
    public void initTest() {
        tODO = createEntity(em);
    }

    @Test
    @Transactional
    public void createTODO() throws Exception {
        int databaseSizeBeforeCreate = tODORepository.findAll().size();
        // Create the TODO
        restTODOMockMvc.perform(post("/api/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(tODO)))
            .andExpect(status().isCreated());

        // Validate the TODO in the database
        List<TODO> tODOList = tODORepository.findAll();
        assertThat(tODOList).hasSize(databaseSizeBeforeCreate + 1);
        TODO testTODO = tODOList.get(tODOList.size() - 1);
        assertThat(testTODO.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTODO.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    public void createTODOWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = tODORepository.findAll().size();

        // Create the TODO with an existing ID
        tODO.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTODOMockMvc.perform(post("/api/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(tODO)))
            .andExpect(status().isBadRequest());

        // Validate the TODO in the database
        List<TODO> tODOList = tODORepository.findAll();
        assertThat(tODOList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllTODOS() throws Exception {
        // Initialize the database
        tODORepository.saveAndFlush(tODO);

        // Get all the tODOList
        restTODOMockMvc.perform(get("/api/todos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tODO.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getTODO() throws Exception {
        // Initialize the database
        tODORepository.saveAndFlush(tODO);

        // Get the tODO
        restTODOMockMvc.perform(get("/api/todos/{id}", tODO.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tODO.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingTODO() throws Exception {
        // Get the tODO
        restTODOMockMvc.perform(get("/api/todos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTODO() throws Exception {
        // Initialize the database
        tODORepository.saveAndFlush(tODO);

        int databaseSizeBeforeUpdate = tODORepository.findAll().size();

        // Update the tODO
        TODO updatedTODO = tODORepository.findById(tODO.getId()).get();
        // Disconnect from session so that the updates on updatedTODO are not directly saved in db
        em.detach(updatedTODO);
        updatedTODO
            .name(UPDATED_NAME)
            .date(UPDATED_DATE);

        restTODOMockMvc.perform(put("/api/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedTODO)))
            .andExpect(status().isOk());

        // Validate the TODO in the database
        List<TODO> tODOList = tODORepository.findAll();
        assertThat(tODOList).hasSize(databaseSizeBeforeUpdate);
        TODO testTODO = tODOList.get(tODOList.size() - 1);
        assertThat(testTODO.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTODO.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingTODO() throws Exception {
        int databaseSizeBeforeUpdate = tODORepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTODOMockMvc.perform(put("/api/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(tODO)))
            .andExpect(status().isBadRequest());

        // Validate the TODO in the database
        List<TODO> tODOList = tODORepository.findAll();
        assertThat(tODOList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteTODO() throws Exception {
        // Initialize the database
        tODORepository.saveAndFlush(tODO);

        int databaseSizeBeforeDelete = tODORepository.findAll().size();

        // Delete the tODO
        restTODOMockMvc.perform(delete("/api/todos/{id}", tODO.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TODO> tODOList = tODORepository.findAll();
        assertThat(tODOList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
