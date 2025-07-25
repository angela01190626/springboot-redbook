package com.chuwa.redbook.service.impl;

import com.chuwa.redbook.dao.CommentRepository;
import com.chuwa.redbook.dao.PostRepository;
import com.chuwa.redbook.entity.Comment;
import com.chuwa.redbook.entity.Post;
import com.chuwa.redbook.payload.CommentDto;
import com.chuwa.redbook.payload.PostDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)

class CommentServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImplTest.class);

    @Mock
    private CommentRepository commentRepositoryMock;

    @Mock
    private PostRepository postRepositoryMock;

    @Mock(name="modelMapper")
    private ModelMapper mockedModelMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    @InjectMocks
    private PostServiceImpl postService;

    private PostDto postDto;
    private Post post;
    private CommentDto commentDto;
    private Comment comment;

    @BeforeAll
    static void beforeAll() {
        logger.info("START test");
    }

    @BeforeEach
    void setUp() {
        logger.info("set up Comment, Post for each test");

        this.comment = new Comment(1L, "test", "test@gamil.com", "test body");
        this.commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setName("testi");
        commentDto.setEmail("test@gmail.com");
        commentDto.setBody("test body");

        this.post= new Post(1L, "xiao ruishi", "wanqu", "wanqu xiao ruishi",
                LocalDateTime.now(), LocalDateTime.now());
        this.postDto = new PostDto();
        postDto.setId(1L);
        postDto.setTitle("xiao ruishi");
        postDto.setContent("wanqu xiao ruishi");
        postDto.setDescription("wanqu");
    }

    @Test
    public void testCreateCommentWithMockedModelMapper() {
        // Define modelMapper's two different behaviors (different converting sources and targets)
        // Stubbing
        Mockito.when(postRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(post));
        Mockito.when(mockedModelMapper.map(commentDto, Comment.class)).thenReturn(comment);
        Mockito.when(mockedModelMapper.map(comment, CommentDto.class)).thenReturn(commentDto);
        Mockito.when(commentRepositoryMock.save(ArgumentMatchers.any())).thenReturn(comment);

        // Invoke method to be tested
        CommentDto commentResponse = commentService.createComment(1L, commentDto);

        // assertions
        Assertions.assertEquals(commentDto.getName(), commentResponse.getName());
        Assertions.assertEquals(commentDto.getEmail(), commentResponse.getEmail());
        Assertions.assertEquals(commentDto.getBody(), commentResponse.getBody());
        Mockito.verify(commentRepositoryMock, Mockito.times(1)).save(comment);
    }

    @Test
    public void testGetAllComments() {
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);

        // define the behaviors
        Mockito.when(mockedModelMapper.map(ArgumentMatchers.any(Comment.class), ArgumentMatchers.eq(CommentDto.class))).thenReturn(commentDto);

        Mockito.when(commentRepositoryMock.findByPostId(1L))
                .thenReturn(comments);

        // execute
        List<CommentDto> commentDtos = commentService.getCommentsByPostId(1L);

        // assertions
        Assertions.assertNotNull(commentDtos);
        Assertions.assertEquals(1, commentDtos.size());
        CommentDto commentResponse = commentDtos.get(0);
        Assertions.assertEquals(commentDto.getName(), commentResponse.getName());
        Assertions.assertEquals(commentDto.getEmail(), commentResponse.getEmail());
        Assertions.assertEquals(commentDto.getBody(), commentResponse.getBody());
    }

    @Test
    public void testGetCommentByID() {
        // define the behaviors
        Set<Comment> comments = new HashSet<>();
        comments.add(comment);
        post.setComments(comments);
        comment.setPost(post);

        Mockito.when(commentRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(comment));

        Mockito.when(postRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(post));
        Mockito.when(mockedModelMapper.map(comment, CommentDto.class)).thenReturn(commentDto);

        // execute
        CommentDto commentResponse = commentService.getCommentById(1L, 1L);

        // assertions
        Assertions.assertEquals(commentDto.getName(), commentResponse.getName());
        Assertions.assertEquals(commentDto.getEmail(), commentResponse.getEmail());
        Assertions.assertEquals(commentDto.getBody(), commentResponse.getBody());
    }

    @Test
    public void testUpdateComment() {
        Set<Comment> comments = new HashSet<>();
        comments.add(comment);
        post.setComments(comments);
        comment.setPost(post);
        String body = "UPDATED - " + comment.getBody();
        commentDto.setBody(body);

        // deep copy
        Comment updatedComment = new Comment();
        updatedComment.setId(comment.getId());
        updatedComment.setName(comment.getName());
        updatedComment.setEmail(comment.getEmail());
        updatedComment.setBody(comment.getBody());

        // define the behaviors
        Mockito.when(commentRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(comment));
        Mockito.when(postRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(post));
        Mockito.when(commentRepositoryMock.save(ArgumentMatchers.any(Comment.class)))
                .thenReturn(updatedComment);

        Mockito.when(mockedModelMapper.map(ArgumentMatchers.any(Comment.class), ArgumentMatchers.eq(CommentDto.class))).thenReturn(commentDto);
        // execute
        CommentDto commentResponse = commentService.updateComment(1L, 1L, commentDto);

        // assertions
        Assertions.assertNotNull(commentResponse);
        Assertions.assertEquals(commentDto.getName(), commentResponse.getName());
        Assertions.assertEquals(commentDto.getEmail(), commentResponse.getEmail());
        Assertions.assertEquals(commentDto.getBody(), commentResponse.getBody());
    }

    @Test
    public void testDeleteCommentById() {
        Set<Comment> comments = new HashSet<>();
        comments.add(comment);
        post.setComments(comments);
        comment.setPost(post);
        // define the behaviors
        Mockito.when(commentRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(comment));

        Mockito.doNothing().when(commentRepositoryMock).delete(ArgumentMatchers.any(Comment.class));
        Mockito.when(postRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(post));

        // execute
        commentService.deleteComment(1L, 1L);

        // verify
        // 验证 postRepositoryMock.delete() 被执行过一次
        Mockito.verify(commentRepositoryMock, Mockito.times(1)).delete(ArgumentMatchers.any(Comment.class));
    }
}