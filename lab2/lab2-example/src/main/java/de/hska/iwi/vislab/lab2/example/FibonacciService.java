package de.hska.iwi.vislab.lab2.example;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("fibonacci")
public class FibonacciService {
	private static int fibonacciIndexNumber = 0;
	private static int fibonacciNumber = 0;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getFibonacciPlain() {
		return "" + fibonacciNumber + "";
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getFibonacciHTML() {
		return "<html><title>Fibonacci</title><body><h2>Fibonacci: " + fibonacciNumber + "</h2></body></html>";
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getFibonacciJSON() {
		return  "{\"fibonacci\": " + FibonacciService.fibonacciNumber + "}";
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public String increment() {
		FibonacciService.fibonacciIndexNumber++;
		FibonacciService.fibonacciNumber = this.getFibonacciForNumber(FibonacciService.fibonacciIndexNumber);
		return "success";
	}

	@DELETE
	@Produces(MediaType.TEXT_PLAIN)
	public String reset() {
		FibonacciService.fibonacciNumber = 0;
		FibonacciService.fibonacciIndexNumber = 0;
		return "success";
	}

	private int getFibonacciForNumber(int number) {
        if (number == 0) {
            return 0;
        } else if (number == 1) {
            return 1;
        } else {
            return  getFibonacciForNumber(number - 1) + getFibonacciForNumber(number - 2);
        }
    }
}
