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

}
