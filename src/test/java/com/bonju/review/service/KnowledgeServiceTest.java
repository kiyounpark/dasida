package com.bonju.review.service;

import com.bonju.review.dto.KnowledgeRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KnowledgeServiceTest {

    @Mock
    KnowledgeService knowledgeServiceMock;

    @Test
    public void givenValidKnowledgeTextRequest_whenRegisterKnowledge_thenReturnRegisteredId(){
        //given
        KnowledgeRequestDto knowledgeRequestDto = new KnowledgeRequestDto("최고다", "이순신");
        when(knowledgeServiceMock.registerKnowledge(knowledgeRequestDto)).thenReturn(1L);

        //when
        Long knowledgeId = knowledgeServiceMock.registerKnowledge(knowledgeRequestDto);

        //then
        assertEquals(1L, knowledgeId);
    }


}