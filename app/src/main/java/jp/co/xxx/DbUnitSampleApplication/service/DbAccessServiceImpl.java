package jp.co.xxx.DbUnitSampleApplication.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.xxx.DbUnitSampleApplication.entity.Book;
import jp.co.xxx.DbUnitSampleApplication.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class DbAccessServiceImpl implements DbAccessService {
  private final BookRepository bookRepository;

  public DbAccessServiceImpl(BookRepository bookRepository){
    this.bookRepository = bookRepository;
  }

  @Override
  public Timestamp getCurrentTimeStamp() {
    return bookRepository.getCurrentTimeStamp();
  }

  @Override
  public String getBookData() throws JsonProcessingException {
    List<Book> bookList = bookRepository.getBookData();
    ObjectMapper objectMapper = new ObjectMapper();
    String bookListJson = objectMapper.writeValueAsString(bookList);

    return bookListJson;
  }

  @Override
  public void insertBookData(Book book) {
    bookRepository.insertBookData(book);
  }

  @Override
  public void updateBookData(Book book){
    bookRepository.updateBookData(book);
  }

  public void deleteBookData(int id){
    bookRepository.deleteBookData(id);
  }
}
