package com.trippia.travel.event.diary.model;

public record DiaryUpdatedEvent(Long diaryId, String title, String thumbnail) {
}
