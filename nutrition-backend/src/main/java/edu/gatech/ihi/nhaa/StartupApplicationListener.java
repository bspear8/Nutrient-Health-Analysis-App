package edu.gatech.ihi.nhaa;

import edu.gatech.ihi.nhaa.entity.Role;
import edu.gatech.ihi.nhaa.repository.RoleRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private final RoleRepository roleRepository;

    public StartupApplicationListener(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        prepopulateRoles();
    }

    private void prepopulateRoles() {
        if(roleRepository.findByName("ROLE_USER") == null) {
            Role role = new Role();
            role.setName("ROLE_USER");
            roleRepository.save(role);
        }
    }
}
