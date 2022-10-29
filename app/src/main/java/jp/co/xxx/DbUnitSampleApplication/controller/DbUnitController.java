package jp.co.xxx.DbUnitSampleApplication.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class DbUnitController {

  @GetMapping("/status")
  public String returnStatus(){
    return "{'status' : 'ok'}";
  }
}
