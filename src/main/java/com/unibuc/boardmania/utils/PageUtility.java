package com.unibuc.boardmania.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@UtilityClass
public class PageUtility {

    public static Pageable getEventsPageable(Integer pageNumber, Integer pageSize) {
        if (pageSize == 0) {
            return Pageable.unpaged();
        }

        return PageRequest.of(pageNumber, pageSize);

    }

}
