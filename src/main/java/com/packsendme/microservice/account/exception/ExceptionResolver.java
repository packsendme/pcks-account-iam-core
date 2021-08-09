package com.packsendme.microservice.account.exception;

import java.io.IOException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

public class ExceptionResolver {
	
	/**
	  * Handles java.lang.Exception
	  */
	  @ExceptionHandler(Exception.class)
	  public ModelAndView unknownError(Exception e) {
	    ModelAndView errorPage = new ModelAndView("errorpage");
	    errorPage.addObject("exception", e);
	    return errorPage;
	  }
	 
	 /**
	  * Handles java.io.IOException
	  */
	  @ExceptionHandler(IOException.class)
	  public ModelAndView handleException(Exception e) {
	    ModelAndView errorPage = new ModelAndView("errorpage");
	    errorPage.addObject("exception", e);
	    return errorPage;
	  }

}
