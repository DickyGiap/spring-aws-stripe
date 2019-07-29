package com.devopsbuddy.test.integration;

import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.repositories.PasswordResetTokenRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by tedonema on 09/04/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PasswordResetTokenIntegrationTest extends AbstractIntegrationTest {

    @Value("${token.expiration.length.minutes}")
    private int expirationTimeInMinutes;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void init() {
        Assert.assertNotEquals(0, expirationTimeInMinutes);
    }

    @Test
    public void testTokenExpirationLength() throws Exception {

        User user = createUser(testName);
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getId());

        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        String token = UUID.randomUUID().toString();

        LocalDateTime expectedTime = now.plusMinutes(expirationTimeInMinutes);

        PasswordResetToken passwordResetToken = createPasswordResetToken(token, user, now);

        LocalDateTime actualTime = passwordResetToken.getExpiryDate();
        Assert.assertNotNull(actualTime);
        Assert.assertEquals(expectedTime, actualTime);
    }

    @Test
    public void testFindTokenByTokenValue() throws Exception {

        User user = createUser(testName);
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        createPasswordResetToken(token, user, now);

        PasswordResetToken retrievedPasswordResetToken = passwordResetTokenRepository.findByToken(token);
        Assert.assertNotNull(retrievedPasswordResetToken);
        Assert.assertNotNull(retrievedPasswordResetToken.getId());
        Assert.assertNotNull(retrievedPasswordResetToken.getUser());

    }

    @Test
    public void testDeleteToken() throws Exception {

        User user = createUser(testName);
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        PasswordResetToken passwordResetToken = createPasswordResetToken(token, user, now);
        long tokenId = passwordResetToken.getId();
        passwordResetTokenRepository.delete(passwordResetToken);

        PasswordResetToken shouldNotExistToken = passwordResetTokenRepository.findById(tokenId).orElse(null);
        Assert.assertNull(shouldNotExistToken);

    }

//    @Test
//    public void testCascadeDeleteFromUserEntity() throws Exception {
//
//        User user = createUser(testName);
//        System.out.println("usr: " + user.getId());
//        String token = UUID.randomUUID().toString();
//        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
//
//        PasswordResetToken passwordResetToken = createPasswordResetToken(token, user, now);
//        Assert.assertNotNull(passwordResetToken);
//        System.out.println("prt: " + passwordResetToken.getId());
//        System.out.println("prt.usr.id: " + passwordResetToken.getUser().getId());
//
//        userRepository.delete(user);
//
//        Set<PasswordResetToken> shouldBeEmpty = passwordResetTokenRepository.findAllByUserId(user.getId());
//        Assert.assertTrue(shouldBeEmpty.isEmpty());
//
//    }

    @Test
    public void testMultipleTokensAreReturnedWhenQueryingByUserId() throws Exception {

        User user = createUser(testName);
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        String token1 = UUID.randomUUID().toString();
        String token2 = UUID.randomUUID().toString();
        String token3 = UUID.randomUUID().toString();

        Set<PasswordResetToken> tokens = new HashSet<>();
        tokens.add(createPasswordResetToken(token1, user, now));
        tokens.add(createPasswordResetToken(token2, user, now));
        tokens.add(createPasswordResetToken(token3, user, now));
        for(PasswordResetToken token : tokens){
            System.out.println(token.getToken());
        }

        passwordResetTokenRepository.saveAll(tokens);

        User foundUser = userRepository.findById(user.getId()).orElse(null);
        Assert.assertNotNull(foundUser);
        Set<PasswordResetToken> actualTokens = passwordResetTokenRepository.findAllByUserId(foundUser.getId());
        for(PasswordResetToken token : actualTokens){
            System.out.println(token.getToken());
        }
        Assert.assertEquals(actualTokens.size(), tokens.size());
        List<String> tokensAsList = tokens.stream().map(PasswordResetToken::getToken).collect(Collectors.toList());
        List<String> actualTokensAsList = actualTokens.stream().map(PasswordResetToken::getToken).collect(
                Collectors.toList());
        Assert.assertEquals(tokensAsList, actualTokensAsList);

    }

    //------------------> Private methods

    private PasswordResetToken createPasswordResetToken(String token, User user, LocalDateTime now) {

        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user, now, expirationTimeInMinutes);
        passwordResetTokenRepository.save(passwordResetToken);
        Assert.assertNotNull(passwordResetToken.getId());
        return passwordResetToken;

    }

}