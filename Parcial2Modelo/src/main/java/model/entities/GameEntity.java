package model.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GameEntity {
    private Integer id;
    private String title;
    private Integer categoryId;

    @Override
    public String toString() {
        return "\nGame: {" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", categoryId=" + categoryId +
                '}';
    }
}
