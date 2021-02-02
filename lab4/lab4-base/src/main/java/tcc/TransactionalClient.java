package tcc;

import tcc.flight.FlightReservationDoc;
import tcc.hotel.HotelReservationDoc;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.GregorianCalendar;


public class TransactionalClient {

    public boolean FAIL_HOTEL = false;
    public boolean FAIL_FLIGHT_CONFORMATION = false;
    public int RETRIES = 10;

    private final WebTarget target;
    private final Client client;
    private final GregorianCalendar tomorrow;
    private final WebTarget flightTarget;
    private final WebTarget hotelTarget;

    private FlightReservationDoc docFlight;
    private HotelReservationDoc docHotel;

    private boolean hotelConfirmed = false;
    private boolean flightConfirmed = false;
    private boolean hotelCancelation = false;
    private String city, name, to, Airline, Hotel;

    public TransactionalClient(String name, String city, String to, String Airline, String Hotel) {
        this.client = ClientBuilder.newClient();
        this.target = this.client.target(TestServer.BASE_URI);
        this.tomorrow = new GregorianCalendar();
        this.tomorrow.setTime(new Date());
        this.tomorrow.add(GregorianCalendar.DAY_OF_YEAR, 1);

        this.flightTarget = target.path("flight");
        this.hotelTarget = target.path("hotel");
        this.name = name;
        this.city = city;
        this.to = to;
        this.Airline = Airline;
        this.Hotel = Hotel;
    }

    public Response bookFlight(String name, String city, String to, String Airline) {
        // book hotel
        this.docFlight = new FlightReservationDoc();
        docFlight.setName(name);
        docFlight.setFrom(city);
        docFlight.setTo(to);
        docFlight.setAirline(Airline);
        docFlight.setDate(tomorrow.getTimeInMillis());

        Response responseFlight = this.flightTarget.request().accept(MediaType.APPLICATION_XML)
                .post(Entity.xml(docFlight));
        return responseFlight;
    }

    public Response bookHotel(String name, String Hotel) {
        this.docHotel = new HotelReservationDoc();
        docHotel.setName(name);
        docHotel.setHotel(Hotel);
        docHotel.setDate(tomorrow.getTimeInMillis());

        Response responseHotel = this.hotelTarget.request().accept(MediaType.APPLICATION_XML)
                .post(Entity.xml(docHotel));

        return responseHotel;
    }

