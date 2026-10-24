package com.project.booking.service.registration;

import com.project.booking.client.ActivityCruiseClient;
import com.project.booking.client.VisitTourClient;
import com.project.booking.service.command.CommandException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RegistrationCatalog {
    @JsonIgnoreProperties(ignoreUnknown=true) public record Ref(UUID id) {}
    @JsonIgnoreProperties(ignoreUnknown=true) public record Stop(Ref visitActivity) {}
    @JsonIgnoreProperties(ignoreUnknown=true) public record Day(List<Stop> stops) {}
    @JsonIgnoreProperties(ignoreUnknown=true) public record Detail(List<Ref> onboardActivities,List<Day> schedules) {}
    public record Target(String type,UUID id,UUID tourId,String name,LocalDateTime startTime,
                         LocalDateTime endTime,Integer capacity,BigDecimal price,String status,
                         UUID benefitId,Integer benefitQuantity,BigDecimal discountPercent) {}
    private final ActivityCruiseClient activities;
    private final VisitTourClient visits;
    private final RestClient http;
    public RegistrationCatalog(ActivityCruiseClient activities,VisitTourClient visits,RestClient.Builder builder,
                               @Value("${tour-service.url}") String url) {
        this.activities=activities;this.visits=visits;this.http=builder.baseUrl(url).build();
    }
    public Target get(String type,UUID id,UUID packageId) {
        if ("ACTIVITY".equals(type)) {
            var t=activities.getActivityCruiseUsageInfo(id,packageId);
            return new Target(type,t.activityCruiseTourId(),t.tourId(),t.activityName(),t.startTime(),t.endTime(),
                    t.maxPassengers(),t.price(),t.activityActive()?t.activityCruiseTourStatus():"CANCELLED",t.packageBenefitId(),t.benefitQuantity(),t.discountPercent());
        }
        if ("SHORE".equals(type)) {
            var t=visits.getVisitTourUsageInfo(id,packageId);
            return new Target(type,t.visitTourId(),t.tourId(),t.name(),t.startTime(),t.endTime(),t.maxPassengers(),
                    t.price(),t.visitTourStatus(),t.packageBenefitId(),t.benefitQuantity(),t.discountPercent());
        }
        throw new CommandException("INVALID_TARGET_TYPE","Loại hoạt động không hợp lệ",HttpStatus.BAD_REQUEST);
    }
    public List<Target> list(UUID tourId,UUID packageId) {
        Detail detail=http.get().uri("/api/public/tours/{id}",tourId).retrieve().body(Detail.class);
        if(detail==null) return List.of();
        var refs=new LinkedHashMap<String,UUID>();
        for(var item:detail.onboardActivities()==null?List.<Ref>of():detail.onboardActivities()) if(item.id()!=null) refs.put("ACTIVITY:"+item.id(),item.id());
        for(var day:detail.schedules()==null?List.<Day>of():detail.schedules()) for(var stop:day.stops()==null?List.<Stop>of():day.stops()) {
            var item=stop.visitActivity();
            if(item!=null && item.id()!=null) refs.put("SHORE:"+item.id(),item.id());
        }
        return refs.entrySet().stream().map(e->get(e.getKey().split(":")[0],e.getValue(),packageId)).toList();
    }
}
