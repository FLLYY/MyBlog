package com.rawchen;

import com.rawchen.domain.*;
import com.rawchen.service.*;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.List;

public class ServiceTest {
	@Test
	public void run1() {
		ApplicationContext ac = new
				ClassPathXmlApplicationContext("classpath:spring-context.xml");
		ContentService contentsService = (ContentService) ac.getBean("contentService");
		Content content = contentsService.selectByPrimaryKey(1);
		System.out.println(content);
	}

	@Test
	public void run2() {
		ApplicationContext ac = new
				ClassPathXmlApplicationContext("classpath:spring-context.xml");
		ContentService contentsService = (ContentService) ac.getBean("contentService");
		int n = contentsService.selectNumberOfArticles();
		System.out.println(n);
	}

	@Test
	public void run3() {
		ApplicationContext ac = new
				ClassPathXmlApplicationContext("classpath:spring-context.xml");
		ContentService contentsService = (ContentService) ac.getBean("contentService");
		List<Content> contents = contentsService.selectRecommendContent();
		for (Content c : contents) {
			System.out.println(c);
		}
	}

	@Test
	public void run4() {
		ApplicationContext ac = new
				ClassPathXmlApplicationContext("classpath:spring-context.xml");
		OptionsService optionsService = (OptionsService) ac.getBean("optionsService");
		String value = optionsService.selectValueByName("qq_link");
		System.out.println(value);
	}

	@Test
	public void run5() {
		ApplicationContext ac = new
				ClassPathXmlApplicationContext("classpath:spring-context.xml");
		CategoryService categoryService = (CategoryService) ac.getBean("categoryService");
		List<Category> categories = categoryService.selectAllCategoryWithCount();
		for (Category c:categories) {
			System.out.println(c);
		}
	}

	@Test
	public void run6() {
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring-context.xml");
		TagService tagService = (TagService) ac.getBean("tagService");
		List<Tag> tags = tagService.selectAllTag();
		for (Tag t:tags) {
			System.out.println(t);
		}
	}

	@Test
	public void run7() {
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring-context.xml");
		TagService tagService = (TagService) ac.getBean("tagService");
		List<Tag> tags = tagService.fuzzyQueryTag("i");
		for (Tag t:tags) {
			System.out.println(t);
		}
	}

	@Test
	public void run8() {
		ApplicationContext ac = new
				ClassPathXmlApplicationContext("classpath:spring-context.xml");
		CategoryService categoryService = (CategoryService) ac.getBean("categoryService");
		int c = categoryService.selectCategoryBySlug("se");
		System.out.println(c);
	}

	@Test
	public void run9() {
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring-context.xml");
		TagService tagService = (TagService) ac.getBean("tagService");
		int a = tagService.findTagIdByName("IO");
		System.out.println(a);
	}

	@Test
	public void run10() {
		ApplicationContext ac = new
				ClassPathXmlApplicationContext("classpath:spring-context.xml");
		ContentService contentsService = (ContentService) ac.getBean("contentService");
//		Content content = contentsService.findContentBySlugName("ioc");
//		System.out.println(content);
		System.out.println("hanhan");
		List<Content> content1 = contentsService.selectContentWithCategory(2);
		System.out.println(content1.size());

// 遍历输出每个 Content 对象的属性
		for (Content content : content1) {
			System.out.println("Content{");
			System.out.println("    cid=" + content.getCid());
			System.out.println("    cgid=" + content.getCgid());
			System.out.println("    categoryName=" + content.getCategoryName());
			System.out.println("    categorySlug=" + content.getCategorySlug());
			System.out.println("    CommentCount=" + content.getCommentCount());
			System.out.println("    title=" + content.getTitle());
			System.out.println("    slug=" + content.getSlug());
			System.out.println("    createdTime=" + content.getCreatedTime());
			System.out.println("    modifiedTime=" + content.getModifiedTime());
			System.out.println("    contentOrder=" + content.getContentOrder());
			System.out.println("    authorId=" + content.getAuthorId());
			System.out.println("    contentType=" + content.getContentType());
			System.out.println("    contentStatus=" + content.getContentStatus());
			System.out.println("    password=" + content.getPassword());
			System.out.println("    views=" + content.getViews());
			System.out.println("    tagList=" + content.getTagList());
		//	System.out.println("    contentText=" + content.getContentText());
			System.out.println("    thumb=" + content.getThumb());
			System.out.println("    description=" + content.getDescription());
			System.out.println("}");
		}

		System.out.println("hanhan");

	}

	@Test
	public void run11() {
		ApplicationContext ac = new
				ClassPathXmlApplicationContext("classpath:spring-context.xml");
		UserService userService = (UserService) ac.getBean("userService");
		User t = new User();
		t.setName("rawchen");
		t.setPassword("rawchen");
		User user = userService.selectUserByNameAndPassword(t);
		System.out.println(user);
	}

	@Test
	public void run12() {
		ApplicationContext ac = new
				ClassPathXmlApplicationContext("classpath:spring-context.xml");
		CommentService commentService = (CommentService) ac.getBean("commentService");
		List<Comment> comments = commentService.selectCommentListByContentId(63);
		for (Comment comment : comments) {
			System.out.println(comment);
		}
	}

	@Test
	public void run13() {
		ApplicationContext ac = new
				ClassPathXmlApplicationContext("classpath:spring-context.xml");
		LogService logService = (LogService) ac.getBean("logService");
		List<Integer> ys = logService.selectYesterdayPvUvIndexGuestbook();
		for (Integer y : ys) {
			System.out.println(y);
			System.out.println("pf");
		}
	}

//	@Test
//	public void run15() {
//		ApplicationContext ac = new
//				ClassPathXmlApplicationContext("classpath:spring-context.xml");
//		FileService fileService = (FileService) ac.getBean("fileService");
//		List<MyFile> files = fileService.selectAllFile();
//		for (File f : files) {
//			System.out.println(f);
//		}
//	}

	@Test
	public void run16() {
		ApplicationContext ac = new
				ClassPathXmlApplicationContext("classpath:spring-context.xml");
		ContentService contentsService = (ContentService) ac.getBean("contentService");
		List<Content> contents = contentsService.selectAllContent();
		for (Content c : contents) {
			System.out.println(c);
			System.out.println(c.getTList());
		}
	}

	@Test
	public void run17() {
		ApplicationContext ac = new
				ClassPathXmlApplicationContext("classpath:spring-context.xml");
		LogService logService = (LogService) ac.getBean("logService");
		List<Integer> ys = logService.selectSevenDaysUv();
		for (Integer y : ys) {
			System.out.println(y);
		}
	}

}
