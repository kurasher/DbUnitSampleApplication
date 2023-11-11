package jp.co.xxx.DbUnitSampleApplication.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jp.co.xxx.DbUnitSampleApplication.service.DbAccessService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DbUnitController {
  private final DbAccessService dbAccessService;

  public DbUnitController(DbAccessService dbAccessService){
    this.dbAccessService = dbAccessService;
  }

  @GetMapping("/status")
  public String returnStatus() throws JsonProcessingException {
    String bookListJson = dbAccessService.getBookData();
    return "{\"status\" : \"ok\", \"bookList\" : " + bookListJson + "}";
  }

  @GetMapping("/get_all")
  public String returnAll() throws JsonProcessingException{
    String bookListJson = dbAccessService.getBookData();
    
    return bookListJson;
  }

  @PostMapping("/insert_data")
  public String insertData() throws JsonProcessingException{
    String bookListJson = dbAccessService.getBookData();

    return bookListJson;
  }
}
