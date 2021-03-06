package MWO.AlbAlbCar.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "rides_cities")
public class RideCity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int rideId;
	
	@ManyToOne
    @JoinColumn(name="ride_id", nullable=false)
	private Ride ride;
	
	@ManyToOne
    @JoinColumn(name="city_id", nullable=false)
	private City city;
	
	@Column(name = "price")
	private double price;
    
	@Column(name = "delay")
	private int delay;
	
	@Column(name = "people_in_car")
	private int peopleInCar;
    
    public RideCity() {}

	public int getRideId() {
		return rideId;
	}

	public void setRideId(int rideId) {
		this.rideId = rideId;
	}

	public Ride getRide() {
		return ride;
	}

	public void setRide(Ride ride) {
		this.ride = ride;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getPeopleInCar() {
		return peopleInCar;
	}

	public void setPeopleInCar(int peopleInCar) {
		this.peopleInCar = peopleInCar;
	}
	
	public void incrementPeopleInCar() {
		peopleInCar++;
	}
	
	public void decrementPeopleInCar() {
		peopleInCar--;
	}
	
}
