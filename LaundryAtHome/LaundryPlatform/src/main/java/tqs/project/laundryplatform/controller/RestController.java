package tqs.project.laundryplatform.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api/laundry")
public class RestController {

   @PostMapping("/make-order")
   public HttpStatus makeOrder(@RequestParam long userId,
                               @RequestBody JSONArray items){

       return HttpStatus.OK;
   }


}
