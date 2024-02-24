package jp.co.xxx.DbUnitSampleApplication.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import jp.co.xxx.DbUnitSampleApplication.entity.Book;

import java.sql.Timestamp;

public interface DbAccessService {
  Timestamp getCurrentTimeStamp();
  String getBookData() throws JsonProcessingException;

  void insertBookData(Book book);
}
