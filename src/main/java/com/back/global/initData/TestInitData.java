package com.back.global.initData;

import com.back.domain.checkList.checkList.entity.CheckList;
import com.back.domain.checkList.checkList.entity.CheckListItem;
import com.back.domain.checkList.checkList.repository.CheckListItemRepository;
import com.back.domain.checkList.checkList.repository.CheckListRepository;
import com.back.domain.checkList.itemAssign.entity.ItemAssign;
import com.back.domain.checkList.itemAssign.repository.ItemAssignRepository;
import com.back.domain.club.club.entity.Club;
import com.back.domain.club.club.repository.ClubRepository;
import com.back.domain.club.clubMember.entity.ClubMember;
import com.back.domain.club.clubMember.repository.ClubMemberRepository;
import com.back.domain.member.friend.entity.Friend;
import com.back.domain.member.friend.entity.FriendStatus;
import com.back.domain.member.friend.repository.FriendRepository;
import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.entity.MemberInfo;
import com.back.domain.member.member.repository.MemberInfoRepository;
import com.back.domain.member.member.repository.MemberRepository;
import com.back.domain.schedule.schedule.entity.Schedule;
import com.back.domain.schedule.schedule.repository.ScheduleRepository;
import com.back.global.enums.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


/**
 * 테스트 환경의 초기 데이터 설정
 */
@Configuration
@Profile("test")
@RequiredArgsConstructor
public class TestInitData {
    private final MemberRepository memberRepository;
    private final MemberInfoRepository memberInfoRepository;
    private final FriendRepository friendRepository;
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final ScheduleRepository scheduleRepository;
    private final CheckListRepository checkListRepository;
    private final CheckListItemRepository checkListItemRepository;
    private final ItemAssignRepository itemAssignRepository;

    @Autowired
    @Lazy
    private TestInitData self;

    private Map<String, Member> members;
    private Map<String, Club> clubs;

    @Bean
    ApplicationRunner testInitDataApplicationRunner() {
        return args -> {
            // 회원 관련 데이터 초기화
            self.initMemberTestData();
            self.initFriendTestData();

            // 모임 관련 데이터 초기화
            self.initGroupTestData();
            self.initGroupMemberTestData();

            // 일정 관련 데이터 초기화
            self.initScheduleTestData();

            // 체크리스트 관련 데이터 초기화
            self.initCheckListTestData();
            self.initCheckListItemTestData();
            self.initItemAssignTestData();
        };
    }

    /**
     * 회원, 회원 정보 초기 데이터 설정
     */
    @Transactional
    public void initMemberTestData() {
        members = new HashMap<>();

        // 회원
        Member member1 = createMember("홍길동", "password1", "hgd222@test.com", "안녕하세요. 홍길동입니다.");
        members.put(member1.getNickname(), member1);

        Member member2 = createMember("김철수", "password2", "chs4s@test.com", "안녕하세요. 김철수입니다.");
        members.put(member2.getNickname(), member2);

        Member member3 = createMember("이영희", "password3", "lyh3@test.com", "안녕하세요. 이영희입니다.");
        members.put(member3.getNickname(), member3);

        Member member4 = createMember("최지우", "password4", "cjw5@test.com", "안녕하세요. 최지우입니다.");
        members.put(member4.getNickname(), member4);

        Member member5 = createMember("박민수", "password5", "pms4@test.com", "안녕하세요. 박민수입니다.");
        members.put(member5.getNickname(), member5);

        Member member6 = createMember("유나영", "password6", "uny@test.com", "안녕하세요, 유나영입니다."); //가입 신청 테스트용
        members.put(member6.getNickname(), member6);

        Member member7 = createMember("이채원", "password7", "lcw@test.com", "안녕하세요, 이채원입니다."); //가입 신청 테스트용
        members.put(member7.getNickname(), member7);

        Member member8 = createMember("호윤호", "password8", "hyh@test.com", "안녕하세요, 호윤호입니다."); //가입 신청 테스트용
        members.put(member8.getNickname(), member8);

        // 비회원
        Member guest1 = createMember("이덕혜", "password11", null, null);
        members.put(guest1.getNickname(), guest1);

        Member guest2 = createMember("레베카", "password12", null, null);
        members.put(guest2.getNickname(), guest2);

        Member guest3 = createEncodedMember("김암호", "password13", null, null);
        members.put(guest3.getNickname(), guest3);
    }

