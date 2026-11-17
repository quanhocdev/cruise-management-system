package com.project.tour.service.scheduler;

import com.project.tour.dto.route.RouteRequest;
import com.project.tour.dto.route.RouteResponse;
import com.project.tour.model.admin.Port;
import com.project.tour.model.scheduler.Schedule;
import com.project.tour.model.scheduler.ScheduleStop;
import com.project.tour.model.scheduler.Tour;
import com.project.tour.model.scheduler.TourRoute;
import com.project.tour.model.scheduler.TourRouteLeg;
import com.project.tour.repository.scheduler.TourRepository;
import com.project.tour.repository.scheduler.TourRouteLegRepository;
import com.project.tour.repository.scheduler.TourRouteRepository;
import com.project.tour.repository.tour.schedule.ScheduleRepository;
import com.project.tour.repository.tour.schedule.ScheduleStopRepository;
import com.project.tour.service.route.RouteService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.tour.mapper.route.RouteRequestMapper;
import java.util.*;

@Service
public class TourApprovalRouteAsyncService {

    private final TourRepository tourRepository;
    private final TourRouteRepository tourRouteRepository;
    private final TourRouteLegRepository tourRouteLegRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleStopRepository scheduleStopRepository;
    private final RouteService routeService;
    private final RouteRequestMapper routeRequestMapper;

    public TourApprovalRouteAsyncService(
            TourRepository tourRepository,
            TourRouteRepository tourRouteRepository,
            TourRouteLegRepository tourRouteLegRepository,
            ScheduleRepository scheduleRepository,
            ScheduleStopRepository scheduleStopRepository,
            RouteService routeService,
            RouteRequestMapper routeRequestMapper) {
        this.tourRepository = tourRepository;
        this.tourRouteRepository = tourRouteRepository;
        this.tourRouteLegRepository = tourRouteLegRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleStopRepository = scheduleStopRepository;
        this.routeService = routeService;
        this.routeRequestMapper = routeRequestMapper;
    }

    // @Async
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

            // Xóa các chặng cũ nếu có (đề phòng trường hợp tính toán lại)
            tourRouteLegRepository.deleteByTourRoute_Id(tourRoute.getId());

            // 1. Gom mảng các cảng theo thứ tự: Departure -> ScheduleStops -> Destination
            List<Port> orderedPorts = new ArrayList<>();
            orderedPorts.add(departurePort);

            List<Schedule> schedules = scheduleRepository.findAllByTour_IdOrderByDayNumberAsc(tourId);
            if (schedules != null && !schedules.isEmpty()) {
                List<UUID> scheduleIds = schedules.stream().map(Schedule::getId).toList();
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

            orderedPorts.add(destinationPort);

            // 2. Tính toán từng chặng kề nhau, lưu bảng phụ và gộp tọa độ tổng quan
            List<List<List<Double>>> allCoordinates = new ArrayList<>();
            double totalDistanceKm = 0.0;
            int legOrder = 1;

            for (int i = 0; i < orderedPorts.size() - 1; i++) {
                Port from = orderedPorts.get(i);
                Port to = orderedPorts.get(i + 1);

                if (Objects.equals(from.getId(), to.getId())) {
                    continue;
                }
                if (from.getLongitude() == null || from.getLatitude() == null
                        || to.getLongitude() == null || to.getLatitude() == null) {
                    continue;
                }

                RouteRequest req = routeRequestMapper.fromPorts(from, to);

                RouteResponse legResponse = routeService.calculateRoute(req);
                if (legResponse != null) {
                    // 1. Lấy trực tiếp từ Response của RouteService
                    double legDistance = legResponse.getDistanceKm() != null ? legResponse.getDistanceKm() : 0.0;
                    Map<String, Object> legGeoJson = legResponse.getGeoJson();

                    // 2. Nếu bằng 0, bóc tách trực tiếp từ attributes/properties do SeaRoute trả về
                    // trong GeoJSON
                    if (legDistance == 0.0 && legGeoJson != null && legGeoJson.get("properties") instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> props = (Map<String, Object>) legGeoJson.get("properties");

                        // Thử tìm các tên trường phổ biến mà SeaRoute hay gạt vào properties
                        Object distProp = props.get("distKM");
                        if (distProp == null)
                            distProp = props.get("distance");
                        if (distProp == null)
                            distProp = props.get("DIST");

                        if (distProp != null) {
                            try {
                                legDistance = Double.parseDouble(distProp.toString());
                            } catch (Exception ignored) {
                            }
                        }
                    }

                    totalDistanceKm += legDistance;

                    // Lưu từng chặng lẻ vào bảng phụ `tour_route_legs`
                    if (legGeoJson != null) {
                        TourRouteLeg routeLeg = new TourRouteLeg();
                        routeLeg.setTourRoute(tourRoute);
                        routeLeg.setLegOrder(legOrder++);
                        routeLeg.setFromPort(from);
                        routeLeg.setToPort(to);
                        routeLeg.setRouteGeoJson(legGeoJson);
                        routeLeg.setDistanceKm(legDistance);
                        tourRouteLegRepository.save(routeLeg);
                    }

                    // Trích xuất tọa độ để gom vào MultiLineString tổng quan
                    if (legGeoJson != null && legGeoJson.get("geometry") instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> geom = (Map<String, Object>) legGeoJson.get("geometry");
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

            // 3. Đóng gói kết quả tổng quan thành GeoJSON MultiLineString hoàn chỉnh
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

            // 4. Lưu thông tin tổng quan vào bảng chính `tour_routes`
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