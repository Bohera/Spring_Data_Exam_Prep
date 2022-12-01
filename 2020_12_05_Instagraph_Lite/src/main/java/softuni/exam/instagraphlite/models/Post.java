package softuni.exam.instagraphlite.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "posts")
public class Post extends BaseEntity{

    @Column(nullable = false)
    private String caption;

    @ManyToOne
    private User user;

    @ManyToOne
    private Picture picture;

    @Override
    public String toString() {
        return String.format("==Post Details:\n" +
                "----Caption: %s\n" +
                "----Picture Size: %.2f",
                this.caption,
                this.picture.getSize());
    }

}
