package de.hska.iwi.vislab.lab1.example.ws;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface FibonacciServiceIntf {
	int getFibonacci(@WebParam(name = "number") int number);
}