package com.employeselfservice.dto.response;

import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Data
@Getter
@Setter
@AllArgsConstructor
public class WidgetsDTO {
    private String widgetPrimaryOne;
    private String widgetSecondaryOne;
    private String widgetPrimaryTwo;
    private String widgetSecondaryTwo;
    private String widgetPrimaryThree;
    private String widgetSecondaryThree;

    public WidgetsDTO(){

    }
}
