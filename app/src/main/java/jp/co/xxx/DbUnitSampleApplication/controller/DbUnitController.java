package jp.co.xxx.DbUnitSampleApplication.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.deser.key.LocalDateKeyDeserializer;
import jp.co.xxx.DbUnitSampleApplication.entity.Book;
import jp.co.xxx.DbUnitSampleApplication.service.DbAccessService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

@RestController
@RequestMapping(value="/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DbUnitController {
  private final DbAccessService dbAccessService;

  public DbUnitController(DbAccessService dbAccessService){
    this.dbAccessService = dbAccessService;
  }

  @GetMapping("/status")
  public String returnStatus() throws JsonProcessingException {
//    String bookListJson = dbAccessService.getBookData();
    Timestamp today = dbAccessService.getCurrentTimeStamp();
//    System.out.println(today);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    return "{\"status\" : \"ok\", " + "\"current_time\" : \"" + sdf.format(today) + "\"}";
  }

  @GetMapping("/get_all")
  public String returnAll() throws JsonProcessingException{
    String bookListJson = dbAccessService.getBookData();
    
    return bookListJson;
  }

  @PostMapping("/insert_bookdata")
  public String insertData(@RequestBody Book book) throws JsonProcessingException{
//    dbAccessService.insertBookData(book);
    String bookListJson = dbAccessService.getBookData();

    return bookListJson;
  }
}
