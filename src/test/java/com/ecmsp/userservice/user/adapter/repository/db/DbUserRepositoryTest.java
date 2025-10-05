package com.ecmsp.userservice.user.adapter.repository.db;

import com.ecmsp.userservice.user.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class DbUserRepositoryTest {

    private static final UserId USER_1_ID = new UserId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    private static final UserId USER_2_ID = new UserId(UUID.fromString("9e349a18-1203-4224-829c-dc15700c68a5"));

    private static final User USER_1 = new User(
        /* id = */ USER_1_ID,
        /* login = */ "user1",
        /* passwordHash = */ "hashedPassword1",
        /* roles = */ Set.of()
    );

    private static final User USER_2 = new User(
        /* id = */ USER_2_ID,
        /* login = */ "user2",
        /* passwordHash = */ "hashedPassword2",
            Set.of()
    );

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void should_save_new_user() {
        // given:
        UserRepository repository = new DbUserRepository(userEntityRepository);

        // when:
        repository.save(USER_1);

        // then:
        User savedUser = repository.findById(USER_1_ID).orElseThrow();
        assertThat(savedUser).isEqualTo(USER_1);

        // and:
        UserEntity userEntity = testEntityManager.find(UserEntity.class, USER_1_ID.value());
        assertThat(userEntity).isNotNull();
        assertThat(userEntity.getUserId()).isEqualTo(USER_1_ID.value());
        assertThat(userEntity.getLogin()).isEqualTo("user1");
        assertThat(userEntity.getPassword()).isEqualTo("hashedPassword1");
    }

    @Test
    void should_throw_exception_when_save_user_with_existing_id() {
        // given:
        UserRepository repository = new DbUserRepository(userEntityRepository);

        // and:
        testEntityManager.persistAndFlush(
            UserEntity.builder()
                .userId(USER_1_ID.value())
                .login("user1")
                .password("hashedPassword1")
                .build()
        );

        // when:
        var error = assertThatThrownBy(() -> {
            repository.save(USER_1); // trying to save user with the same ID
        });

        // then:
        error.isInstanceOf(RuntimeException.class);
        error.hasMessageContaining("User already exists: %s".formatted(USER_1_ID));
    }

    @Test
    void should_find_user_by_id() {
        // given:
        UserRepository repository = new DbUserRepository(userEntityRepository);

        // and:
        testEntityManager.persistAndFlush(
            UserEntity.builder()
                .userId(USER_1_ID.value())
                .login("user1")
                .password("hashedPassword1")
                .build()
        );

        // when:
        Optional<User> user = repository.findById(USER_1_ID);

        // then:
        assertThat(user).isPresent();
        assertThat(user.get()).isEqualTo(USER_1);
    }

    @Test
    void should_return_empty_optional_when_user_with_given_id_not_exist() {
        // given:
        UserRepository repository = new DbUserRepository(userEntityRepository);

        // and:
        testEntityManager.persistAndFlush(
            UserEntity.builder()
                .userId(USER_1_ID.value())
                .login("user1")
                .password("hashedPassword1")
                .build()
        );

        // when:
        Optional<User> user = repository.findById(USER_2_ID); // USER_2_ID does not exist

        // then:
        assertThat(user).isEmpty();
    }

    @Test
    void should_find_user_by_login() {
        // given:
        UserRepository repository = new DbUserRepository(userEntityRepository);

        // and:
        testEntityManager.persistAndFlush(
            UserEntity.builder()
                .userId(USER_1_ID.value())
                .login("user1")
                .password("hashedPassword1")
                .build()
        );

        // when:
        Optional<User> user = repository.findByLogin("user1");

        // then:
        assertThat(user).isPresent();
        assertThat(user.get()).isEqualTo(USER_1);
    }

    @Test
    void should_return_empty_optional_when_user_with_given_login_not_exist() {
        // given:
        UserRepository repository = new DbUserRepository(userEntityRepository);

        // and:
        testEntityManager.persistAndFlush(
            UserEntity.builder()
                .userId(USER_1_ID.value())
                .login("user1")
                .password("hashedPassword1")
                .build()
        );

        // when:
        Optional<User> user = repository.findByLogin("nonexistentuser"); // login does not exist

        // then:
        assertThat(user).isEmpty();
    }

    @Test
    void should_find_correct_user_when_multiple_users_exist() {
        // given:
        UserRepository repository = new DbUserRepository(userEntityRepository);

        // and:
        testEntityManager.persist(
            UserEntity.builder()
                .userId(USER_1_ID.value())
                .login("user1")
                .password("hashedPassword1")
                .build()
        );
        testEntityManager.persist(
            UserEntity.builder()
                .userId(USER_2_ID.value())
                .login("user2")
                .password("hashedPassword2")
                .build()
        );
        testEntityManager.flush();

        // when:
        Optional<User> user1 = repository.findByLogin("user1");
        Optional<User> user2 = repository.findByLogin("user2");

        // then:
        assertThat(user1).isPresent();
        assertThat(user1.get()).isEqualTo(USER_1);
        assertThat(user2).isPresent();
        assertThat(user2.get()).isEqualTo(USER_2);
    }

}