package com.unibuc.boardmania.specifications;

import com.unibuc.boardmania.model.Event;
import org.springframework.data.jpa.domain.Specification;

public abstract class EventSpecifications {

    public static Specification<Event> searchName(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String searchString = String.format("%%%s%%", searchTerm);

            return criteriaBuilder.like(root.get("name"), searchString);
        };
    }

    public static Specification<Event> searchType(int locationType) {
        return (root, query, criteriaBuilder) -> {
            if (locationType == 0) {
                return criteriaBuilder.conjunction();
            }

            boolean onlineFilter = locationType == 1;
            return criteriaBuilder.equal(root.get("online"), onlineFilter);
        };
    }

    public static Specification<Event> afterNow() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("eventDateTimestamp"), System.currentTimeMillis());
    }

}
