package jp.co.xxx.DbUnitSampleApplication.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.xxx.DbUnitSampleApplication.entity.Book;
import jp.co.xxx.DbUnitSampleApplication.repository.BookRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
public class DbUnitController {
  private final BookRepository bookRepository;

  public DbUnitController(BookRepository bookRepository){
    this.bookRepository = bookRepository;
  }

  @GetMapping("/status")
  public String returnStatus() throws JsonProcessingException {
    List<Book> bookList = bookRepository.getBookData();
    ObjectMapper objectMapper = new ObjectMapper();
    String bookListJson = objectMapper.writeValueAsString(bookList);
    return "{\"status\" : \"ok\", \"bookList\" : " + bookListJson + "}";
  }
}