    /**
     * 친구 초기 데이터 설정
     */
    @Transactional
    public void initFriendTestData() {
        Member requester = members.get("홍길동");

        // 친구 요청을 보낸 회원
        Member responder1 = members.get("이영희");
        Friend friend1 = Friend.builder()
                .requestedBy(requester)
                .member1(requester)
                .member2(responder1)
                .status(FriendStatus.PENDING)
                .build();
        friendRepository.save(friend1);

        // 친구 요청을 수락한 회원
        Member responder2 = members.get("최지우");
        Friend friend2 = Friend.builder()
                .requestedBy(requester)
                .member1(requester)
                .member2(responder2)
                .status(FriendStatus.ACCEPTED)
                .build();
        friendRepository.save(friend2);

        // 친구 요청을 거절한 회원
        Member responder3 = members.get("박민수");
        Friend friend3 = Friend.builder()
                .requestedBy(requester)
                .member1(requester)
                .member2(responder3)
                .status(FriendStatus.REJECTED)
                .build();
        friendRepository.save(friend3);
    }

    /**
     * 모임 초기 데이터 설정
     */
    @Transactional
    public void initGroupTestData() {
        Member leader1 = members.get("홍길동");
        clubs = new HashMap<>();

        // 장기 공개 모임 - 모집 중
        Club club1 = new Club(
                null,
                "산책 모임",
                null,
                ClubCategory.SPORTS,
                "서울",
                25,
                true,
                EventType.LONG_TERM,
                LocalDate.parse("2025-07-05"),
                LocalDate.parse("2025-08-30"),
                null,
                true,
                leader1.getId(),
                true
        );
        clubRepository.save(club1);
        clubs.put(club1.getName(), club1);

        ClubMember clubMember1 = new ClubMember(leader1, ClubMemberRole.HOST, ClubMemberState.JOINING);
        club1.addClubMember(clubMember1);
        clubMemberRepository.save(clubMember1);

        // 장기 비공개 모임 - 모집 마감
        Club club2 = new Club(
                null,
                "친구 모임",
                null,
                ClubCategory.TRAVEL,
                "강원도",
                4,
                false,
                EventType.LONG_TERM,
                LocalDate.parse("2025-05-01"),
                LocalDate.parse("2026-12-31"),
                null,
                false,
                leader1.getId(),
                true
        );
        clubRepository.save(club2);
        clubs.put(club2.getName(), club2);

        ClubMember clubMember2 = new ClubMember(leader1, ClubMemberRole.HOST, ClubMemberState.JOINING);
        club2.addClubMember(clubMember2);
        clubMemberRepository.save(clubMember2);

        // 단기 비공개 모임 - 모집중
        Club club3 = new Club(
                null,
                "친구 모임2",
                null,
                ClubCategory.TRAVEL,
                "제주도",
                5,
                true,
                EventType.SHORT_TERM,
                LocalDate.parse("2025-07-01"),
                LocalDate.parse("2025-12-31"),
                null,
                false,
                leader1.getId(),
                true
        );
        clubRepository.save(club3);
        clubs.put(club3.getName(), club3);

        ClubMember clubMember3 = new ClubMember(leader1, ClubMemberRole.HOST, ClubMemberState.JOINING);
        club3.addClubMember(clubMember3);
        clubMemberRepository.save(clubMember3);

        Member leader2 = members.get("최지우");

        // 일회성 공개 모임 - 모집 중
        Club club4 = new Club(
                null,
                "A도시 러닝 대회",
                null,
                ClubCategory.SPORTS,
                "서울",
                50,
                true,
                EventType.ONE_TIME,
                LocalDate.parse("2025-08-10"),
                LocalDate.parse("2025-08-10"),
                null,
                true,
                leader2.getId(),
                true
        );
        clubRepository.save(club4);
        clubs.put(club4.getName(), club4);

        ClubMember clubMember4 = new ClubMember(leader2, ClubMemberRole.HOST, ClubMemberState.JOINING);
        club4.addClubMember(clubMember4);
        clubMemberRepository.save(clubMember4);

        // 종료일 지난 모임
        Club nClub1 = new Club(
                null,
                "독서 모임",
                null,
                ClubCategory.STUDY,
                "부산",
                10,
                true,
                EventType.SHORT_TERM,
                LocalDate.parse("2025-07-12"),
                LocalDate.parse("2025-07-12"),
                "img3",
                false,
                leader2.getId(),
                true
        );
        clubRepository.save(nClub1);
        clubs.put(nClub1.getName(), nClub1);

        ClubMember nClubMember1 = new ClubMember(leader2, ClubMemberRole.HOST, ClubMemberState.JOINING);
        nClub1.addClubMember(nClubMember1);
        clubMemberRepository.save(nClubMember1);

        // 삭제된 모임
        Club nClub2 = new Club(
                null,
                "테니스 모임",
                null,
                ClubCategory.SPORTS,
                "충청도 A 테니스장",
                2,
                false,
                EventType.SHORT_TERM,
                LocalDate.parse("2025-07-05"),
                LocalDate.parse("2025-08-11"),
                "img4",
                false,
                leader1.getId(),
                false
        );
        clubRepository.save(nClub2);
        clubs.put(nClub2.getName(), nClub2);

        ClubMember nClubMember2 = new ClubMember(leader2, ClubMemberRole.HOST, ClubMemberState.JOINING);
        nClub2.addClubMember(nClubMember2);
        clubMemberRepository.save(nClubMember2);
    }

