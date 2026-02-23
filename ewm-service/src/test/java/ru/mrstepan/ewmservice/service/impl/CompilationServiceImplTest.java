package ru.mrstepan.ewmservice.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.mrstepan.ewmservice.dao.CompilationRepository;
import ru.mrstepan.ewmservice.dao.EventRepository;
import ru.mrstepan.ewmservice.dao.RequestRepository;
import ru.mrstepan.ewmservice.dto.CompilationDto;
import ru.mrstepan.ewmservice.dto.CompilationEditDto;
import ru.mrstepan.ewmservice.dto.NewCompilationDto;
import ru.mrstepan.ewmservice.exception.NotFoundException;
import ru.mrstepan.ewmservice.model.Compilation;
import ru.mrstepan.ewmservice.model.Event;
import ru.mrstepan.ewmservice.model.RequestStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompilationServiceImplTest {

    @Mock
    private CompilationRepository compilationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private CompilationServiceImpl compilationService;

    private Compilation compilation;
    private NewCompilationDto newCompilationDto;
    private Event event;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");

        compilation = new Compilation();
        compilation.setId(1L);
        compilation.setTitle("Test Compilation");
        compilation.setPinned(false);
        compilation.setEvents(new HashSet<>());

        newCompilationDto = new NewCompilationDto();
        newCompilationDto.setTitle("Test Compilation");
        newCompilationDto.setPinned(false);
        newCompilationDto.setEvents(List.of());
    }

    @Test
    void getCompilations_NoPinnedFilter_Success() {
        Page<Compilation> compilationPage = new PageImpl<>(List.of(compilation));

        when(compilationRepository.findAll(any(PageRequest.class))).thenReturn(compilationPage);

        List<CompilationDto> result = compilationService.getCompilations(null, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getCompilations_WithPinnedFilter_Success() {
        when(compilationRepository.findAllByPinned(eq(true), any(PageRequest.class)))
                .thenReturn(List.of(compilation));

        List<CompilationDto> result = compilationService.getCompilations(true, 0, 10);

        assertNotNull(result);
    }

    @Test
    void getCompilationById_Success() {
        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation));

        CompilationDto result = compilationService.getCompilationById(1L);

        assertNotNull(result);
        assertEquals("Test Compilation", result.getTitle());
    }

    @Test
    void getCompilationById_NotFound() {
        when(compilationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                compilationService.getCompilationById(999L));
    }

    @Test
    void addCompilation_Success_NoEvents() {
        when(compilationRepository.save(any(Compilation.class))).thenReturn(compilation);

        CompilationDto result = compilationService.addCompilation(newCompilationDto);

        assertNotNull(result);
        assertEquals("Test Compilation", result.getTitle());
        verify(compilationRepository).save(any(Compilation.class));
    }

    @Test
    void addCompilation_Success_WithEvents() {
        newCompilationDto.setEvents(List.of(1L));
        Set<Event> events = Set.of(event);
        compilation.setEvents(events);

        when(eventRepository.findAllById(List.of(1L))).thenReturn(List.of(event));
        when(compilationRepository.save(any(Compilation.class))).thenReturn(compilation);
        when(requestRepository.countByEventIdAndStatus(anyLong(), eq(RequestStatus.CONFIRMED))).thenReturn(0L);

        CompilationDto result = compilationService.addCompilation(newCompilationDto);

        assertNotNull(result);
        verify(eventRepository).findAllById(List.of(1L));
    }

    @Test
    void deleteCompilation_Success() {
        when(compilationRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> compilationService.deleteCompilation(1L));

        verify(compilationRepository).deleteById(1L);
    }

    @Test
    void deleteCompilation_NotFound() {
        when(compilationRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                compilationService.deleteCompilation(999L));

        verify(compilationRepository, never()).deleteById(anyLong());
    }

    @Test
    void editCompilation_Success_UpdateTitle() {
        CompilationEditDto editDto = new CompilationEditDto();
        editDto.setTitle("Updated Title");

        Compilation updatedCompilation = new Compilation();
        updatedCompilation.setId(1L);
        updatedCompilation.setTitle("Updated Title");
        updatedCompilation.setPinned(false);
        updatedCompilation.setEvents(new HashSet<>());

        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation));
        when(compilationRepository.save(any(Compilation.class))).thenReturn(updatedCompilation);

        CompilationDto result = compilationService.editCompilation(1L, editDto);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
    }

    @Test
    void editCompilation_Success_UpdatePinned() {
        CompilationEditDto editDto = new CompilationEditDto();
        editDto.setPinned(true);

        Compilation updatedCompilation = new Compilation();
        updatedCompilation.setId(1L);
        updatedCompilation.setTitle("Test Compilation");
        updatedCompilation.setPinned(true);
        updatedCompilation.setEvents(new HashSet<>());

        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation));
        when(compilationRepository.save(any(Compilation.class))).thenReturn(updatedCompilation);

        CompilationDto result = compilationService.editCompilation(1L, editDto);

        assertNotNull(result);
        assertTrue(result.getPinned());
    }

    @Test
    void editCompilation_Success_UpdateEvents() {
        CompilationEditDto editDto = new CompilationEditDto();
        editDto.setEvents(List.of(1L));

        Set<Event> events = Set.of(event);
        Compilation updatedCompilation = new Compilation();
        updatedCompilation.setId(1L);
        updatedCompilation.setTitle("Test Compilation");
        updatedCompilation.setPinned(false);
        updatedCompilation.setEvents(events);

        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation));
        when(eventRepository.findAllById(List.of(1L))).thenReturn(List.of(event));
        when(compilationRepository.save(any(Compilation.class))).thenReturn(updatedCompilation);
        when(requestRepository.countByEventIdAndStatus(anyLong(), eq(RequestStatus.CONFIRMED))).thenReturn(0L);

        CompilationDto result = compilationService.editCompilation(1L, editDto);

        assertNotNull(result);
        verify(eventRepository).findAllById(List.of(1L));
    }

    @Test
    void editCompilation_NotFound() {
        CompilationEditDto editDto = new CompilationEditDto();
        editDto.setTitle("New Title");

        when(compilationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                compilationService.editCompilation(999L, editDto));
    }
}