    public void start() {
        try {
            Response responseHotel = bookHotel(this.name, this.Hotel);
            Response responseFlight = bookFlight(this.name, this.city, this.to, this.Airline);

            HotelReservationDoc outputHotel = responseHotel.readEntity(HotelReservationDoc.class);
            FlightReservationDoc outputFlight = responseFlight.readEntity(FlightReservationDoc.class);

            if (responseFlight.getStatus() != 200) {
                System.out.println("Failed : HTTP error code : " + responseFlight.getStatus());
            } else {
                System.out.println("Output from Server: " + outputFlight);
                System.out.println();
                System.out.println("ReservedState: FLIGHT " + outputFlight.getName());
                System.out.println();
            }

            if (responseHotel.getStatus() != 200) {
                System.out.println("Failed : HTTP error code : " + responseHotel.getStatus());
            } else {

                System.out.println();
                System.out.println("Output from Server: " + outputHotel);
                System.out.println("ReservedState: HOTEL " + outputHotel.getName());
                System.out.println();
            }
            // Hotel and FLight Reserved
            if (responseHotel.getStatus() == 200 && responseFlight.getStatus() == 200 && !FAIL_HOTEL) {
                ///
                /// Confirming Hotel
                ///
                // Try confirming the Hotel for the TOTAL number of RETRIES
                for (int i = 0; i < RETRIES; i++) {
                    WebTarget webTargetHotelConformation = this.client.target(outputHotel.getUrl());
                    Response responseHotelConformation = webTargetHotelConformation.request()
                            .accept(MediaType.TEXT_PLAIN).put(Entity.xml(docHotel));

                    if (responseHotelConformation.getStatus() == 200) {
                        // HOTEL was Confirmed, so STOP the Execution
                        System.out.println("CONFIRMED: HOTEL " + responseHotelConformation.getStatus());
                        System.out.println();
                        this.hotelConfirmed = true;
                        break;
                    }
                }
                // next Confirm Flight
                if (this.hotelConfirmed) {
                    ///
                    /// Confirming Flight
                    ///
                    for (int i = 0; i < RETRIES; i++) {
                        WebTarget webTargetFlightConformation = client.target(outputFlight.getUrl());
                        Response responseFlightConformation = webTargetFlightConformation.request()
                                .accept(MediaType.TEXT_PLAIN).put(Entity.xml(docFlight));

                        if (responseFlightConformation.getStatus() == 200) {
                            System.out.println("CONFIRMED: Flight " + responseFlightConformation.getStatus());
                            System.out.println();
                            if (!FAIL_FLIGHT_CONFORMATION) flightConfirmed = true;
                            break;
                        }
                    }

                    // if flight reservation could not be confirmed, rollback hotel confirmation
                    if (!this.flightConfirmed) {
                        System.out.println("ROLLBACK: Starting Flight Rollback ");
                        System.out.println();

                        for (int i = 0; i < RETRIES; i++) {
                            WebTarget webTargetHotelRollback = client.target(outputHotel.getUrl());
                            Response responseHotelRollback = webTargetHotelRollback.request()
                                    .accept(MediaType.TEXT_PLAIN).delete();

                            if (responseHotelRollback.getStatus() == 200) {
                                this.hotelCancelation = true;
                                break;
                            }
                        }

                        if (hotelCancelation) {
                            System.out.println("ROLLBACK: Hotel was rolled back");
                            System.out.println();
                        } else {
                            System.out.println("ROLLBACK: Couldnt Rollback the Hotel");
                            System.out.println();
                        }
                    }
                } else {
                    System.out.println("Output from Server: Hotel Conformation not successful");
                }
            } else if (responseHotel.getStatus() != 200 && responseFlight.getStatus() == 200 || FAIL_HOTEL) {
                ///
                /// Couldnt Reserve Hotel so resetting Flight
                ///
                System.out.println("ERROR: Hotel reservation unsuccessful");
                System.out.println();
                System.out.println("ROLLBACK: Rolling Back Flight");
                System.out.println();
                WebTarget webTargetFlightRollback = client.target(outputFlight.getUrl());
                Response responseFlightRollback = webTargetFlightRollback.request().accept(MediaType.TEXT_PLAIN)
                        .delete();
                if (responseFlightRollback.getStatus() == 200) {
                    System.out.println("ROLLBACK: Rollback of flight reservation successful");
                    System.out.println();

                } else {
                    System.out.println("ERROR: Rollback of flight reservation unsuccessful, waiting for timeout.");
                    System.out.println();

                }
            }
            // flight reservation not successful, but
            else if (responseFlight.getStatus() != 200 && responseHotel.getStatus() == 200) {
                ///
                /// Couldnt Reserve Flight so resetting Hotel
               ///
                System.out.println("ERROR: Flight reservation unsuccessful, rolling back hotel reservation.");
                System.out.println();
                System.out.println("ROLLBACK: Rolling Back hotel Reservation");
                System.out.println();
                WebTarget webTargetHotelRollback = client.target(outputHotel.getUrl());
                Response responseHotelRollback = webTargetHotelRollback.request().accept(MediaType.TEXT_PLAIN).delete();

                if (responseHotelRollback.getStatus() == 200) {
                    System.out.println("ROLLBACK: Rollback of hotel reservation successful");
                    System.out.println();

                } else {
                    System.out.println("ERROR: Rollback of hotel reservation unsuccessful, waiting for timeout.");
                    System.out.println();

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        TransactionalClient client = new TransactionalClient("Julian", "Asperg", "Karlsruhe", "HSKAir", "Li137");
        client.start();

        TransactionalClient client2 = new TransactionalClient("Julian1", "Asperg1", "Karlsruhe1", "HSKAir1", "Li1371" );
        client2.FAIL_HOTEL = true;
        client2.start();


    }
}