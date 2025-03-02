package com.tms.app.services.tag;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.tms.app.dtos.tag.request.TagRequest;
import com.tms.app.dtos.tag.response.TagResponse;
import com.tms.app.dtos.wrapper.PaginationResponse;
import com.tms.app.entities.tag.Tag;
import com.tms.app.exceptions.BadRequestException;
import com.tms.app.repositories.tag.TagRepository;
import com.tms.app.services.redis.RedisService;
import com.tms.app.services.tag.Impl.TagServiceImpl;
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
class TagServiceTest {

    @Mock
    private RedisService redisService;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    @BeforeEach
    void setUp() {
        reset(redisService, tagRepository);
    }

    // Tests for create(TagRequest request)
    @Test
    void create_success() {
        TagRequest request = new TagRequest("tag1", "desc");
        Tag savedTag = new Tag("tag1", "desc");
        when(tagRepository.findByTagName("tag1")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(savedTag);

        TagResponse response = tagService.create(request);

        assertNotNull(response);
        assertEquals("tag1", response.getTagName());
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    void create_duplicateTag_throwsException() {
        TagRequest request = new TagRequest("tag1", "desc");
        when(tagRepository.findByTagName("tag1")).thenReturn(Optional.of(new Tag()));

        assertThrows(BadRequestException.class, () -> tagService.create(request));
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void create_nullDescription() {
        TagRequest request = new TagRequest("tag1", null);
        Tag savedTag = new Tag("tag1", null);
        when(tagRepository.findByTagName("tag1")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(savedTag);

        TagResponse response = tagService.create(request);

        assertNull(response.getTagDescription());
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    // Tests for update(TagRequest request)
    @Test
    void update_success() {
        TagRequest request = new TagRequest("tag1", "new desc");
        Tag savedTag = new Tag("tag1", "new desc");
        when(tagRepository.findByTagName("tag1")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(savedTag);

        TagResponse response = tagService.update(request);

        assertEquals("new desc", response.getTagDescription());
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    void update_existingTag_throwsException() {
        TagRequest request = new TagRequest("tag1", "desc");
        when(tagRepository.findByTagName("tag1")).thenReturn(Optional.of(new Tag()));

        assertThrows(BadRequestException.class, () -> tagService.update(request));
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void update_nullDescription() {
        TagRequest request = new TagRequest("tag1", null);
        Tag savedTag = new Tag("tag1", null);
        when(tagRepository.findByTagName("tag1")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(savedTag);

        TagResponse response = tagService.update(request);

        assertNull(response.getTagDescription());
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    // Tests for findById(UUID id)
    @Test
    void findById_successFromDb() {
        UUID id = UUID.randomUUID();
        Tag tag = new Tag("tag1", "desc");
        when(redisService.getCachedData(anyString(), eq(id.toString()), anyString(), eq(TagResponse.class))).thenReturn(null);
        when(tagRepository.findById(id)).thenReturn(Optional.of(tag));

        TagResponse response = tagService.findById(id);

        assertEquals("tag1", response.getTagName());
        verify(tagRepository, times(1)).findById(id);
    }

    @Test
    void findById_cached() {
        UUID id = UUID.randomUUID();
        TagResponse cachedResponse = new TagResponse(new Tag("tag1", "desc"));
        when(redisService.getCachedData(anyString(), eq(id.toString()), anyString(), eq(TagResponse.class))).thenReturn(cachedResponse);

        TagResponse response = tagService.findById(id);

        assertEquals(cachedResponse, response);
        verify(tagRepository, never()).findById(id);
    }

    @Test
    void findById_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(redisService.getCachedData(anyString(), eq(id.toString()), anyString(), eq(TagResponse.class))).thenReturn(null);
        when(tagRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> tagService.findById(id));
    }

    // Tests for findByName(String name)
    @Test
    void findByName_successFromDb() {
        String name = "tag1";
        Tag tag = new Tag("tag1", "desc");
        when(redisService.getCachedData(anyString(), eq(name), anyString(), eq(TagResponse.class))).thenReturn(null);
        when(tagRepository.findByTagName(name)).thenReturn(Optional.of(tag));

        TagResponse response = tagService.findByName(name);

        assertEquals("tag1", response.getTagName());
        verify(tagRepository, times(1)).findByTagName(name);
    }

    @Test
    void findByName_cached() {
        String name = "tag1";
        TagResponse cachedResponse = new TagResponse(new Tag("tag1", "desc"));
        when(redisService.getCachedData(anyString(), eq(name), anyString(), eq(TagResponse.class))).thenReturn(cachedResponse);

        TagResponse response = tagService.findByName(name);

        assertEquals(cachedResponse, response);
        verify(tagRepository, never()).findByTagName(name);
    }

    @Test
    void findByName_notFound_throwsException() {
        String name = "invalid";
        when(redisService.getCachedData(anyString(), eq(name), anyString(), eq(TagResponse.class))).thenReturn(null);
        when(tagRepository.findByTagName(name)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> tagService.findByName(name));
    }

    // Tests for findAll(Integer pageNo, Integer pageSize)
    @Test
    void findAll_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Tag tag = new Tag("tag1", "desc");
        Page<Tag> page = new PageImpl<>(Collections.singletonList(tag), pageable, 1);
        when(redisService.getCachedData(anyString(), anyString(), any())).thenReturn(null);
        when(tagRepository.findAll(pageable)).thenReturn(page);

        PaginationResponse<TagResponse> response = tagService.findAll(0, 10);

        assertFalse(response.getContent().isEmpty());
        assertEquals(1, response.getContent().size());
        verify(tagRepository, times(1)).findAll(pageable);
    }

    @Test
    void findAll_emptyResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Tag> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(redisService.getCachedData(anyString(), anyString(), any())).thenReturn(null);
        when(tagRepository.findAll(pageable)).thenReturn(emptyPage);

        PaginationResponse<TagResponse> response = tagService.findAll(0, 10);

        assertTrue(response.getContent().isEmpty());
        verify(tagRepository, times(1)).findAll(pageable);
    }
}