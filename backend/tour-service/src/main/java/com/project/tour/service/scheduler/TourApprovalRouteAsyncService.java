
package com.project.tour.service.scheduler;

import com.project.tour.dto.route.RouteRequest;
import com.project.tour.dto.route.RouteResponse;
import com.project.tour.mapper.route.RouteRequestMapper;
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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TourApprovalRouteAsyncService {

    private static final double EARTH_RADIUS_KM = 6371.0088;

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

            // Xóa các chặng cũ nếu có.
            tourRouteLegRepository.deleteByTourRoute_Id(tourRoute.getId());

            // 1. Gom các cảng theo thứ tự:
            // Departure -> ScheduleStops -> Destination.
            List<Port> orderedPorts = new ArrayList<>();
            orderedPorts.add(departurePort);

            List<Schedule> schedules = scheduleRepository.findAllByTour_IdOrderByDayNumberAsc(tourId);

            if (schedules != null && !schedules.isEmpty()) {
                List<UUID> scheduleIds = schedules.stream().map(Schedule::getId).toList();

                List<ScheduleStop> stops = scheduleStopRepository
                        .findAllBySchedule_IdInOrderBySchedule_DayNumberAscStopOrderAsc(
                                scheduleIds);

                if (stops != null) {
                    for (ScheduleStop stop : stops) {
                        if (stop.getPort() != null) {
                            orderedPorts.add(stop.getPort());
                        }
                    }
                }
            }

            orderedPorts.add(destinationPort);

            // 2. Tính từng chặng và tổng hợp tọa độ.
            List<List<List<Double>>> allCoordinates = new ArrayList<>();

            double totalDistanceKm = 0.0;

            int legOrder = 1;

            int geometryFallbackLegs = 0;
            int haversineFallbackLegs = 0;

            for (int i = 0; i < orderedPorts.size() - 1; i++) {

                Port from = orderedPorts.get(i);
                Port to = orderedPorts.get(i + 1);

                if (Objects.equals(from.getId(), to.getId())) {
                    continue;
                }

                if (from.getLongitude() == null
                        || from.getLatitude() == null
                        || to.getLongitude() == null
                        || to.getLatitude() == null) {
                    continue;
                }

                RouteRequest req = routeRequestMapper.fromPorts(from, to);

                RouteResponse legResponse = routeService.calculateRoute(req);

                // RouteService không trả response thì không tự tạo khoảng cách.
                if (legResponse == null) {
                    continue;
                }

                Map<String, Object> legGeoJson = legResponse.getGeoJson();

                // Ưu tiên 1: Khoảng cách do RouteService trả về.
                double legDistance = getValidDistance(legResponse.getDistanceKm());

                // Ưu tiên 2: Đọc khoảng cách từ properties của GeoJSON.
                if (legDistance <= 0.0 && legGeoJson != null) {
                    legDistance = getDistanceFromGeoJsonProperties(legGeoJson);
                }

                // Ưu tiên 3: Tính chiều dài theo geometry SeaRoute.
                // Cộng khoảng cách giữa các tọa độ liên tiếp trong tuyến.
                if (legDistance <= 0.0 && legGeoJson != null) {
                    Double geometryDistance = calculateGeometryDistanceKm(legGeoJson);

                    if (geometryDistance != null && geometryDistance > 0.0) {
                        legDistance = geometryDistance;
                        geometryFallbackLegs++;
                    }
                }

                // Ưu tiên 4: Haversine trực tiếp giữa hai cảng.
                // Đây là khoảng cách đường chim bay, chỉ dùng khi
                // không có khoảng cách và geometry hợp lệ từ SeaRoute.
                if (legDistance <= 0.0) {
                    legDistance = calculateHaversineKm(
                            from.getLatitude().doubleValue(),
                            from.getLongitude().doubleValue(),
                            to.getLatitude().doubleValue(),
                            to.getLongitude().doubleValue());

                    haversineFallbackLegs++;
                }

                totalDistanceKm += legDistance;

                // Lưu từng chặng vào bảng tour_route_legs.
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

                // Trích xuất tọa độ để gộp thành MultiLineString.
                if (legGeoJson != null
                        && legGeoJson.get("geometry") instanceof Map) {

                    @SuppressWarnings("unchecked")
                    Map<String, Object> geom = (Map<String, Object>) legGeoJson.get("geometry");

                    Object coordsObj = geom.get("coordinates");
                    String type = (String) geom.get("type");

                    if ("LineString".equals(type)
                            && coordsObj instanceof List) {

                        @SuppressWarnings("unchecked")
                        List<List<Double>> lineCoords = (List<List<Double>>) coordsObj;

                        allCoordinates.add(lineCoords);

                    } else if ("MultiLineString".equals(type)
                            && coordsObj instanceof List) {

                        @SuppressWarnings("unchecked")
                        List<List<List<Double>>> multiCoords = (List<List<List<Double>>>) coordsObj;

                        allCoordinates.addAll(multiCoords);
                    }
                }
            }

            // 3. Đóng gói GeoJSON tổng quan.
            Map<String, Object> finalGeoJson = new LinkedHashMap<>();

            finalGeoJson.put("type", "Feature");

            Map<String, Object> properties = new LinkedHashMap<>();

            properties.put("distKM", totalDistanceKm);
            properties.put("legsCount", allCoordinates.size());

            // Thống kê các chặng phải dùng phương án dự phòng.
            properties.put("distanceFallbackGeometryLegs", geometryFallbackLegs);
            properties.put("distanceFallbackHaversineLegs", haversineFallbackLegs);

            finalGeoJson.put("properties", properties);

            Map<String, Object> geometry = new LinkedHashMap<>();

            geometry.put("type", "MultiLineString");
            geometry.put("coordinates", allCoordinates);

            finalGeoJson.put("geometry", geometry);

            // 4. Lưu thông tin tổng quan vào tour_routes.
            tourRoute.setRouteGeoJson(finalGeoJson);
            tourRoute.setDistanceKm(totalDistanceKm);
            tourRoute.setDistanceFromDepartureKm(0.0);
            tourRoute.setDistanceToDestinationKm(totalDistanceKm);

            tourRouteRepository.save(tourRoute);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Kiểm tra khoảng cách có hợp lệ hay không.
     */
    private double getValidDistance(Number distance) {
        if (distance == null) {
            return 0.0;
        }

        double value = distance.doubleValue();

        if (!Double.isFinite(value) || value <= 0.0) {
            return 0.0;
        }

        return value;
    }

    /**
     * Đọc khoảng cách từ các thuộc tính phổ biến trong GeoJSON.
     */
    private double getDistanceFromGeoJsonProperties(
            Map<String, Object> geoJson) {

        Object propertiesObj = geoJson.get("properties");

        if (!(propertiesObj instanceof Map)) {
            return 0.0;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) propertiesObj;

        String[] distanceKeys = { "distKM", "distance", "DIST" };

        for (String key : distanceKeys) {
            Object value = properties.get(key);

            if (value instanceof Number) {
                double distance = getValidDistance((Number) value);

                if (distance > 0.0) {
                    return distance;
                }
            }

            if (value != null) {
                try {
                    double distance = getValidDistance(Double.parseDouble(value.toString()));

                    if (distance > 0.0) {
                        return distance;
                    }
                } catch (NumberFormatException ignored) {
                    // Thử key tiếp theo.
                }
            }
        }

        return 0.0;
    }

    /**
     * Tính chiều dài tuyến dựa trên geometry GeoJSON.
     *
     * Hỗ trợ:
     * - LineString
     * - MultiLineString
     *
     * Tính tổng khoảng cách giữa các tọa độ liên tiếp
     * bằng Haversine.
     *
     * Trả về null nếu geometry không hợp lệ.
     */
    private Double calculateGeometryDistanceKm(
            Map<String, Object> geoJson) {

        Object geometryObj = geoJson.get("geometry");

        if (!(geometryObj instanceof Map)) {
            return null;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> geometry = (Map<String, Object>) geometryObj;

        String type = (String) geometry.get("type");
        Object coordinatesObj = geometry.get("coordinates");

        if (!(coordinatesObj instanceof List)) {
            return null;
        }

        double totalDistance = 0.0;

        boolean hasValidSegment = false;

        if ("LineString".equals(type)) {

            Double distance = calculateLineStringDistanceKm((List<?>) coordinatesObj);

            if (distance != null) {
                totalDistance += distance;
                hasValidSegment = true;
            }

        } else if ("MultiLineString".equals(type)) {

            List<?> lines = (List<?>) coordinatesObj;

            for (Object lineObj : lines) {
                if (!(lineObj instanceof List)) {
                    continue;
                }

                Double distance = calculateLineStringDistanceKm((List<?>) lineObj);

                if (distance != null) {
                    totalDistance += distance;
                    hasValidSegment = true;
                }
            }
        }

        if (!hasValidSegment
                || !Double.isFinite(totalDistance)
                || totalDistance <= 0.0) {
            return null;
        }

        return totalDistance;
    }

    /**
     * Tính tổng chiều dài của một LineString.
     *
     * Mỗi tọa độ GeoJSON có thứ tự:
     * [longitude, latitude].
     */
    private Double calculateLineStringDistanceKm(List<?> coordinates) {

        if (coordinates == null || coordinates.size() < 2) {
            return null;
        }

        double totalDistance = 0.0;
        boolean hasValidSegment = false;

        for (int i = 0; i < coordinates.size() - 1; i++) {

            double[] point1 = extractCoordinate(coordinates.get(i));
            double[] point2 = extractCoordinate(coordinates.get(i + 1));

            if (point1 == null || point2 == null) {
                continue;
            }

            double distance = calculateHaversineKm(
                    point1[1],
                    point1[0],
                    point2[1],
                    point2[0]);

            if (distance > 0.0 && Double.isFinite(distance)) {
                totalDistance += distance;
                hasValidSegment = true;
            }
        }

        return hasValidSegment ? totalDistance : null;
    }

    /**
     * Đọc tọa độ GeoJSON dạng [longitude, latitude].
     */
    private double[] extractCoordinate(Object coordinateObj) {

        if (!(coordinateObj instanceof List)) {
            return null;
        }

        List<?> coordinate = (List<?>) coordinateObj;

        if (coordinate.size() < 2
                || !(coordinate.get(0) instanceof Number)
                || !(coordinate.get(1) instanceof Number)) {
            return null;
        }

        double longitude = ((Number) coordinate.get(0)).doubleValue();

        double latitude = ((Number) coordinate.get(1)).doubleValue();

        if (!Double.isFinite(longitude)
                || !Double.isFinite(latitude)
                || longitude < -180.0
                || longitude > 180.0
                || latitude < -90.0
                || latitude > 90.0) {
            return null;
        }

        return new double[] { longitude, latitude };
    }

    /**
     * Tính khoảng cách địa lý bằng công thức Haversine.
     *
     * @param lat1 vĩ độ điểm đầu
     * @param lon1 kinh độ điểm đầu
     * @param lat2 vĩ độ điểm cuối
     * @param lon2 kinh độ điểm cuối
     * @return khoảng cách km
     */
    private double calculateHaversineKm(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {

        double latitude1 = Math.toRadians(lat1);
        double latitude2 = Math.toRadians(lat2);

        double deltaLatitude = Math.toRadians(lat2 - lat1);

        double deltaLongitude = Math.toRadians(lon2 - lon1);

        double a = Math.pow(Math.sin(deltaLatitude / 2.0), 2)
                + Math.cos(latitude1)
                        * Math.cos(latitude2)
                        * Math.pow(Math.sin(deltaLongitude / 2.0), 2);

        // Giới hạn a trong [0, 1] để tránh sai số dấu phẩy động.
        a = Math.max(0.0, Math.min(1.0, a));

        double c = 2.0 * Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1.0 - a));

        return EARTH_RADIUS_KM * c;
    }
}
