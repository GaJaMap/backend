package com.map.gaja.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.map.gaja.client.application.*;
import com.map.gaja.client.application.validator.ClientRequestValidator;
import com.map.gaja.client.infrastructure.file.FileParsingService;
import com.map.gaja.client.infrastructure.file.FileValidator;
import com.map.gaja.client.infrastructure.geocode.Geocoder;
import com.map.gaja.client.presentation.api.ClientDeleteController;
import com.map.gaja.client.presentation.api.ClientSavingController;
import com.map.gaja.client.presentation.api.ClientUpdatingController;
import com.map.gaja.client.presentation.web.WebClientController;
import com.map.gaja.global.authentication.AuthenticationRepository;
import com.map.gaja.group.application.GroupAccessVerifyService;
import com.map.gaja.group.application.GroupService;
import com.map.gaja.group.presentation.api.GroupController;
import com.map.gaja.memo.application.MemoService;
import com.map.gaja.memo.presentation.api.MemoController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
        controllers = {
                GroupController.class,
                ClientDeleteController.class,
                ClientSavingController.class,
                ClientUpdatingController.class,
                WebClientController.class,
                MemoController.class
        }
)
@MockBean(JpaMetamodelMappingContext.class)
public abstract class ControllerTest {
    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper mapper;

    @MockBean
    protected ClientBulkService clientBulkService;

    @MockBean
    protected ClientDeleteService clientDeleteService;

    @MockBean
    protected ClientAccessVerifyService clientAccessVerifyService;

    @MockBean
    protected AuthenticationRepository authenticationRepository;

    @MockBean
    protected GroupAccessVerifyService groupAccessVerifyService;

    @MockBean
    protected ClientRequestValidator clientRequestValidator;

    @MockBean
    protected ClientSavingService clientSavingService;

    @MockBean
    protected ClientUpdatingService clientUpdatingService;

    @MockBean
    protected FileParsingService parsingService;

    @MockBean
    protected GroupService groupService;

    @MockBean
    protected Geocoder geocoder;

    @MockBean
    protected FileValidator fileValidator;

    @MockBean
    protected MemoService memoService;
}
