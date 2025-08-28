package com.back.domain.checkList.checkList.entity;

import com.back.domain.schedule.schedule.entity.Schedule;
import jakarta.persistence.*;
import jdk.jfr.Description;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CheckList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    @EqualsAndHashCode.Include
    private Long id;

    @Description("활성화 여부")
    private boolean isActive;

    @Description("연동된 일정")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "checkList", orphanRemoval = true)
    @Description("체크리스트 아이템들")
    private List<CheckListItem> checkListItems;

    @Builder
    public CheckList(boolean isActive, Schedule schedule, List<CheckListItem> checkListItems) {
        this.isActive = isActive;
        this.schedule = schedule;
        if (checkListItems != null) {
            this.checkListItems = checkListItems;
            // 양방향 연관관계 설정
            checkListItems.forEach(item -> item.setCheckList(this));
        }
    }

    // TODO: 코틀린과의 호환성을 위한 임시 getter. CheckList 엔티티가 코틀린으로 전환되면 삭제해주세요.
    public boolean isActive() {
        return this.isActive;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;

        // 양방향 관계 설정
        if (schedule.getCheckList() != this) {
            schedule.updateCheckList(this);
        }
    }

    public void updateIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void updateCheckListItems(List<CheckListItem> checkListItems) {
        this.checkListItems.clear();
        this.checkListItems.addAll(checkListItems);

        // 양방향 연관관계 설정
        if (checkListItems != null) {
            checkListItems.forEach(item -> item.setCheckList(this));
        }
    }
}
