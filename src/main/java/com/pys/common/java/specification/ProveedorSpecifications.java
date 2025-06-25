package com.pys.common.java.specification;

import com.pys.common.kotlin.model.Proveedor;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ProveedorSpecifications {

    public static Specification<Proveedor> searchByCadena(String cadena) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Concatenación de campos y comparación con LIKE
            Predicate likePredicate = criteriaBuilder.like(
                criteriaBuilder.concat(
                    criteriaBuilder.concat(
                        criteriaBuilder.coalesce(root.get("cuit"), ""),
                        " "
                    ),
                    criteriaBuilder.concat(
                        criteriaBuilder.coalesce(root.get("razonSocial"), ""),
                        criteriaBuilder.concat(
                            " ",
                            criteriaBuilder.coalesce(root.get("nombreFantasia"), "")
                        )
                    )
                ),
                "%" + cadena.toLowerCase() + "%"
            );

            // Asegurar que razonSocial no esté vacío
            Predicate razonSocialNotEmpty = criteriaBuilder.notEqual(
                root.get("razonSocial"), ""
            );

            predicates.add(likePredicate);
            predicates.add(razonSocialNotEmpty);

            // Ordenar por razonSocial
            query.orderBy(criteriaBuilder.asc(root.get("razonSocial")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
} 