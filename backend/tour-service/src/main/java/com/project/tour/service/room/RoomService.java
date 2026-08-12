package com.project.tour.service.room;

import com.project.tour.dto.room.CreateRoomRequest;
import com.project.tour.dto.room.RoomResponse;
import com.project.tour.dto.room.UpdateRoomRequest;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.Room;
import com.project.tour.model.RoomType;
import com.project.tour.model.enums.RoomStatus;
import com.project.tour.repository.cruise.CruiseDeckRepository;
import com.project.tour.repository.room.RoomRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class RoomService {

}