    /**
     * 모임 맴버 헬퍼 dto
     */
    private record GroupMemberData(
            String clubName,
            String memberNickname,
            ClubMemberRole role
    ) {
    }

    /**
     * 모임 맴버 초기 데이터 설정
     */
    @Transactional
    public void initGroupMemberTestData() {
        List<GroupMemberData> groupMembers = List.of(
                new GroupMemberData("산책 모임", "김철수", ClubMemberRole.MANAGER),
                new GroupMemberData("산책 모임", "이영희", ClubMemberRole.PARTICIPANT),
                new GroupMemberData("친구 모임", "박민수", ClubMemberRole.PARTICIPANT),
                new GroupMemberData("친구 모임", "이영희", ClubMemberRole.PARTICIPANT),
                new GroupMemberData("친구 모임2", "이덕혜", ClubMemberRole.PARTICIPANT),
                new GroupMemberData("독서 모임", "레베카", ClubMemberRole.PARTICIPANT),
                new GroupMemberData("친구 모임2", "김암호", ClubMemberRole.PARTICIPANT) //암호화 테스트용 데이터
        );

        for (GroupMemberData gm : groupMembers) {
            Club club = clubs.get(gm.clubName());
            Member member = members.get(gm.memberNickname());

            ClubMember clubMember = new ClubMember(
                    member,
                    gm.role(),
                    ClubMemberState.JOINING
            );
            clubMember.setClub(club);

            clubMemberRepository.save(clubMember);
        }
    }


    /**
     * 모임 일정 초기 데이터 설정
     */
    @Transactional
    public void initScheduleTestData() {
        // 모임 1의 일정 초기 데이터
        Club club1 = clubs.get("산책 모임");

        for (int i = 1; i <= 4; i++) {
            Schedule schedule = new Schedule(
                    "제 %s회 걷기 일정".formatted(i),
                    "서울에서 함께 산책합니다",
                    LocalDateTime.parse("2025-07-05T10:00:00").plusDays(i * 7),
                    LocalDateTime.parse("2025-07-05T15:00:00").plusDays(i * 7),
                    "서울시 서초동",
                    club1
            );
            scheduleRepository.save(schedule);
        }

        // 모임 2의 일정 초기 데이터
        Club club2 = clubs.get("친구 모임");

        Schedule schedule2 = new Schedule(
                "맛집 탐방",
                "시장 맛집 탐방",
                LocalDateTime.parse("2025-05-07T18:00:00"),
                LocalDateTime.parse("2025-05-07T21:30:00"),
                "단양시장",
                club2
        );
        scheduleRepository.save(schedule2);

        Schedule schedule3 = new Schedule(
                "강릉 여행",
                "1박 2일 강릉 여행",
                LocalDateTime.parse("2025-07-23T08:10:00"),
                LocalDateTime.parse("2025-07-24T15:00:00"),
                "강릉",
                club2
        );
        scheduleRepository.save(schedule3);

        // 모임 3의 일정 초기 데이터
        Club club3 = clubs.get("친구 모임2");
        Schedule schedule4 = new Schedule(
                "제주도 여행",
                "제주도에서 함께 여행해요",
                LocalDateTime.parse("2025-07-01T09:00:00"),
                LocalDateTime.parse("2025-07-05T18:00:00"),
                "제주도",
                club3
        );
        scheduleRepository.save(schedule4);

        // 모임 3의 일정 초기 데이터 - 비활성화된 일정
        Schedule schedule5 = new Schedule(
                "제주도 여행 (비활성화)",
                "제주도에서 함께 여행해요",
                LocalDateTime.parse("2025-10-01T09:00:00"),
                LocalDateTime.parse("2025-10-05T18:00:00"),
                "제주도",
                club3
        );
        scheduleRepository.save(schedule5);
        schedule5.deactivate();

        // 모임 4의 일정 초기 데이터
        Club club4 = clubs.get("A도시 러닝 대회");
        Schedule schedule6 = new Schedule(
                "A도시 러닝 대회",
                "A도시에서 열리는 러닝 대회에 참여해요",
                LocalDateTime.parse("2025-08-10T07:00:00"),
                LocalDateTime.parse("2025-08-10T12:00:00"),
                "서울 A도시",
                club4
        );
        scheduleRepository.save(schedule6);

        // 종료된 모임 일정
        Club nClub1 = clubs.get("독서 모임");
        Schedule nSchedule1 = new Schedule(
                "독서 모임 일정",
                "부산에서 함께 독서해요",
                LocalDateTime.parse("2025-07-12T10:00:00"),
                LocalDateTime.parse("2025-07-12T15:00:00"),
                "부산",
                nClub1
        );
        scheduleRepository.save(nSchedule1);
    }

