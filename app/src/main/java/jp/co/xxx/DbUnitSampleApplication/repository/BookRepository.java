package jp.co.xxx.DbUnitSampleApplication.repository;

import jp.co.xxx.DbUnitSampleApplication.entity.Book;

import java.util.List;

public interface BookRepository {
  List<Book> getBookData();
}
