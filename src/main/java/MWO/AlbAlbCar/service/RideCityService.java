package MWO.AlbAlbCar.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import MWO.AlbAlbCar.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import MWO.AlbAlbCar.model.City;
import MWO.AlbAlbCar.model.Ride;
import MWO.AlbAlbCar.model.RideCity;
import MWO.AlbAlbCar.repository.RideCityRepository;

@Service
public class RideCityService {

	@Autowired 
	CityService cityService;
	
	@Autowired 
	RideService rideService;
	
	@Autowired
	RideCityRepository rideCityRepository;
	
	@Autowired
	RideUsersService rideUsersService;

	public Map<String, String> addStops(Ride ride, Iterator<JsonNode> stops, int priceAll, int assembly_place, int destination_place) {
		HashMap<String, String> result = new HashMap<String, String>();
		ArrayList<RideCity> stopsList= new ArrayList<RideCity>();
		HashSet<Integer> cities = new HashSet<Integer>(); 
		
		int previousDelay = 0; 
		int previousPrice = 0;
		String reason = "";
		cities.add(assembly_place);
		cities.add(destination_place);
		
		RideCity rideCityStart = new RideCity();
		City cityStart = cityService.getCityById(assembly_place); 
		rideCityStart.setRide(ride);
		rideCityStart.setDelay(0);
		rideCityStart.setPrice(0);
		rideCityStart.setCity(cityStart);
		rideCityStart.setPeopleInCar(0);
		stopsList.add(rideCityStart);
		
		while(stops.hasNext()) {		
			JsonNode stop = stops.next();
			int cityId = stop.findValue("cityId").asInt();
			int delay = stop.findValue("delay").asInt();
			int price = stop.findValue("price").asInt();
			
			if(cities.contains(cityId)) {
				reason = "2 same cities among stops";
				result.put("result","fail");
				result.put("reason", reason);
				return result;
			}
			
			if(previousDelay > delay) {
				reason = "It is something wrong with delays";
				result.put("result","fail");
				result.put("reason", reason);
				return result;
			}

			if(previousPrice > price || priceAll < price) {
				reason = "It is something wrong with prices";
				result.put("result","fail");
				result.put("reason", reason);
				return result;
			}
			
			City city = cityService.getCityById(cityId);
			
			RideCity rideCity = new RideCity();
			rideCity.setRide(ride);
			rideCity.setDelay(delay);
			rideCity.setPrice(price);
			rideCity.setCity(city);
			rideCity.setPeopleInCar(0);
			stopsList.add(rideCity);
			
			cities.add(cityId);
			previousDelay = delay;
			previousPrice = price;
		}
		
		RideCity rideCityStop = new RideCity();
		City cityStop = cityService.getCityById(destination_place); 
		rideCityStop.setRide(ride);
		rideCityStop.setDelay(stopsList.get(stopsList.size()-1).getDelay()+1);
		rideCityStop.setPrice(priceAll);
		rideCityStop.setCity(cityStop);
		rideCityStop.setPeopleInCar(0);
		stopsList.add(rideCityStop);
		
		for(RideCity stop : stopsList)
			rideCityRepository.save(stop);

		result.put("result","success");
		
		return result;
	}
	
	public List<RideCity> stopsBetweenAAndB(int rideId, int assembly_place, int destination_place) {
		RideCity assemblyCity = rideCityRepository.findyByRideIdAndCityId(rideId, assembly_place);
		int assemblyDelay = 0;
		assemblyDelay = assemblyCity.getDelay();
			
		RideCity destinationCity = rideCityRepository.findyByRideIdAndCityId(rideId, destination_place);
        int destinationDelay = 0;
		destinationDelay = destinationCity.getDelay();
		
		return rideCityRepository.getStopsBetweenDelays(rideId,assemblyDelay,destinationDelay);
	}
	
	public boolean isFreeSeatOnRideFromAToB(int rideId, int assembly_place, int destination_place) {
		int seatsInRide = rideService.getRideById(rideId).getSeats();
		
		List<RideCity> stopsList = stopsBetweenAAndB(rideId, assembly_place, destination_place);
		for (RideCity stop : stopsList) {
			if (stop.getPeopleInCar() < seatsInRide)
				continue;
			else
				return false;
		}
		return true;
	}
	
	public void save(RideCity rc) {
		rideCityRepository.save(rc);
	}

	public int computeFreeSeats(Ride ride, int assembly_place, int destination_place) {
		int seatsInRide = ride.getSeats();
		int maxBookedSeats = 0;
		List<RideCity> stopsList = stopsBetweenAAndB(ride.getRideId(), assembly_place, destination_place);
		for (RideCity stop : stopsList) {
			if (stop.getPeopleInCar() > maxBookedSeats) {
				maxBookedSeats = stop.getPeopleInCar();
			}
		}
		return seatsInRide - maxBookedSeats;
	}
}
