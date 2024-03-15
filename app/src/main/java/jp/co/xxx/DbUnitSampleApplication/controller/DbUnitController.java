package jp.co.xxx.DbUnitSampleApplication.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jp.co.xxx.DbUnitSampleApplication.entity.Book;
import jp.co.xxx.DbUnitSampleApplication.service.DbAccessService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping(value="/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DbUnitController {
  private final DbAccessService dbAccessService;

  public DbUnitController(DbAccessService dbAccessService){
    this.dbAccessService = dbAccessService;
  }

  @GetMapping("/status")
  public String returnStatus() throws JsonProcessingException {
    Timestamp today = dbAccessService.getCurrentTimeStamp();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    return "{\"status\" : \"ok\", " + "\"current_time\" : \"" + sdf.format(today) + "\"}";
  }

  @GetMapping("/get_all")
  public String returnAll() throws JsonProcessingException{
    String bookListJson = dbAccessService.getBookData();
    
    return bookListJson;
  }

  @PostMapping("/insert_bookdata")
  public void insertData(@RequestBody Book book) throws JsonProcessingException{
    dbAccessService.insertBookData(book);
  }

  @PutMapping("/update_bookdata")
  public void updateData(@RequestBody Book book){
    dbAccessService.updateBookData(book);
  }

  @DeleteMapping("/delete_bookdata/{id}")
  public void deleteData(@PathVariable int id){
    dbAccessService.deleteBookData(id);
  }
}
