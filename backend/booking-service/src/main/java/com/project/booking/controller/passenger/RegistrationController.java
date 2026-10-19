package com.project.booking.controller.passenger;

import com.project.booking.service.registration.RegistrationService;
import com.project.booking.service.command.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
public class RegistrationController {
    public record Register(@NotNull Long bookingPassengerId,@Pattern(regexp="ACTIVITY|SHORE") @NotNull String targetType,@NotNull UUID targetId,@NotNull UUID clientRequestId) {}
    public record Command(@NotNull UUID clientRequestId) {}
    public record Attendance(@NotNull UUID clientRequestId,@NotBlank @Size(max=1000) String reason) {}
    private final RegistrationService service;
    private final IdempotentCommandService commands;
    public RegistrationController(RegistrationService service,IdempotentCommandService commands){this.service=service;this.commands=commands;}
    @GetMapping("/api/passengers/bookings/{bookingId}/activities")
    public RegistrationService.Catalog catalog(@PathVariable long bookingId,JwtAuthenticationToken jwt){return service.catalog(bookingId,user(jwt,"PASSENGER"));}
    @GetMapping("/api/passengers/bookings/{bookingId}/registrations")
    public List<RegistrationService.Item> mine(@PathVariable long bookingId,@RequestParam(defaultValue="0") int page,JwtAuthenticationToken jwt){return service.mine(bookingId,user(jwt,"PASSENGER"),page);}
    @PostMapping("/api/passengers/registrations")
    public ResponseEntity<RegistrationService.Item> register(@Valid @RequestBody Register request,JwtAuthenticationToken jwt){
        long user=user(jwt,"PASSENGER");service.authorizePassenger(request.bookingPassengerId(),user);
        return commands.execute("REGISTER_ACTIVITY",request.clientRequestId(),request,RegistrationService.Item.class,
                ()->service.register(request.bookingPassengerId(),request.targetType(),request.targetId(),user));
    }
    @PostMapping("/api/passengers/registrations/{id}/cancel")
    public ResponseEntity<RegistrationService.Item> cancel(@PathVariable long id,@Valid @RequestBody Command request,JwtAuthenticationToken jwt){
        long user=user(jwt,"PASSENGER");service.authorizeItem(id,user);
        return commands.execute("CANCEL_REGISTRATION",request.clientRequestId(),Map.of("id",id),RegistrationService.Item.class,()->service.cancel(id,user));
    }
    @GetMapping({"/api/onboard/activity-cruise-usages/registrations","/api/shore/activity-visit-usages/registrations"})
    public List<RegistrationService.Item> staff(@RequestParam String bookingCode,@RequestParam(defaultValue="") String search,
            @RequestParam(defaultValue="0") int page,JwtAuthenticationToken jwt,jakarta.servlet.http.HttpServletRequest http){
        String role=department(http);user(jwt,role);return service.staff(role.equals("ONBOARD")?"ACTIVITY":"SHORE",bookingCode,search,page);
    }
    @PostMapping({"/api/onboard/activity-cruise-usages/registrations/{id}/attend","/api/shore/activity-visit-usages/registrations/{id}/attend"})
    public ResponseEntity<RegistrationService.Item> attend(@PathVariable long id,@Valid @RequestBody Attendance request,
            JwtAuthenticationToken jwt,jakarta.servlet.http.HttpServletRequest http){
        String role=department(http);long user=user(jwt,role);String type=role.equals("ONBOARD")?"ACTIVITY":"SHORE";service.authorizeStaff(id,type);
        return commands.execute("ATTEND_REGISTRATION",request.clientRequestId(),Map.of("id",id,"reason",request.reason()),RegistrationService.Item.class,
                ()->service.attend(id,type,"user:"+user,request.reason(),request.clientRequestId()));
    }
    private String department(jakarta.servlet.http.HttpServletRequest http){return http.getRequestURI().startsWith("/api/onboard/")?"ONBOARD":"SHORE";}
    private long user(JwtAuthenticationToken jwt,String role){
        if(jwt==null || jwt.getAuthorities().stream().noneMatch(a->a.getAuthority().equals("ROLE_"+role)))throw new CommandException("ROLE_REQUIRED","Không có quyền thực hiện",HttpStatus.FORBIDDEN);
        try{return Long.parseLong(String.valueOf(jwt.getTokenAttributes().get("userId")));}
        catch(Exception e){throw new CommandException("INVALID_USER_ID","Thiếu định danh tài khoản",HttpStatus.UNAUTHORIZED);}
    }
}
