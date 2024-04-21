package logParser.dataModel;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@Table(name="logs")
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