    /**
     * 모임의 체크리스트 초기 데이터 설정
     */
    @Transactional
    public void initCheckListTestData() {
        List<String> clubNames = List.of("산책 모임", "친구 모임", "친구 모임2", "A도시 러닝 대회");

        for (String clubName : clubNames) {
            Club club = clubs.get(clubName);
            if (club == null) continue;

            List<Schedule> club1Schedules = scheduleRepository.findByClubIdOrderByStartDate(club.getId());

            for (Schedule schedule : club1Schedules) {
                if (schedule.getTitle().equals("강릉 여행")) continue; // 체크리스트 없는 일정(테스트용)

                CheckList checkList = CheckList.builder()
                        .isActive(true)
                        .build();
                checkList.setSchedule(schedule);
                checkListRepository.save(checkList);
            }
        }
    }

    /**
     * 체크리스트 항목 초기 데이터 설정
     */
    @Transactional
    public void initCheckListItemTestData() {
        List<CheckList> allCheckLists = checkListRepository.findAll();

        for (CheckList checkList : allCheckLists) {
            // 각 체크리스트에 3개의 체크리스트 항목 생성
            for (int i = 1; i <= 3; i++) {
                CheckListItem item = CheckListItem.builder()
                        .content("체크리스트 항목 " + i)
                        .isChecked(false)
                        .checkList(checkList)
                        .build();
                checkListItemRepository.save(item);
            }
        }
    }

    /**
     * 체크리스트 항목에 모임 맴버를 랜덤으로 할당
     */
    @Transactional
    public void initItemAssignTestData() {
        List<CheckListItem> allItems = checkListItemRepository.findAll();

        for (CheckListItem item : allItems) {
            Long clubId = item.getCheckList().getSchedule().getClub().getId();

            // 모임의 맴버들만 할당 대상
            List<ClubMember> clubMembers = clubMemberRepository.findAllByClubId(clubId);
            if (clubMembers.isEmpty()) {
                continue;
            }

            // 랜덤 할당
            int assignCount = 1 + (int) (Math.random());

            // 중복되지 않도록 할당
            Set<ClubMember> assignedMembers = new HashSet<>();
            for (int i = 0; i < assignCount; i++) {
                ClubMember assignee;

                // 중복되지 않는 멤버를 랜덤으로 선택
                do {
                    assignee = clubMembers.get((int) (Math.random() * clubMembers.size()));
                } while (assignedMembers.contains(assignee));
                assignedMembers.add(assignee);

                // 아이템 할당 생성
                ItemAssign assign = ItemAssign.builder()
                        .clubMember(assignee)
                        .checkListItem(item)
                        .build();

                assignee.addItemAssign(assign);
                itemAssignRepository.save(assign);
            }
        }
    }

    /**
     * 회원 생성 메서드
     */
    private Member createMember(String nickname, String password, String email, String bio) {
        Member member = new Member(
                null,                    // id
                nickname,
                password,
                MemberType.MEMBER,
                UUID.randomUUID().toString().substring(0, 5),
                null,                    // memberInfo
                new ArrayList<>(),       // presets
                new HashSet<>(),         // friendshipsAsMember1
                new HashSet<>(),         // friendshipsAsMember2
                new ArrayList<>()        // clubMembers
        );
        memberRepository.save(member);

        if (email == null) return member;

        MemberInfo info = new MemberInfo(
                null,           // id
                email,
                bio,
                "",             // profileImageUrl
                null,            //apiKey
                member          // Member 객체
        );

        memberInfoRepository.save(info);

        member.setMemberInfo(info);
        return member;
    }

    /**
     * 회원 생성 메서드 2 - 비밀번호 암호화 테스트용
     */
    private Member createEncodedMember(String nickname, String password, String email, String bio) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        Member member = Member.Companion.createGuest(nickname, passwordEncoder.encode(password), "2344");
        memberRepository.save(member);

        if (email == null) return member;

        MemberInfo info = new MemberInfo(
                null,     // id
                email,
                bio,
                null,     // profileImageUrl
                null,     // apiKey
                member    // _member
        );
        memberInfoRepository.save(info);

        member.setMemberInfo(info);
        return member;
    }
}