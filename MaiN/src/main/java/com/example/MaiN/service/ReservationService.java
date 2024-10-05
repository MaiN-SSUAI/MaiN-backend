package com.example.MaiN.service;


import com.example.MaiN.Exception.CustomErrorCode;
import com.example.MaiN.Exception.CustomException;
import com.example.MaiN.dto.EventDto;
import com.example.MaiN.dto.PushMessage;
import com.example.MaiN.entity.Reserv;
import com.example.MaiN.entity.ReservAssign;
import com.example.MaiN.entity.User;
import com.example.MaiN.repository.ReservAssignRepository;
import com.example.MaiN.repository.ReservRepository;
import com.example.MaiN.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.format.DateTimeFormatter;

import java.io.IOException;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    private final ReservRepository reservRepository;
    private final UserRepository userRepository;
    private final ReservAssignRepository reservAssignRepository;
    private final ReservationValidService reservationValidService;
    private final CalendarService calendarService;
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    //예약 제한 사항 (사용자 사용시간 관련) 체크
    public void checkUser(String studentId, LocalDate date) {
        Optional<User> userOptional = userRepository.findByStudentNo(studentId);
        User user = userOptional.orElse(null);
        if(user == null){
            return;
        }
        int userId = user.getId();
        reservationValidService.checkReservationPerMonth(date);
        reservationValidService.checkReservationPerWeek(studentId, userId, date);
    }

    //예약 등록
    @Transactional
    public String addReservation(EventDto eventDto) throws Exception {
        // 예약 제한사항 체크 (2시간 이상, 30분 미만 금지, 2인 이상, 겹치는지 확인)
        reservationValidService.checkReservation(eventDto.getStartDateTime(),eventDto.getEndDateTime(),eventDto.getStudentIds());

        List<String> studentIds = eventDto.getStudentIds();

        return ReservationLockService.executeWithLock(eventDto.getStartDateTime(), eventDto.getEndDateTime(), () -> {
            Reserv savedReserv = null;

            for(int i=0; i<studentIds.size(); i++) {

                String studentId = studentIds.get(i);
                Optional<User> user = userRepository.findByStudentNo(studentId);

                //저장되어 있지 않은 학생인 경우 user 테이블에 저장, 저장되어 있으면 pk get
                int userId = user.map(User::getId).orElseGet(() -> addUninformedUser(studentId));

                //주최자인 경우
                if (i == 0) {
                    //구글 캘린더에 올리는 메소드
                    String eventId = calendarService.addReservation(studentIds, eventDto.getStartDateTime(), eventDto.getEndDateTime());
                    eventDto.setEventId(eventId);

                    //Reserv 테이블에 데이터 저장
                    Reserv reserv = new Reserv(eventDto,userId);
                    savedReserv = reservRepository.save(reserv);
                }

                //Reserv Assign 테이블에 데이터 저장
                ReservAssign reservAssign = new ReservAssign(savedReserv, userId);
                reservAssignRepository.save(reservAssign);
            }

            return "success";
        });

    }

    //예약 삭제
    @Transactional
    public String deleteReservation(int reservId) throws Exception {

        //시작시간 30분 이후인지 확인
        reservationValidService.checkDeleteTime(reservId);

        Reserv reserv = reservRepository.findByReservId(reservId);
        String eventId = reserv.getEventId();

        //구글 캘린더에서 삭제 메소드 호출하기
        calendarService.deleteReservation(eventId);

        reservRepository.deleteById(reservId);
        return "예약 삭제 성공";
    }

    //예약 수정
    @Transactional
    public String updateReservation(int reservId, EventDto eventDto) throws Exception {
        Reserv reserv = reservRepository.findByReservId(reservId);

        // 시작시간 30분 이후인지 확인
        reservationValidService.checkDeleteTime(reservId);

        if (reserv == null) {
            throw new CustomException("존재하지 않는 예약입니다.", CustomErrorCode.NOT_EXIST_RESERVATION);
        }

        // 예약 유효성 검사
        reservationValidService.checkReservation(eventDto.getStartDateTime(), eventDto.getEndDateTime(), eventDto.getStudentIds());

        // 사용 구성원 변경
        List<String> newStudentIds = eventDto.getStudentIds(); // 입력으로 받은 학번 리스트
        List<Integer> existingUserIds = reservAssignRepository.findUserIdsByReservId(reservId); // 기존에 연결되어 있던 학생의 userId 리스트

        System.out.println("newStudentIDs : " + newStudentIds);
        System.out.println("existingUserIds : " + existingUserIds);

        // 추가할 학생 userId 리스트
        List<Integer> addedStudent = new ArrayList<>();
        // 삭제될 학생 userId 리스트
        List<Integer> deletedStudent = new ArrayList<>(existingUserIds);

        for (String studentId : newStudentIds) {
            // 학번에 해당하는 userId 불러오기
            Optional<User> user = userRepository.findByStudentNo(studentId);
            int userId = user.map(User::getId).orElseGet(() -> addUninformedUser(studentId));

            // 요청된 studentId 리스트에 없는 사용자인 경우 제거하기
            deletedStudent.remove(Integer.valueOf(userId));

            if (!existingUserIds.contains(userId)) {
                addedStudent.add(userId);
            }

            System.out.println("deletedStudent : " + deletedStudent);
            System.out.println("addedStudent : " + addedStudent);
        }

        // 사용목적, 사용시간 변경
        reserv.updateReserv(eventDto);
        reservRepository.save(reserv);

        // Reserv Assign 테이블의 데이터 수정
        // 학생 추가
        for (Integer userId : addedStudent) {
            ReservAssign reservAssign = new ReservAssign(reserv, userId);
            reservAssignRepository.save(reservAssign);
        }

        // 학생 제거
        for (Integer userId : deletedStudent) {
            reservAssignRepository.deleteByReservAndUserId(reserv, userId);
        }

        // 구글 캘린더 수정 메소드 호출
        calendarService.updateReservation(reserv.getEventId(), newStudentIds, eventDto.getStartDateTime(), eventDto.getEndDateTime());

        return "예약 수정 성공";
    }

    //앱 사용자가 아닌 경우 DB 에 저장
    @Transactional
    public int addUninformedUser(String studentId) {
        User user = new User();
        user.setStudentNo(studentId);
        user.setStudentName("");
        user.setRefreshToken("");
        System.out.println("Unauthorized User Addition Successful");
        return userRepository.save(user).getId();
    }

    //User에게 푸쉬 알림을 보내는 메서드
    public void sendNotificationToUsers(List<String> userIds, PushMessage pushMessage, String... args) throws IOException {
        for (String userIdStr : userIds) {
            Optional<String> fcmTokenOpt = userRepository.findById(userIdStr).map(User::getFcmToken);

            if (fcmTokenOpt.isPresent() && !fcmTokenOpt.get().isEmpty()) { //FCM 토큰이 존재하는 유저일 경우, 예약 알림 보냄.
                String fcmToken = fcmTokenOpt.get();
                log.info("푸쉬 알림 - userId: {}, FCM token: {}", userIdStr, fcmToken);
                firebaseCloudMessageService.sendMessageTo(fcmToken, pushMessage, args);
            } else { //FCM 토큰이 존재하지 않는 유저일 경우, 예약 알림을 보내지 않음.
                log.warn("FCM 토큰을 찾을 수 없음 - userId: {}", userIdStr);
            }
        }
    }

    public List<Reserv> getReservationsStartingIn30Minutes() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0); // 초와 나노초 제거
        LocalDateTime exactTime = now.plusMinutes(30); // 30분 후 시간

        return reservRepository.findReservationsStartingIn30Minutes(exactTime); // 30분 후에 해당하는 예약만 조회
    }

    public List<Reserv> getReservationsEndingIn5Minutes() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0); // 초와 나노초 제거
        LocalDateTime exactTime = now.plusMinutes(5); // 5분 후 시간

        return reservRepository.findReservationsEndingIn5Minutes(exactTime); // 5분 후에 해당하는 예약만 조회
    }
}

