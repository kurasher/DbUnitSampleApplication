package jp.co.xxx.DbUnitSampleApplication.repository;

import jp.co.xxx.DbUnitSampleApplication.entity.Book;

import java.sql.Timestamp;
import java.util.List;

public interface BookRepository {
  Timestamp getCurrentTimeStamp();
  List<Book> getBookData();

  void insertBookData(Book book);

  void updateBookData(Book book);

  void deleteBookData(int id);
}
