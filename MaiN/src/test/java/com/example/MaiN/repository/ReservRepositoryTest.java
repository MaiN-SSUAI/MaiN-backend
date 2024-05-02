package com.example.MaiN.repository;

import com.example.MaiN.dto.EventDto;
import com.example.MaiN.entity.Event;
import jakarta.persistence.EntityManager;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest @Transactional
class ReservRepositoryTest {
    @Autowired
    ReservRepository reservRepository;

    @Test
    public void testFindByEventId() {
        //given
        Event event = new Event();
        event.setEventId("lucytest33");
        reservRepository.save(event);
        // when
        Event result = reservRepository.findByEventId("lucytest33");
        //then
        assertThat(result).isEqualTo(event);
    }

    @Test
    public void testFindByStudentId() {
        //given
        Event event111 = new Event();
        event111.setStudentId("lucytest111");
        Event event222 = new Event();
        event222.setStudentId("lucytest111");
        Event event333 = new Event();
        event222.setStudentId("lucytest111");
        List<Event> eventList = new ArrayList<>();
        eventList.add(event111);
        eventList.add(event222);
        reservRepository.save(event111);
        reservRepository.save(event222);
        // when
        List<Event> result = reservRepository.findByStudentId("lucytest111");
        // then
        assertThat(result).isEqualTo(eventList);
    }
}
