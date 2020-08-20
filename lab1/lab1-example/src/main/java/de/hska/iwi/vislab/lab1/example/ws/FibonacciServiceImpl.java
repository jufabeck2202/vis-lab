package de.hska.iwi.vislab.lab1.example.ws;

import javax.jws.WebService;

@WebService(endpointInterface = "de.hska.iwi.vislab.lab1.example.ws.FibonacciServiceIntf")
public class FibonacciServiceImpl implements FibonacciServiceIntf {
    @Override
	public int getFibonacci(int number) {
		if (number == 0) {
            return 0;
        } else if (number == 1) {
            return 1;
        } else {
            return  getFibonacci(number - 1) + getFibonacci(number - 2);
        }
    }
}