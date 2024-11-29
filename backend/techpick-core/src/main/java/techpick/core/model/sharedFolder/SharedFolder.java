package techpick.core.model.sharedFolder;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import techpick.core.model.common.BaseEntity;
import techpick.core.model.folder.Folder;
import techpick.core.model.user.User;

@Table(name = "sharedFolder")
@Entity
@Getter
@NoArgsConstructor
public class SharedFolder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "folder_id", nullable = false, unique = true)
    private Folder folder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private SharedFolder(User user, Folder folder) {
        this.user = user;
        this.folder = folder;
    }

    public static SharedFolder createSharedFolder(User user, Folder folder) {
        return new SharedFolder(user, folder);
    }
}