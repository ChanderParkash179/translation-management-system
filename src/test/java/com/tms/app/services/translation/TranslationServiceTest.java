package com.tms.app.services.translation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.app.dtos.translation.request.TranslationRequest;
import com.tms.app.dtos.translation.response.TranslationResponse;
import com.tms.app.dtos.wrapper.PaginationResponse;
import com.tms.app.entities.locale.Locale;
import com.tms.app.entities.tag.Tag;
import com.tms.app.entities.translation.Translation;
import com.tms.app.entities.translationTag.TranslationTag;
import com.tms.app.exceptions.BadRequestException;
import com.tms.app.repositories.locale.LocaleRepository;
import com.tms.app.repositories.tag.TagRepository;
import com.tms.app.repositories.translation.TranslationRepository;
import com.tms.app.repositories.translationTag.TranslationTagRepository;
import com.tms.app.services.redis.RedisService;
import com.tms.app.services.translation.Impl.TranslationServiceImpl;
import com.tms.app.utils.AppConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TranslationServiceTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RedisService redisService;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private LocaleRepository localeRepository;

    @Mock
    private TranslationRepository translationRepository;

    @Mock
    private TranslationTagRepository translationTagRepository;

    @InjectMocks
    private TranslationServiceImpl translationService;

    @BeforeEach
    void setUp() {
        reset(modelMapper, objectMapper, redisService, tagRepository, localeRepository, translationRepository, translationTagRepository);
    }

    // Tests for create(TranslationRequest request)
    @Test
    void create_success() {
        TranslationRequest request = new TranslationRequest("key", "content", "en_US", List.of("tag1"));
        Locale locale = new Locale("en_US", "English", "en", true);
        Translation savedTranslation = new Translation(UUID.randomUUID(), "key", "content", locale, true);
        when(localeRepository.findByLocaleCode("en_US")).thenReturn(Optional.of(locale));
        when(translationRepository.save(any(Translation.class))).thenReturn(savedTranslation);
        when(tagRepository.findByTagName("tag1")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(new Tag("tag1", "auto-created"));
        when(modelMapper.map(any(), eq(TranslationResponse.class))).thenReturn(new TranslationResponse());

        TranslationResponse response = translationService.create(request);

        assertNotNull(response);
        verify(translationTagRepository, times(1)).save(any(TranslationTag.class));
        verify(redisService, times(2)).deleteData(anyString());
    }

    @Test
    void create_localeNotFound_throwsException() {
        TranslationRequest request = new TranslationRequest("key", "content", "en_US", null);
        when(localeRepository.findByLocaleCode("en_US")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> translationService.create(request));
        verify(translationRepository, never()).save(any());
    }

    @Test
    void create_noTags() {
        TranslationRequest request = new TranslationRequest("key", "content", "en_US", null);
        Locale locale = new Locale("en_US", "English", "en", true);
        Translation savedTranslation = new Translation(UUID.randomUUID(), "key", "content", locale, true);
        when(localeRepository.findByLocaleCode("en_US")).thenReturn(Optional.of(locale));
        when(translationRepository.save(any(Translation.class))).thenReturn(savedTranslation);
        when(modelMapper.map(any(), eq(TranslationResponse.class))).thenReturn(new TranslationResponse());

        TranslationResponse response = translationService.create(request);

        assertNotNull(response);
        verify(translationTagRepository, never()).save(any());
    }

    @Test
    void create_cacheInvalidation() {
        TranslationRequest request = new TranslationRequest("key", "content", "en_US", List.of("tag1"));
        Locale locale = new Locale("en_US", "English", "en", true);
        Translation savedTranslation = new Translation(UUID.randomUUID(), "key", "content", locale, true);
        when(localeRepository.findByLocaleCode("en_US")).thenReturn(Optional.of(locale));
        when(translationRepository.save(any(Translation.class))).thenReturn(savedTranslation);
        when(tagRepository.findByTagName("tag1")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(new Tag("tag1", "auto-created"));
        when(modelMapper.map(any(), eq(TranslationResponse.class))).thenReturn(new TranslationResponse());

        TranslationResponse response = translationService.create(request);

        verify(redisService).deleteData(AppConstants.TRANSLATION_CACHE_PREFIX + "key:en_US");
        verify(redisService).deleteData(AppConstants.TRANSLATION_JSON_EXPORT_PREFIX + "en_US");
    }

    // Tests for get(String key, String localeCode)
    @Test
    void get_successFromDb() {
        Translation translation = new Translation(UUID.randomUUID(), "key", "content", new Locale(), true);
        when(redisService.getCachedData(anyString(), eq("key:en_US"), anyString(), eq(Translation.class))).thenReturn(null);
        when(translationRepository.findByKeyAndLocaleCode("key", "en_US")).thenReturn(Optional.of(translation));
        when(modelMapper.map(any(), eq(TranslationResponse.class))).thenReturn(new TranslationResponse());

        TranslationResponse response = translationService.get("key", "en_US");

        assertNotNull(response);
        verify(redisService, times(1)).saveData(anyString(), anyString(), eq(30));
    }

    @Test
    void get_cached() {
        Translation cached = new Translation(UUID.randomUUID(), "key", "content", new Locale(), true);
        when(redisService.getCachedData(anyString(), eq("key:en_US"), anyString(), eq(Translation.class))).thenReturn(cached);
        when(modelMapper.map(any(), eq(TranslationResponse.class))).thenReturn(new TranslationResponse());

        TranslationResponse response = translationService.get("key", "en_US");

        assertNotNull(response);
        verify(translationRepository, never()).findByKeyAndLocaleCode(anyString(), anyString());
    }

    @Test
    void get_notFound_throwsException() {
        when(redisService.getCachedData(anyString(), eq("key:en_US"), anyString(), eq(Translation.class))).thenReturn(null);
        when(translationRepository.findByKeyAndLocaleCode("key", "en_US")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> translationService.get("key", "en_US"));
    }

    // Tests for update(UUID id, TranslationRequest request)
    @Test
    void update_success() {
        UUID id = UUID.randomUUID();
        TranslationRequest request = new TranslationRequest("key", "new", "en_US", List.of("tag1"));
        Translation translation = new Translation(id, "old", "old content", new Locale(), true);
        Locale locale = new Locale("en_US", "English", "en", true);
        when(translationRepository.findById(id)).thenReturn(Optional.of(translation));
        when(localeRepository.findByLocaleCode("en_US")).thenReturn(Optional.of(locale));
        when(translationRepository.save(any(Translation.class))).thenReturn(translation);
        when(tagRepository.findByTagName("tag1")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(new Tag("tag1", "auto-created"));
        when(modelMapper.map(any(), eq(TranslationResponse.class))).thenReturn(new TranslationResponse());

        TranslationResponse response = translationService.update(id, request);

        assertNotNull(response);
        verify(translationTagRepository, times(1)).deleteByTranslationId(id);
        verify(translationTagRepository, times(1)).save(any(TranslationTag.class));
    }

    @Test
    void update_translationNotFound_throwsException() {
        UUID id = UUID.randomUUID();
        TranslationRequest request = new TranslationRequest("key", "new", "en_US", null);
        when(translationRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> translationService.update(id, request));
    }

    @Test
    void update_localeNotFound_throwsException() {
        UUID id = UUID.randomUUID();
        TranslationRequest request = new TranslationRequest("key", "new", "en_US", null);
        Translation translation = new Translation(id, "old", "old content", new Locale(), true);
        when(translationRepository.findById(id)).thenReturn(Optional.of(translation));
        when(localeRepository.findByLocaleCode("en_US")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> translationService.update(id, request));
    }

    @Test
    void update_noTags() {
        UUID id = UUID.randomUUID();
        TranslationRequest request = new TranslationRequest("key", "new", null, null);
        Translation translation = new Translation(id, "old", "old content", new Locale(), true);
        when(translationRepository.findById(id)).thenReturn(Optional.of(translation));
        when(translationRepository.save(any(Translation.class))).thenReturn(translation);
        when(modelMapper.map(any(), eq(TranslationResponse.class))).thenReturn(new TranslationResponse());

        TranslationResponse response = translationService.update(id, request);

        assertNotNull(response);
        verify(translationTagRepository, never()).deleteByTranslationId(any());
    }

    // Tests for search(String key, List<String> tags, String localeCode, int pageNo, int pageSize)
    @Test
    void search_byTags() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Translation> page = new PageImpl<>(Collections.singletonList(new Translation()));
        when(translationRepository.findByTagsAndLocaleCode(eq(List.of("tag1")), eq("en_US"), eq(pageable))).thenReturn(page);
        when(modelMapper.map(any(), eq(TranslationResponse.class))).thenReturn(new TranslationResponse());

        PaginationResponse<TranslationResponse> response = translationService.search(null, List.of("tag1"), "en_US", 0, 10);

        assertFalse(response.getContent().isEmpty());
        verify(translationRepository, times(1)).findByTagsAndLocaleCode(anyList(), anyString(), any(Pageable.class));
    }

    @Test
    void search_byKey() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Translation> page = new PageImpl<>(Collections.singletonList(new Translation()));
        when(translationRepository.findByTranslationKeyContainingAndIsActiveTrue("key", pageable)).thenReturn(page);
        when(modelMapper.map(any(), eq(TranslationResponse.class))).thenReturn(new TranslationResponse());

        PaginationResponse<TranslationResponse> response = translationService.search("key", null, null, 0, 10);

        assertFalse(response.getContent().isEmpty());
        verify(translationRepository, times(1)).findByTranslationKeyContainingAndIsActiveTrue(anyString(), any(Pageable.class));
    }

    @Test
    void search_byLocaleCode() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Translation> page = new PageImpl<>(Collections.singletonList(new Translation()));
        when(translationRepository.findAllByLocaleCodePaginated("en_US", pageable)).thenReturn(page);
        when(modelMapper.map(any(), eq(TranslationResponse.class))).thenReturn(new TranslationResponse());

        PaginationResponse<TranslationResponse> response = translationService.search(null, null, "en_US", 0, 10);

        assertFalse(response.getContent().isEmpty());
        verify(translationRepository, times(1)).findAllByLocaleCodePaginated(anyString(), any(Pageable.class));
    }

    @Test
    void search_emptyResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Translation> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(translationRepository.findAllByLocaleCodePaginated("en_US", pageable)).thenReturn(emptyPage);

        PaginationResponse<TranslationResponse> response = translationService.search(null, null, "en_US", 0, 10);

        assertTrue(response.getContent().isEmpty());
    }

    @Test
    void export_cached() throws JsonProcessingException {
        when(redisService.getData(anyString())).thenReturn("{\"key1\":\"content1\"}");
        when(objectMapper.readValue(eq("{\"key1\":\"content1\"}"), any(TypeReference.class))).thenReturn(Map.of("key1", "content1"));

        Map<String, String> response = translationService.export("en_US");

        assertEquals("content1", response.get("key1"));
        verify(translationRepository, never()).findAllByLocaleCode(anyString());
    }

    @Test
    void export_emptyList() {
        when(redisService.getData(anyString())).thenReturn(null);
        when(translationRepository.findAllByLocaleCode("en_US")).thenReturn(Collections.emptyList());

        Map<String, String> response = translationService.export("en_US");

        assertTrue(response.isEmpty());
    }
}