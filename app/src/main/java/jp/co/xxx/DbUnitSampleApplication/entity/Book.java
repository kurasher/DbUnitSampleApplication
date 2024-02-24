package jp.co.xxx.DbUnitSampleApplication.entity;

import lombok.Data;

@Data
public class Book {
  private Integer id;
  private String title;
  private String author;

//  public void setTitle(String title) {
//    this.title = title;
//  }
//  public void setAuthor(String author){
//    this.author = author;
//  }
}
