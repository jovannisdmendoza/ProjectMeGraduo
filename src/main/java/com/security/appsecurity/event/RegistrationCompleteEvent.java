package com.security.appsecurity.event;


import lombok.*;
import org.springframework.context.ApplicationEvent;


import com.security.appsecurity.Persistence.Entity.UseEntity;

/**
 * @author Sampson Alfred
 */
@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {
    private  UseEntity user;
    private String applicationUrl;

    public RegistrationCompleteEvent(UseEntity user, String applicationUrl) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
    }
}