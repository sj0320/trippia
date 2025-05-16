package com.trippia.travel.domain.travel.scheduleitem;

import com.trippia.travel.controller.dto.scheduleitem.response.ScheduleItemResponse;
import com.trippia.travel.domain.travel.scheduleitem.memo.Memo;
import com.trippia.travel.domain.travel.scheduleitem.scheduleplace.SchedulePlace;

import java.util.List;


public class ScheduleItemConverter {

    public static ScheduleItemResponse toResponse(ScheduleItem item){
        if(item instanceof Memo memo){
            return ScheduleItemResponse.ofMemo(memo);
        }
        else if(item instanceof SchedulePlace place){
            return ScheduleItemResponse.ofPlace(place);
        }
        else{
            throw new IllegalArgumentException("Unknown ItemType");
        }
    }

    public static List<ScheduleItemResponse> toResponses(List<ScheduleItem> items){
        return items.stream()
                .map(ScheduleItemConverter::toResponse)
                .toList();
    }

}
