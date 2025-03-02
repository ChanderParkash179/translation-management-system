package com.tms.app.services.locale;

import com.tms.app.dtos.locale.request.LocaleRequest;
import com.tms.app.dtos.locale.response.LocaleResponse;
import com.tms.app.dtos.wrapper.PaginationResponse;
import com.tms.app.entities.locale.Locale;
import com.tms.app.exceptions.BadRequestException;
import com.tms.app.repositories.locale.LocaleRepository;
import com.tms.app.services.locale.Impl.LocaleServiceImpl;
import com.tms.app.services.redis.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocaleServiceTest {

    @Mock
    private RedisService redisService;

    @Mock
    private LocaleRepository localeRepository;

    @InjectMocks
    private LocaleServiceImpl localeService;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(redisService, localeRepository);
    }

    // Tests for create(LocaleRequest request)
    @Test
    void create_success() {
        LocaleRequest request = new LocaleRequest("en_US", "English", "en", true);
        Locale savedLocale = new Locale("en_US", "English", "en", true);
        when(localeRepository.findByLocaleCode("en_US")).thenReturn(Optional.empty());
        when(localeRepository.save(any(Locale.class))).thenReturn(savedLocale);

        LocaleResponse response = localeService.create(request);

        assertNotNull(response);
        assertEquals("en_US", response.getLocaleCode());
        verify(localeRepository, times(1)).save(any(Locale.class));
    }

    @Test
    void create_duplicateLocale_throwsException() {
        LocaleRequest request = new LocaleRequest("en_US", "English", "en", true);
        when(localeRepository.findByLocaleCode("en_US")).thenReturn(Optional.of(new Locale()));

        assertThrows(BadRequestException.class, () -> localeService.create(request));
        verify(localeRepository, never()).save(any(Locale.class));
    }

    @Test
    void create_nullFields() {
        LocaleRequest request = new LocaleRequest(null, "English", null, false);
        Locale savedLocale = new Locale(null, "English", null, false);
        when(localeRepository.findByLocaleCode(null)).thenReturn(Optional.empty());
        when(localeRepository.save(any(Locale.class))).thenReturn(savedLocale);

        LocaleResponse response = localeService.create(request);

        assertNull(response.getLocaleCode());
        assertEquals("English", response.getLocaleName());
        verify(localeRepository, times(1)).save(any(Locale.class));
    }

    // Tests for update(LocaleRequest request)
    @Test
    void update_success() {
        LocaleRequest request = new LocaleRequest("en_US", "English Updated", "en", true);
        Locale savedLocale = new Locale("en_US", "English Updated", "en", true);
        when(localeRepository.findByLocaleCode("en_US")).thenReturn(Optional.empty());
        when(localeRepository.save(any(Locale.class))).thenReturn(savedLocale);

        LocaleResponse response = localeService.update(request);

        assertEquals("English Updated", response.getLocaleName());
        verify(localeRepository, times(1)).save(any(Locale.class));
    }

    @Test
    void update_existingLocale_throwsException() {
        LocaleRequest request = new LocaleRequest("en_US", "English", "en", true);
        when(localeRepository.findByLocaleCode("en_US")).thenReturn(Optional.of(new Locale()));

        assertThrows(BadRequestException.class, () -> localeService.update(request));
        verify(localeRepository, never()).save(any(Locale.class));
    }

    @Test
    void update_minimalFields() {
        LocaleRequest request = new LocaleRequest("en_US", null, null, false);
        Locale savedLocale = new Locale("en_US", null, null, false);
        when(localeRepository.findByLocaleCode("en_US")).thenReturn(Optional.empty());
        when(localeRepository.save(any(Locale.class))).thenReturn(savedLocale);

        LocaleResponse response = localeService.update(request);

        assertNull(response.getLocaleName());
        verify(localeRepository, times(1)).save(any(Locale.class));
    }


    // Tests for findById(UUID id)
    @Test
    void findById_successFromDb() {
        UUID id = UUID.randomUUID();
        Locale locale = new Locale("en_US", "English", "en", true);
        when(redisService.getCachedData(anyString(), eq(id.toString()), anyString(), eq(LocaleResponse.class))).thenReturn(null);
        when(localeRepository.findById(id)).thenReturn(Optional.of(locale));

        LocaleResponse response = localeService.findById(id);

        assertEquals("en_US", response.getLocaleCode());
        verify(localeRepository, times(1)).findById(id);
    }

    @Test
    void findById_cached() {
        UUID id = UUID.randomUUID();
        LocaleResponse cachedResponse = new LocaleResponse(new Locale("en_US", "English", "en", true));
        when(redisService.getCachedData(anyString(), eq(id.toString()), anyString(), eq(LocaleResponse.class))).thenReturn(cachedResponse);

        LocaleResponse response = localeService.findById(id);

        assertEquals(cachedResponse, response);
        verify(localeRepository, never()).findById(id);
    }

    @Test
    void findById_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(redisService.getCachedData(anyString(), eq(id.toString()), anyString(), eq(LocaleResponse.class))).thenReturn(null);
        when(localeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> localeService.findById(id));
    }

    // Tests for findByCode(String code)
    @Test
    void findByCode_successFromDb() {
        String code = "en_US";
        Locale locale = new Locale("en_US", "English", "en", true);
        when(redisService.getCachedData(anyString(), eq(code), anyString(), eq(LocaleResponse.class))).thenReturn(null);
        when(localeRepository.findByLocaleCode(code)).thenReturn(Optional.of(locale));

        LocaleResponse response = localeService.findByCode(code);

        assertEquals("en_US", response.getLocaleCode());
        verify(localeRepository, times(1)).findByLocaleCode(code);
    }

    @Test
    void findByCode_cached() {
        String code = "en_US";
        LocaleResponse cachedResponse = new LocaleResponse(new Locale("en_US", "English", "en", true));
        when(redisService.getCachedData(anyString(), eq(code), anyString(), eq(LocaleResponse.class))).thenReturn(cachedResponse);

        LocaleResponse response = localeService.findByCode(code);

        assertEquals(cachedResponse, response);
        verify(localeRepository, never()).findByLocaleCode(code);
    }

    @Test
    void findByCode_notFound_throwsException() {
        String code = "invalid_code";
        when(redisService.getCachedData(anyString(), eq(code), anyString(), eq(LocaleResponse.class))).thenReturn(null);
        when(localeRepository.findByLocaleCode(code)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> localeService.findByCode(code));
    }

    // Tests for findAll(Integer pageNo, Integer pageSize)
    @Test
    void findAll_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Locale locale = new Locale("en_US", "English", "en", true);
        Page<Locale> page = new PageImpl<>(Collections.singletonList(locale), pageable, 1);
        when(redisService.getCachedData(anyString(), anyString(), any())).thenReturn(null);
        when(localeRepository.findAll(pageable)).thenReturn(page);

        PaginationResponse<LocaleResponse> response = localeService.findAll(0, 10);

        assertFalse(response.getContent().isEmpty());
        assertEquals(1, response.getContent().size());
        verify(localeRepository, times(1)).findAll(pageable);
    }

    @Test
    void findAll_emptyResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Locale> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(redisService.getCachedData(anyString(), anyString(), any())).thenReturn(null);
        when(localeRepository.findAll(pageable)).thenReturn(emptyPage);

        PaginationResponse<LocaleResponse> response = localeService.findAll(0, 10);

        assertTrue(response.getContent().isEmpty());
        verify(localeRepository, times(1)).findAll(pageable);
    }

}