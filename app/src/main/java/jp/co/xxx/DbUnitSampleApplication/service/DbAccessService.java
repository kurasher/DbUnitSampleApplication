package jp.co.xxx.DbUnitSampleApplication.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import jp.co.xxx.DbUnitSampleApplication.entity.Book;

public interface DbAccessService {
  String getBookData() throws JsonProcessingException;

  void insertBookData(Book book);
}
