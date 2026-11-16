package com.project.tour.service.scheduler;

import com.project.tour.dto.route.RouteRequest;
import com.project.tour.dto.route.RouteResponse;
import com.project.tour.model.admin.Port;
import com.project.tour.model.scheduler.Schedule;
import com.project.tour.model.scheduler.ScheduleStop;
import com.project.tour.model.scheduler.Tour;
import com.project.tour.model.scheduler.TourRoute;
import com.project.tour.repository.scheduler.TourRepository;
import com.project.tour.repository.scheduler.TourRouteRepository;
import com.project.tour.repository.tour.schedule.ScheduleRepository;
import com.project.tour.repository.tour.schedule.ScheduleStopRepository;
import com.project.tour.service.route.RouteService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TourApprovalRouteAsyncService {

    private final TourRepository tourRepository;
    private final TourRouteRepository tourRouteRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleStopRepository scheduleStopRepository;
    private final RouteService routeService;

    public TourApprovalRouteAsyncService(
            TourRepository tourRepository,
            TourRouteRepository tourRouteRepository,
            ScheduleRepository scheduleRepository,
            ScheduleStopRepository scheduleStopRepository,
            RouteService routeService) {
        this.tourRepository = tourRepository;
        this.tourRouteRepository = tourRouteRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleStopRepository = scheduleStopRepository;
        this.routeService = routeService;
    }

    @Async
    @Transactional
    public void calculateAndSaveRouteForTour(UUID tourId) {
        try {
            Tour tour = tourRepository.findById(tourId).orElse(null);
            if (tour == null || tour.getTourRoute() == null) {
                return;
            }

            TourRoute tourRoute = tour.getTourRoute();
            Port departurePort = tourRoute.getDeparturePort();
            Port destinationPort = tourRoute.getDestinationPort();

            if (departurePort == null || destinationPort == null) {
                return;
            }

            // 1. Gom mảng các cảng theo thứ tự: Departure -> ScheduleStops -> Destination
            List<Port> orderedPorts = new ArrayList<>();
            orderedPorts.add(departurePort);

            // Lấy danh sách Schedule thuộc tour này theo thứ tự dayNumber tăng dần
            List<Schedule> schedules = scheduleRepository.findAllByTour_IdOrderByDayNumberAsc(tourId);
            if (schedules != null && !schedules.isEmpty()) {
                List<UUID> scheduleIds = schedules.stream().map(Schedule::getId).toList();

                // Lấy tất cả các điểm dừng thuộc các schedule trên, đã sắp xếp theo ngày và
                // stopOrder
                List<ScheduleStop> stops = scheduleStopRepository
                        .findAllBySchedule_IdInOrderBySchedule_DayNumberAscStopOrderAsc(scheduleIds);
                if (stops != null) {
                    for (ScheduleStop stop : stops) {
                        if (stop.getPort() != null) {
                            orderedPorts.add(stop.getPort());
                        }
                    }
                }
            }

            // Chốt hạ bằng cảng đến của tour
            orderedPorts.add(destinationPort);

            // 2. Tính toán từng chặng kề nhau và gộp tọa độ
            List<List<List<Double>>> allCoordinates = new ArrayList<>();
            double totalDistanceKm = 0.0;

            for (int i = 0; i < orderedPorts.size() - 1; i++) {
                Port from = orderedPorts.get(i);
                Port to = orderedPorts.get(i + 1);

                if (Objects.equals(from.getId(), to.getId())) {
                    continue;
                }

                RouteRequest req = new RouteRequest();
                req.setDepartureLongitude(from.getLongitude());
                req.setDepartureLatitude(from.getLatitude());
                req.setDestinationLongitude(to.getLongitude());
                req.setDestinationLatitude(to.getLatitude());

                RouteResponse legResponse = routeService.calculateRoute(req);
                if (legResponse != null) {
                    if (legResponse.getDistanceKm() != null) {
                        totalDistanceKm += legResponse.getDistanceKm();
                    }

                    Map<String, Object> geoJson = legResponse.getGeoJson();
                    if (geoJson != null && geoJson.get("geometry") instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> geom = (Map<String, Object>) geoJson.get("geometry");
                        Object coordsObj = geom.get("coordinates");
                        String type = (String) geom.get("type");

                        if ("LineString".equals(type) && coordsObj instanceof List) {
                            @SuppressWarnings("unchecked")
                            List<List<Double>> lineCoords = (List<List<Double>>) coordsObj;
                            allCoordinates.add(lineCoords);
                        } else if ("MultiLineString".equals(type) && coordsObj instanceof List) {
                            @SuppressWarnings("unchecked")
                            List<List<List<Double>>> multiCoords = (List<List<List<Double>>>) coordsObj;
                            allCoordinates.addAll(multiCoords);
                        }
                    }
                }
            }

            // 3. Đóng gói kết quả thành GeoJSON MultiLineString hoàn chỉnh
            Map<String, Object> finalGeoJson = new LinkedHashMap<>();
            finalGeoJson.put("type", "Feature");

            Map<String, Object> properties = new LinkedHashMap<>();
            properties.put("distKM", totalDistanceKm);
            properties.put("legsCount", allCoordinates.size());
            finalGeoJson.put("properties", properties);

            Map<String, Object> geometry = new LinkedHashMap<>();
            geometry.put("type", "MultiLineString");
            geometry.put("coordinates", allCoordinates);
            finalGeoJson.put("geometry", geometry);

            // 4. Lưu lại vào TourRoute
            tourRoute.setRouteGeoJson(finalGeoJson);
            tourRoute.setDistanceKm(totalDistanceKm);

            tourRoute.setDistanceFromDepartureKm(0.0);
            tourRoute.setDistanceToDestinationKm(totalDistanceKm);

            tourRouteRepository.save(tourRoute);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}