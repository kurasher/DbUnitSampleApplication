package jp.co.xxx.DbUnitSampleApplication.service;


import com.fasterxml.jackson.core.JsonProcessingException;

public interface DbAccessService {
  String getBookData() throws JsonProcessingException;
}
