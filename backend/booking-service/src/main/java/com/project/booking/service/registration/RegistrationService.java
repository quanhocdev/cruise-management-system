package com.project.booking.service.registration;

import com.project.booking.model.*;
import com.project.booking.model.enums.*;
import com.project.booking.repository.*;
import com.project.booking.service.account.*;
import com.project.booking.service.command.CommandException;
import com.project.booking.service.onboard.ActivityCruiseUsageService;
import com.project.booking.service.shore.ActivityVisitUsageService;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {
    public record Guest(long id,String name,String status) {}
    public record Choice(RegistrationCatalog.Target target,long remainingSeats) {}
    public record Catalog(long bookingId,String bookingCode,UUID tourId,List<Guest> passengers,List<Choice> activities) {}
    public record Item(long id,long bookingPassengerId,String passengerName,String bookingCode,UUID tourId,String targetType,UUID targetId,
                       String name,LocalDateTime startTime,LocalDateTime endTime,BigDecimal listedPrice,String status,Long usageId,boolean canCancel) {}
    private static final String SELECT="""
        select r.*,p.full_name,b.booking_code from booking.activity_registrations r
        join booking.booking_passengers bp on bp.id=r.booking_passenger_id
        join booking.passengers p on p.id=bp.passenger_id join booking.bookings b on b.id=r.booking_id
        """;
    private final JdbcTemplate jdbc;
    private final BookingRepository bookings;
    private final BookingPassengerRepository passengers;
    private final RegistrationCatalog catalog;
    private final UsageCapacityLock capacity;
    private final OnboardAccountService accounts;
    private final ActivityCruiseUsageService activities;
    private final ActivityVisitUsageService visits;
    private final long cancellationHours;
    public RegistrationService(JdbcTemplate jdbc,BookingRepository bookings,BookingPassengerRepository passengers,
            RegistrationCatalog catalog,UsageCapacityLock capacity,OnboardAccountService accounts,
            ActivityCruiseUsageService activities,ActivityVisitUsageService visits,
            @Value("${booking.registrations.cancel-before-hours:24}") long cancellationHours) {
        this.jdbc=jdbc;this.bookings=bookings;this.passengers=passengers;this.catalog=catalog;this.capacity=capacity;
        this.accounts=accounts;this.activities=activities;this.visits=visits;this.cancellationHours=Math.max(0,cancellationHours);
    }
    @Transactional(readOnly=true)
    public Catalog catalog(long bookingId,long userId) {
        Booking b=owned(bookingId,userId);
        var guests=passengers.findAllByBooking_IdOrderByIdAsc(b.getId()).stream()
                .map(p->new Guest(p.getId(),p.getPassenger().getFullName(),p.getStatus().name())).toList();
        var choices=catalog.list(b.getTourId(),b.getTourPackageId()).stream()
                .filter(t->Set.of("CONFIGURED","NOT_STARTED","IN_PROGRESS").contains(Objects.toString(t.status(),"")))
                .map(t->new Choice(t,Math.max(0,(t.capacity()==null?0:t.capacity())-capacity.occupied(t.type(),t.id(),null)))).toList();
        return new Catalog(b.getId(),b.getBookingCode(),b.getTourId(),guests,choices);
    }
    @Transactional(readOnly=true)
    public void authorizePassenger(long passengerId,long userId) { owned(passenger(passengerId).getBooking().getId(),userId); }
    @Transactional(readOnly=true)
    public void authorizeItem(long id,long userId) {
        owned(passenger(read(id).bookingPassengerId()).getBooking().getId(),userId);
    }
    @Transactional(readOnly=true)
    public void authorizeStaff(long id,String type) {
        if(!read(id).targetType().equals(type)) throw fail("WRONG_DEPARTMENT","Đăng ký không thuộc bộ phận này",HttpStatus.FORBIDDEN);
    }
    @Transactional
    public Item register(long passengerId,String type,UUID targetId,long userId) {
        BookingPassenger p=passenger(passengerId);Booking b=owned(p.getBooking().getId(),userId);
        var t=catalog.get(type,targetId,b.getTourPackageId());
        lockBooking(b.getId());capacity.acquire(type,targetId);
        if(b.getStatus()!=BookingStatus.CONFIRMED || p.getStatus()==BookingPassengerStatus.CHECKED_OUT)
            throw fail("BOOKING_NOT_ELIGIBLE","Booking chưa xác nhận hoặc khách đã checkout",HttpStatus.UNPROCESSABLE_ENTITY);
        validTarget(t,b);
        var existing=jdbc.query(SELECT+" where r.booking_passenger_id=? and r.target_type=? and r.target_id=? and r.status<>'CANCELLED'",this::map,passengerId,type,targetId);
        if(!existing.isEmpty()) return existing.get(0);
        capacity.requireSeat(type,targetId,passengerId,t.capacity());
        Long id=jdbc.queryForObject("""
            insert into booking.activity_registrations(booking_id,booking_passenger_id,tour_id,target_type,target_id,name,starts_at,ends_at,listed_price,status)
            values (?,?,?,?,?,?,?,?,?,'CONFIRMED') returning id
            """,Long.class,b.getId(),passengerId,b.getTourId(),type,targetId,t.name(),t.startTime(),t.endTime(),t.price());
        notify(b.getCreatedByUserId(),"Đăng ký thành công",p.getPassenger().getFullName()+" đã giữ chỗ: "+t.name()+" lúc "+t.startTime()+". Phí và quyền lợi được tính khi xác nhận tham gia.");
        return read(id);
    }
    @Transactional
    public Item cancel(long id,long userId) {
        Item initial=read(id);Booking b=owned(passenger(initial.bookingPassengerId()).getBooking().getId(),userId);
        lockBooking(b.getId());capacity.acquire(initial.targetType(),initial.targetId());Item item=read(id);
        if(item.status().equals("CANCELLED")) return item;
        var current=catalog.get(item.targetType(),item.targetId(),b.getTourPackageId());
        if(!item.status().equals("CONFIRMED") || current.startTime()==null || !LocalDateTime.now().isBefore(current.startTime().minusHours(cancellationHours)))
            throw fail("CANCELLATION_CLOSED","Đã hết hạn hủy tự động. Vui lòng liên hệ nhân viên.",HttpStatus.UNPROCESSABLE_ENTITY);
        jdbc.update("update booking.activity_registrations set status='CANCELLED',updated_at=CURRENT_TIMESTAMP where id=?",id);
        notify(userId,"Đã hủy đăng ký",item.passengerName()+" đã hủy chỗ: "+item.name()+". Chưa phát sinh phí tham gia.");
        return read(id);
    }
    @Transactional
    public Item attend(long id,String type,String actor,String reason,UUID requestId) {
        authorizeStaff(id,type);Item initial=read(id);BookingPassenger p=passenger(initial.bookingPassengerId());
        lockBooking(p.getBooking().getId());accounts.lockForUsage(p.getBooking());capacity.acquire(type,initial.targetId());
        Item item=read(id);
        if(!item.status().equals("CONFIRMED")) throw fail("REGISTRATION_NOT_ACTIVE","Đăng ký đã hủy hoặc đã tham gia",HttpStatus.UNPROCESSABLE_ENTITY);
        if("ACTIVITY".equals(type)) activities.createForPassenger(p.getId(),item.targetId(),requestId);
        else visits.createForPassenger(p.getId(),item.targetId(),requestId);
        jdbc.update("update booking.activity_registrations set attendance_actor=?,attendance_reason=? where id=?",actor,reason.trim(),id);
        return read(id);
    }
    @Transactional(readOnly=true)
    public List<Item> mine(long bookingId,long userId,int page) { owned(bookingId,userId);checkPage(page);return jdbc.query(SELECT+" where r.booking_id=? order by r.id desc limit 20 offset ?",this::map,bookingId,(long)page*20); }
    @Transactional(readOnly=true)
    public List<Item> staff(String type,String code,String search,int page) {
        checkPage(page);
        if(code==null || code.isBlank()) throw fail("BOOKING_CODE_REQUIRED","Nhập mã booking",HttpStatus.BAD_REQUEST);
        return jdbc.query(SELECT+" where lower(b.booking_code)=lower(?) and r.target_type=? and position(lower(?) in lower(p.full_name))>0 order by r.starts_at,r.id limit 20 offset ?",this::map,code.trim(),type,search==null?"":search.trim(),(long)page*20);
    }
    public void notify(long userId,String title,String message) {
        jdbc.update("insert into booking.registration_notifications(id,recipient_id,title,message) values (?,?,?,?)",UUID.randomUUID(),userId,title,message);
    }
    public Item read(long id) { return jdbc.query(SELECT+" where r.id=?",this::map,id).stream().findFirst().orElseThrow(()->fail("REGISTRATION_NOT_FOUND","Không tìm thấy đăng ký",HttpStatus.NOT_FOUND)); }
    private BookingPassenger passenger(long id) {return passengers.findById(id).orElseThrow(()->fail("PASSENGER_NOT_FOUND","Không tìm thấy hành khách",HttpStatus.NOT_FOUND));}
    private Booking owned(long id,long user) {
        Booking b=bookings.findById(id).orElseThrow(()->fail("BOOKING_NOT_FOUND","Không tìm thấy booking",HttpStatus.NOT_FOUND));
        if(!Objects.equals(b.getCreatedByUserId(),user)) throw fail("BOOKING_FORBIDDEN","Booking không thuộc tài khoản của bạn",HttpStatus.FORBIDDEN);
        return b;
    }
    private void lockBooking(long id) {jdbc.queryForObject("select id from booking.bookings where id=? for update",Long.class,id);}
    private void checkPage(int page) {if(page<0 || page>100000)throw fail("INVALID_PAGE","Trang không hợp lệ",HttpStatus.BAD_REQUEST);}
    private void validTarget(RegistrationCatalog.Target t,Booking b) {
        if(!Objects.equals(t.tourId(),b.getTourId())) throw fail("WRONG_TOUR","Hoạt động không thuộc chuyến của booking",HttpStatus.UNPROCESSABLE_ENTITY);
        if(t.startTime()==null || t.endTime()==null || !t.endTime().isAfter(t.startTime()) || !LocalDateTime.now().isBefore(t.startTime())
            || !Set.of("CONFIGURED","NOT_STARTED").contains(Objects.toString(t.status(),"")) || t.capacity()==null || t.capacity()<1 || t.price()==null || t.price().signum()<0)
            throw fail("REGISTRATION_CLOSED","Hoạt động chưa được cấu hình hoặc đã đóng đăng ký",HttpStatus.UNPROCESSABLE_ENTITY);
    }
    private Item map(ResultSet rs,int row)throws SQLException {
        LocalDateTime start=rs.getTimestamp("starts_at").toLocalDateTime();String status=rs.getString("status");
        return new Item(rs.getLong("id"),rs.getLong("booking_passenger_id"),rs.getString("full_name"),rs.getString("booking_code"),rs.getObject("tour_id",UUID.class),
                rs.getString("target_type"),rs.getObject("target_id",UUID.class),rs.getString("name"),start,rs.getTimestamp("ends_at").toLocalDateTime(),rs.getBigDecimal("listed_price"),status,
                rs.getObject("usage_id",Long.class),status.equals("CONFIRMED") && LocalDateTime.now().isBefore(start.minusHours(cancellationHours)));
    }
    private CommandException fail(String code,String message,HttpStatus status){return new CommandException(code,message,status);}
}
