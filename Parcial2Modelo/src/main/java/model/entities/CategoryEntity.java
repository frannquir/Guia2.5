package model.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class CategoryEntity {
    private Integer id;
    private String name;

    @Override
    public String toString() {
        return "\nCategory: {" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
