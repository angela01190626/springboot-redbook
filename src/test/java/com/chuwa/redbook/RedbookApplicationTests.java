package com.chuwa.redbook;

import com.chuwa.redbook.payload.PostDto;
import com.chuwa.redbook.service.impl.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

// Integration test
@SpringBootTest
class RedbookApplicationTests {

	@Autowired
	private PostServiceImpl postService;

	@Test
	void testGetAllPosts() {
		assertNotNull(postService.getAllPost());
	}

    @Test
	void testCreatePost() {
        PostDto postDto = new PostDto();
        postDto.setTitle("test");
        postDto.setContent("new test");
        postDto.setDescription("description");
        postService.createPost(postDto);

        assertNotNull(postService.getAllPost());
    }

}
