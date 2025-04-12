package com.airbnb.spec;

import java.util.Set;

import org.springframework.data.jpa.domain.Specification;

import com.airbnb.entities.User;
import com.airbnb.entities.User_;

public class UserSpec {
    public static Specification<User> likeName(String name) {
        // Tham khảo cách viết Specification cũ:
        // https://spring.io/blog/2011/04/26/advanced-spring-data-jpa-specifications-and-querydsl

        // Do Specification là một functional interface, chỉ có 1 method toPredicate nên
        // ta có thể tự động ghi đè lại, nếu như có nhiều 2 methods trở lên thì không
        // ghi đè lại được như thế này
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(User_.NAME), "%" + name + "%");
        };
    }

    public static Specification<User> likeEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null || email.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(User_.EMAIL), "%" + email + "%");
        };
    }

    public static Specification<User> byIds(Set<Long> ids) {
        return (root, query, criteriaBuilder) -> {
            if (ids == null || ids.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            // return root.get(User_.ID).in(ids);
            return criteriaBuilder.in(root.get(User_.ID)).value(ids);
        };
    }
}
