package com.viniciuspadovam.s3.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.viniciuspadovam.s3.constants.FileValidFormats;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidImageFormatException extends RuntimeException {
	
    public InvalidImageFormatException() {
        super("Invalid image format. Valid formats are " + FileValidFormats.validImageFormats.toString());
    }

}
