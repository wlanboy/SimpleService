package com.wlanboy.demo.controller;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wlanboy.demo.model.HelloParameters;

import static org.springframework.hateoas.core.DummyInvocationUtils.methodOn;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
public class HelloController {

    private static final Logger logger = Logger
            .getLogger(HelloController.class.getCanonicalName());

    static AtomicInteger counter = new AtomicInteger(0);

    /**
     * http://127.0.0.1:8001/hello
     * @param name String
     * @return String template
     */
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public HttpEntity<HelloParameters> hello(
            @RequestParam(value = "name", defaultValue = "World") String name) {
                HelloParameters helloString = new HelloParameters(counter.incrementAndGet(),"Hello",name);
                helloString.add(linkTo(methodOn(HelloController.class).hello(name)).withSelfRel());

        logger.info("HelloParameters created.");
        return new ResponseEntity<HelloParameters>(helloString, HttpStatus.OK);
    }
    
    /**
     * http://127.0.0.1:8001/datetime
     * @return String template
     */
    @RequestMapping(value = "/datetime", method = RequestMethod.GET)
    public HttpEntity<String> datetime() {

        logger.info("DateTime created.");
        DateTime now = DateTime.now();
        return new ResponseEntity<String>(now.toLocalDateTime().toString(), HttpStatus.OK);
    }    
  
}
