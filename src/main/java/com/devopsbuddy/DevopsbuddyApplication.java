package com.devopsbuddy;

import com.devopsbuddy.backend.persistence.domain.backend.Role;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.service.PlanService;
import com.devopsbuddy.backend.service.RoleService;
import com.devopsbuddy.backend.service.UserService;
import com.devopsbuddy.enums.PlansEnum;
import com.devopsbuddy.enums.RolesEnum;
import com.devopsbuddy.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class DevopsbuddyApplication implements CommandLineRunner {
    /** The application logger */
    private static final Logger LOG = LoggerFactory.getLogger(DevopsbuddyApplication.class);
    @Autowired
    private UserService userService;

    @Autowired
    private PlanService planService;

    @Autowired
    private RoleService roleService;

    //@Value("${webmaster.username}")
    private String webmasterUsername = "basicUser";

    //@Value("${webmaster.password}")
    private String webmasterPassword = "secret";

    //@Value("${webmaster.email}")
    private String webmasterEmail = "me@example.com";


    public static void main(String[] args) {
        SpringApplication.run(DevopsbuddyApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        LOG.info("Creating Basic and Pro plans in the database...");
        planService.createPlan(PlansEnum.BASIC.getId());
        planService.createPlan(PlansEnum.PRO.getId());

        roleService.createRole(RolesEnum.BASIC.getId());
        roleService.createRole(RolesEnum.PRO.getId());
        roleService.createRole(RolesEnum.ADMIN.getId());


        User user = UserUtils.createUser(webmasterUsername, webmasterEmail);
        user.setPassword(webmasterPassword);
        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(user, new Role(RolesEnum.ADMIN)));
        LOG.debug("Creating user with username {}", user.getUsername());
        userService.createUser(user, PlansEnum.PRO, userRoles);
        LOG.info("User {} created", user.getUsername());
    }

}
